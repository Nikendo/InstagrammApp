package nikendo.com.instagrammapp

import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class LikesActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setMenuOptions()
    }

    private fun setMenuOptions() {
        bottomNavigationView.setIconSize(29f, 29f)
        bottomNavigationView.setTextVisibility(false)
        bottomNavigationView.enableItemShiftingMode(false)
        bottomNavigationView.enableShiftingMode(false)
        bottomNavigationView.enableAnimation(false)
        for (i in 0 until bottomNavigationView.menu.size()) {
            bottomNavigationView.setIconTintList(i, null)
        }
    }
}