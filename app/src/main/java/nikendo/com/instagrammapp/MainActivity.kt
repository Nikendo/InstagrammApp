package nikendo.com.instagrammapp

import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth

class MainActivity : BaseActivity(0) {

    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        Log.d(TAG, "onCreate")
        setupBottomNavigation()

        val auth = FirebaseAuth.getInstance()
        auth.signInWithEmailAndPassword("pravdomir42@gmail.com", "UAZ3303Fire")
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        Log.d(TAG, "signIn: success")
                    } else {
                        Log.e(TAG, "signIn: failure", it.exception)
                    }
                }
    }

}
