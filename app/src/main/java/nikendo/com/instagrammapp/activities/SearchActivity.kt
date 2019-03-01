package nikendo.com.instagrammapp.activities

import android.os.Bundle
import android.util.Log
import nikendo.com.instagrammapp.BaseActivity
import nikendo.com.instagrammapp.R

class SearchActivity: BaseActivity(1) {

    private val TAG = "SearchActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        Log.d(TAG, "onCreate")
        setupBottomNavigation()
    }


}