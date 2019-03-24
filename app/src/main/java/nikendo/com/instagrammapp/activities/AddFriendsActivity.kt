package nikendo.com.instagrammapp.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import nikendo.com.instagrammapp.R

class AddFriendsActivity: AppCompatActivity() {
    private val TAG = "AddFriendsActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_friends_activity)
        Log.d(TAG, "onCreate")

    }
}
