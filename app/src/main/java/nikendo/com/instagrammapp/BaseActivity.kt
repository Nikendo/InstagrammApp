package nikendo.com.instagrammapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*

abstract class BaseActivity(val navNumber: Int): AppCompatActivity() {

    private val TAG = "Base Activity"

    fun setupBottomNavigation() {
        bottomNavigationView.setIconSize(29f, 29f)
        bottomNavigationView.setTextVisibility(false)
        bottomNavigationView.enableItemShiftingMode(false)
        bottomNavigationView.enableShiftingMode(false)
        bottomNavigationView.enableAnimation(false)
        for (i in 0 until bottomNavigationView.menu.size()) {
            bottomNavigationView.setIconTintList(i, null)
        }
        bottomNavigationView.setOnNavigationItemSelectedListener {
            val nextActivity =
                    when (it.itemId) {
                        R.id.nav_item_home -> MainActivity::class.java
                        R.id.nav_item_search -> SearchActivity::class.java
                        R.id.nav_item_share -> ShareActivity::class.java
                        R.id.nav_item_likes -> LikesActivity::class.java
                        R.id.nav_item_profile -> ProfileActivity::class.java
                        else -> {
                            Log.e(TAG, "unknown nav item clicked $it")
                            null
                        }
                    }
            if (nextActivity != null) {
                val intent = Intent(this, nextActivity)
                intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
                startActivity(intent)
                overridePendingTransition(0, 0)
                true
            } else {
                false
            }
        }
        bottomNavigationView.menu.getItem(navNumber).isChecked = true
    }
}