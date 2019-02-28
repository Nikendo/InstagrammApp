package nikendo.com.instagrammapp

import android.os.Bundle
import android.util.Log

class ShareActivity: BaseActivity(2) {

    private val TAG = "ShareActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupBottomNavigation()
        Log.d(TAG, "onCreate")
    }

}