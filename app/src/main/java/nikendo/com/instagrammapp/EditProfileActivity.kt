 package nikendo.com.instagrammapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_edit_profile.*
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
        val user = mAuth.currentUser
        val database = FirebaseDatabase.getInstance().reference
        database.child("users").child(user!!.uid).addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(data: DataSnapshot) {
                val user = data.getValue(User ::class.java)
                etNameInput.setText(user!!.name, TextView.BufferType.EDITABLE)
                etUsernameInput.setText(user.username, TextView.BufferType.EDITABLE)
                etWebsiteInput.setText(user.website, TextView.BufferType.EDITABLE)
                etBioInput.setText(user.bio, TextView.BufferType.EDITABLE)
                etEmailInput.setText(user.email, TextView.BufferType.EDITABLE)
                etPhoneInput.setText(user.phone, TextView.BufferType.EDITABLE)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "onCancelled: ", error.toException())
            }
        })
    }
}
