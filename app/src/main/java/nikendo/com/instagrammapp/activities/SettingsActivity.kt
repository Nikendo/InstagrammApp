package nikendo.com.instagrammapp.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_profile_settings.*
import nikendo.com.instagrammapp.R
import nikendo.com.instagrammapp.utils.FirebaseHelper

class SettingsActivity: AppCompatActivity() {
    private val TAG = "SettingsActivity"

    lateinit var mFirebase: FirebaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_settings)
        Log.d(TAG, "onCreate")
        ivSignOut.setOnClickListener {
            mFirebase.auth.signOut()
        }
        ivBack.setOnClickListener { onBackPressed() }

        mFirebase = FirebaseHelper(this)
    }
}
