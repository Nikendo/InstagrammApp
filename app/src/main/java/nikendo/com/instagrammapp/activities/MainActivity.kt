package nikendo.com.instagrammapp.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import kotlinx.android.synthetic.main.activity_home.*
import nikendo.com.instagrammapp.BaseActivity
import nikendo.com.instagrammapp.R
import nikendo.com.instagrammapp.utils.FirebaseHelper
import nikendo.com.instagrammapp.utils.ValueEventListenerAdapter

class MainActivity: BaseActivity(0) {
    private val TAG = "MainActivity"
    private lateinit var mFirebase: FirebaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        Log.d(TAG, "onCreate")
        setupBottomNavigation()

        mFirebase = FirebaseHelper(this)
        tvSingOut.setOnClickListener{
            mFirebase.auth.signOut()
        }
        mFirebase.auth.addAuthStateListener {
            if (it.currentUser == null) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } else {
                if (mFirebase.auth.currentUser != null) {
                    mFirebase.database.child("feed-posts").child(mFirebase.auth.currentUser!!.uid)
                            .addValueEventListener(ValueEventListenerAdapter {
                                val posts = it.children.map { it.getValue(FeedPost::class.java)!! }
                                Log.d(TAG, "feedPosts: ${posts.first().timestampDate()}")
                            })
                }
            }
        }

    }

    override fun onStart() {
        super.onStart()
        if (mFirebase.auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}