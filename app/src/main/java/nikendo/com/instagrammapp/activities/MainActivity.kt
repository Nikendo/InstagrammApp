package nikendo.com.instagrammapp.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_home.*
import nikendo.com.instagrammapp.BaseActivity
import nikendo.com.instagrammapp.R

class MainActivity : BaseActivity(0) {

    private val TAG = "MainActivity"
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        Log.d(TAG, "onCreate")
        setupBottomNavigation()

        mAuth = FirebaseAuth.getInstance()
//        auth.signInWithEmailAndPassword("pravdomir42@gmail.com", "UAZ3303Fire")
//                .addOnCompleteListener {
//                    if (it.isSuccessful) {
//                        Log.d(TAG, "signIn: success")
//                    } else {
//                        Log.e(TAG, "signIn: failure", it.exception)
//                    }
//                }

        tvSingOut.setOnClickListener{
            mAuth.signOut()
        }
        mAuth.addAuthStateListener {
            if (it.currentUser == null) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (mAuth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

}
