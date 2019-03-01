 package nikendo.com.instagrammapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.activity_edit_profile.view.*
import nikendo.com.instagrammapp.activities.ValueEventListenerAdapter
import nikendo.com.instagrammapp.models.User

 class EditProfileActivity: AppCompatActivity() {

    private val TAG = "EditProfileActivity "
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        Log.d(TAG, "onCreate")

        imageClose.setOnClickListener {
            finish()
        }

        mAuth = FirebaseAuth.getInstance()
        val database = FirebaseDatabase.getInstance().reference
        database.child("users").child(mAuth.currentUser!!.uid).addListenerForSingleValueEvent( ValueEventListenerAdapter {
                val user = it .getValue(User ::class.java)
                etNameInput.setText(user!!.name, TextView.BufferType.EDITABLE)
                etUsernameInput.setText(user.username, TextView.BufferType.EDITABLE)
                etWebsiteInput.setText(user.website, TextView.BufferType.EDITABLE)
                etBioInput.setText(user.bio, TextView.BufferType.EDITABLE)
                etEmailInput.setText(user.email, TextView.BufferType.EDITABLE)
                etPhoneInput.setText(user.phone, TextView.BufferType.EDITABLE)
                editProfileToolBar.tvUserName.text = user.username
        })
    }
}
