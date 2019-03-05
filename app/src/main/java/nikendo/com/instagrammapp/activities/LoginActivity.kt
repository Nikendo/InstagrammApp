package nikendo.com.instagrammapp.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener
import nikendo.com.instagrammapp.R

class LoginActivity : AppCompatActivity(), KeyboardVisibilityEventListener, TextWatcher, View.OnClickListener {

    private val TAG = "LoginActivity"
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        Log.d(TAG, "onCreate")

        KeyboardVisibilityEvent.setEventListener(this, this)
        bLogin.isEnabled = false
        etEmailInput.addTextChangedListener(this)
        etPasswordInput.addTextChangedListener(this)
        bLogin.setOnClickListener(this)
        tvCreateAccount.setOnClickListener(this)

        mAuth = FirebaseAuth.getInstance()
    }

    override fun onClick(v: View) {
        when(v.id) {
            R.id.bLogin -> {
                val email = etEmailInput.text.toString()
                val password = etPasswordInput.text.toString()
                if (validate(email, password)) {
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                        if (it.isSuccessful) {
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        }
                    }
                } else {
                    showToast("Please enter email and password")
                }
            }
            R.id.tvCreateAccount -> {
                startActivity(Intent(this, RegisterActivity::class.java))
            }
        }
    }

    override fun onVisibilityChanged(isKeyboardOpen: Boolean) {
        if (isKeyboardOpen) {
            scrollView.scrollTo(0, scrollView.bottom)
            tvCreateAccount.visibility = View.GONE
        } else {
            scrollView.scrollTo(0, scrollView.top)
            tvCreateAccount.visibility = View.VISIBLE
        }
    }

    override fun afterTextChanged(s: Editable?) {
        bLogin.isEnabled = validate(etEmailInput.text.toString(), etPasswordInput.text.toString())
    }

    private fun validate(email: String, password: String) =
            email.isNotEmpty() && password.isNotEmpty()

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
}
