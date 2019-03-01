package nikendo.com.instagrammapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_login.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener

class LoginActivity : AppCompatActivity(), KeyboardVisibilityEventListener, TextWatcher {

    private val TAG = "LoginActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        Log.d(TAG, "onCreate")

        KeyboardVisibilityEvent.setEventListener(this, this)
        bLogin.isEnabled = false
        etEmailInput.addTextChangedListener(this)
        etPasswordInput.addTextChangedListener(this)
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
        bLogin.isEnabled =
                        etEmailInput.text.toString().isNotEmpty() &&
                        etPasswordInput.text.toString().isNotEmpty()
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
}
