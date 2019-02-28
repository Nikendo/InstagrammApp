package nikendo.com.instagrammapp

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log

class SearchActivity: BaseActivity() {

    private val TAG = "SearchActivity"

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "onCreate")
        setupBottomNavigation()
    }


}