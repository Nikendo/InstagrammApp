package nikendo.com.instagrammapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.TextView
import com.google.firebase.auth.EmailAuthProvider
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.activity_edit_profile.view.*
import nikendo.com.instagrammapp.activities.loadUserPhoto
import nikendo.com.instagrammapp.activities.showToast
import nikendo.com.instagrammapp.activities.toStringOrNull
import nikendo.com.instagrammapp.models.User
import nikendo.com.instagrammapp.utils.CameraPictureTaker
import nikendo.com.instagrammapp.utils.FirebaseHelper
import nikendo.com.instagrammapp.utils.ValueEventListenerAdapter
import nikendo.com.instagrammapp.views.PasswordDialog

class EditProfileActivity : AppCompatActivity(), PasswordDialog.Listener {

    private val TAG = "EditProfileActivity "

    private lateinit var mUser: User
    private lateinit var mPendingUser: User
    private lateinit var mFirebaseHelper: FirebaseHelper
    private lateinit var cameraPictureTaker: CameraPictureTaker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        Log.d(TAG, "onCreate")

        cameraPictureTaker = CameraPictureTaker(this)

        imageClose.setOnClickListener {
            finish()
        }
        imageSave.setOnClickListener {
            updateProfile()
        }
        tvChangePhoto.setOnClickListener {
            cameraPictureTaker.takeCameraPicture()
        }
        mFirebaseHelper = FirebaseHelper(this)

        mFirebaseHelper.currentUserReference()
                .addListenerForSingleValueEvent(ValueEventListenerAdapter {
                    mUser = it.getValue(User::class.java)!!
                    etNameInput.setText(mUser.name)
                    etUsernameInput.setText(mUser.username)
                    etWebsiteInput.setText(mUser.website)
                    etBioInput.setText(mUser.bio)
                    etEmailInput.setText(mUser.email)
                    etPhoneInput.setText(mUser.phone)
                    editProfileToolBar.tvUserName.text = mUser.username
                    imageProfile.loadUserPhoto(mUser.photo)
                })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == cameraPictureTaker.REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            mFirebaseHelper.uploadUserPhoto(cameraPictureTaker.imageUri!!) {
                val photoUrl = it.downloadUrl.toString()
                mFirebaseHelper.updateUserPhoto(photoUrl) {
                    mUser = mUser.copy(photo = photoUrl)
                    imageProfile.loadUserPhoto(mUser.photo)
                }
            }
        }
    }

    override fun onPasswordConfirm(password: String) {
        if (password.isNotEmpty()) {
            val credential = EmailAuthProvider.getCredential(mUser.email, password)
            mFirebaseHelper.reauthenticate(credential) {
                mFirebaseHelper.updateEmail(mPendingUser.email) {
                    updateUser(mPendingUser)
                }
            }
        } else {
            showToast("You should enter your password")
        }
    }

    fun updateProfile() {
        mPendingUser = readInputs()
        val error = validate(mPendingUser)
        if (error == null) {
            if (mPendingUser.email == mUser.email) {
                updateUser(mPendingUser)
            } else {
                PasswordDialog().show(supportFragmentManager, "password dialog")
            }
        } else {
            showToast(error)
        }
    }

    private fun readInputs(): User {
        return User(
                name = etNameInput.text.toString(),
                username = etUsernameInput.text.toString(),
                website = etWebsiteInput.text.toStringOrNull(),
                bio = etBioInput.text.toStringOrNull(),
                email = etEmailInput.text.toString(),
                phone = etPhoneInput.text.toStringOrNull()
        )
    }

    private fun updateUser(user: User) {
        val updatesMap = mutableMapOf<String, Any?>()
        if (user.name != mUser.name) updatesMap["name"] = user.name
        if (user.username != mUser.username) updatesMap["username"] = user.username
        if (user.website != mUser.website) updatesMap["website"] = user.website
        if (user.bio != mUser.bio) updatesMap["bio"] = user.bio
        if (user.email != mUser.email) updatesMap["email"] = user.email
        if (user.phone != mUser.phone) updatesMap["phone"] = user.phone
        mFirebaseHelper.updateUser(updatesMap) {
            showToast("Profile saved")
            finish()
        }
    }

    private fun validate(user: User): String? =
            when {
                user.name.isEmpty() -> "Please enter name"
                user.username.isEmpty() -> "Please enter username"
                user.email.isEmpty() -> "Please enter email"
                else -> null
            }
}
