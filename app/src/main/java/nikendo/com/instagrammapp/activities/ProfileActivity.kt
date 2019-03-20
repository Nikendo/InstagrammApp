package nikendo.com.instagrammapp.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_profile.*
import nikendo.com.instagrammapp.BaseActivity
import nikendo.com.instagrammapp.EditProfileActivity
import nikendo.com.instagrammapp.R
import nikendo.com.instagrammapp.models.User
import nikendo.com.instagrammapp.utils.FirebaseHelper
import nikendo.com.instagrammapp.utils.ValueEventListenerAdapter

class ProfileActivity: BaseActivity(4) {

    private val TAG = "ProfileActivity"
    private lateinit var mFirebaseHelper: FirebaseHelper
    private lateinit var mUser: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        Log.d(TAG, "onCreate")
        setupBottomNavigation()

        bEditProfile.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
        }

        mFirebaseHelper = FirebaseHelper(this)
        mFirebaseHelper.currentUserReference().addValueEventListener(ValueEventListenerAdapter {
            mUser = it.getValue(User::class.java)!!
            imageProfile.loadUserPhoto(mUser.photo)
            tvUserName.text = mUser.username
        })
    }
}