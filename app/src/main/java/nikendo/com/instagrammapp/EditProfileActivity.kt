package nikendo.com.instagrammapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.activity_edit_profile.view.*
import nikendo.com.instagrammapp.activities.ValueEventListenerAdapter
import nikendo.com.instagrammapp.activities.showToast
import nikendo.com.instagrammapp.models.User

class EditProfileActivity : AppCompatActivity() {

    private val TAG = "EditProfileActivity "
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference
    private lateinit var mUser: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        Log.d(TAG, "onCreate")

        imageClose.setOnClickListener {
            finish()
        }
        imageSave.setOnClickListener {
            updateProfile()
            finish()
        }

        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance().reference
        mDatabase.child("users").child(mAuth.currentUser!!.uid).addListenerForSingleValueEvent(ValueEventListenerAdapter {
            mUser = it.getValue(User::class.java)!!
            etNameInput.setText(mUser.name, TextView.BufferType.EDITABLE)
            etUsernameInput.setText(mUser.username, TextView.BufferType.EDITABLE)
            etWebsiteInput.setText(mUser.website, TextView.BufferType.EDITABLE)
            etBioInput.setText(mUser.bio, TextView.BufferType.EDITABLE)
            etEmailInput.setText(mUser.email, TextView.BufferType.EDITABLE)
            etPhoneInput.setText(mUser.phone, TextView.BufferType.EDITABLE)
            editProfileToolBar.tvUserName.text = mUser.username
        })
    }

    fun updateProfile() {
        val user = User(
                name = etNameInput.text.toString(),
                username = etUsernameInput.text.toString(),
                website = etWebsiteInput.text.toString(),
                bio = etBioInput.text.toString(),
                email = etEmailInput.text.toString(),
                phone = etPhoneInput.text.toString()
        )
        val error = validate(user)
        if (error == null) {
            if (user.email == mUser.email) {
                updateUser(user)
            } else {
                // re-authenticate
                // update email in auth
                // update user
            }
        } else {
            showToast(error)
        }
    }

    private fun updateUser(user: User) {
        val updatesMap = mutableMapOf<String, Any>()
        if (user.name != mUser.name) updatesMap["name"] = user.name
        if (user.username != mUser.username) updatesMap["username"] = user.username
        if (user.website != mUser.website) updatesMap["website"] = user.website
        if (user.bio != mUser.bio) updatesMap["bio"] = user.bio
        if (user.email != mUser.email) updatesMap["email"] = user.email
        if (user.phone != mUser.phone) updatesMap["phone"] = user.phone
        mDatabase.child("users").child(mAuth.currentUser!!.uid).updateChildren(updatesMap)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        showToast("Profile save")
                        finish()
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
