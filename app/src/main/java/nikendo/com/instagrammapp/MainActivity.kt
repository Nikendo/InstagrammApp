package nikendo.com.instagrammapp

import android.os.Bundle
import android.util.Log

class MainActivity : BaseActivity(0) {

    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "onCreate")
        setupBottomNavigation()
    }

}
