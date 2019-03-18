package nikendo.com.instagrammapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.util.Log
import android.widget.TextView
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.activity_edit_profile.view.*
import nikendo.com.instagrammapp.activities.ValueEventListenerAdapter
import nikendo.com.instagrammapp.activities.loadUserPhoto
import nikendo.com.instagrammapp.activities.showToast
import nikendo.com.instagrammapp.activities.toStringOrNull
import nikendo.com.instagrammapp.models.User
import nikendo.com.instagrammapp.views.PasswordDialog
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class EditProfileActivity : AppCompatActivity(), PasswordDialog.Listener {

    private val TAG = "EditProfileActivity "
    private val TAKE_PICTURE_REQUEST_CODE = 1

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference
    private lateinit var mStorage: StorageReference
    private lateinit var mUser: User
    private lateinit var mPendingUser: User
    private lateinit var mImageUri: Uri


    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        Log.d(TAG, "onCreate")

        imageClose.setOnClickListener {
            finish()
        }
        imageSave.setOnClickListener {
            updateProfile()
        }
        tvChangePhoto.setOnClickListener {
            takeCameraPicture()
        }
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance().reference
        mStorage = FirebaseStorage.getInstance().reference

        mDatabase.child("users").child(mAuth.currentUser!!.uid)
                .addListenerForSingleValueEvent(ValueEventListenerAdapter {
                    mUser = it.getValue(User::class.java)!!
                    etNameInput.setText(mUser.name, TextView.BufferType.EDITABLE)
                    etUsernameInput.setText(mUser.username, TextView.BufferType.EDITABLE)
                    etWebsiteInput.setText(mUser.website, TextView.BufferType.EDITABLE)
                    etBioInput.setText(mUser.bio, TextView.BufferType.EDITABLE)
                    etEmailInput.setText(mUser.email, TextView.BufferType.EDITABLE)
                    etPhoneInput.setText(mUser.phone, TextView.BufferType.EDITABLE)
                    editProfileToolBar.tvUserName.text = mUser.username
                    imageProfile.loadUserPhoto(mUser.photo)
                    //imageProfile.loadUserPhoto("https://avatars.mds.yandex.net/get-pdb/33827/203472834-toyota-kikai-concept-1467734605.5/s1200")
                })
    }

    private fun takeCameraPicture() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            val imageFile = createImageFile()
            mImageUri = FileProvider.getUriForFile(
                    this,
                    "nikendo.com.instagrammapp.fileprovider",
                    imageFile
            )
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri)
            startActivityForResult(intent, TAKE_PICTURE_REQUEST_CODE)
        }
    }

    private fun createImageFile(): File {
        return File.createTempFile(
                "JPEG_${timeStamp}_",
                ".jpg",
                getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == TAKE_PICTURE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val uid = mAuth.currentUser!!.uid
            // upload image to firebase storage
            mStorage.child("users/$uid/photo").putFile(mImageUri).addOnCompleteListener {
                if (it.isSuccessful) {
                    val photoUrl = it.result.downloadUrl.toString()
                    mDatabase.child("users/$uid/photo").setValue(photoUrl)
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    mUser = mUser.copy(phone = photoUrl)
                                    imageProfile.loadUserPhoto(mUser.photo)
                                    //imageProfile.loadUserPhoto("https://avatars.mds.yandex.net/get-pdb/33827/203472834-toyota-kikai-concept-1467734605.5/s1200")
                                } else {
                                    showToast(it.exception!!.message!!)
                                }
                            }
                } else {
                    showToast(it.exception!!.message!!)
                }
            }
            // save image to database user.photo
        }
    }

    override fun onPasswordConfirm(password: String) {
        if (password.isNotEmpty()) {
            val credential = EmailAuthProvider.getCredential(mUser.email, password)
            mAuth.currentUser!!.reauthenticate(credential) {
                mAuth.currentUser!!.updateEmail(mPendingUser.email) {
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
        mDatabase.updateUser(mAuth.currentUser!!.uid, updatesMap) {
            showToast("Profile saved")
            finish()
        }
    }

    private fun FirebaseUser.reauthenticate(credential: AuthCredential, onSuccess: () -> Unit) {
        reauthenticate(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                onSuccess()
            } else {
                showToast(it.exception!!.message!!)
            }
        }
    }

    private fun FirebaseUser.updateEmail(email: String, onSuccess: () -> Unit) {
        updateEmail(email).addOnCompleteListener {
            if (it.isSuccessful) {
                onSuccess()
            } else {
                showToast(it.exception!!.message!!)
            }
        }
    }

    private fun DatabaseReference.updateUser(uid: String, updates: Map<String, Any?>, onSuccess: () -> Unit) {
        child("users").child(uid).updateChildren(updates)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        onSuccess()
                    } else {
                        showToast(it.exception!!.message!!)
                    }
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
