package nikendo.com.instagrammapp.activities

import android.os.Bundle
import android.util.Log
import nikendo.com.instagrammapp.BaseActivity
import nikendo.com.instagrammapp.R

class LikesActivity: BaseActivity(3) {

    private val TAG = "LikesActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        Log.d(TAG, "onCreate")
        setupBottomNavigation()
    }

}