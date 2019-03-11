package nikendo.com.instagrammapp.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.fragment_register_email.*
import kotlinx.android.synthetic.main.fragment_register_namepass.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener
import nikendo.com.instagrammapp.R
import nikendo.com.instagrammapp.models.User

class RegisterActivity : AppCompatActivity(), EmailFragment.Listener, NamePassFragment.Listener {

    private val TAG = "RegisterActivity"

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference

    private var mEmail: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        Log.d(TAG, "onCreate")

        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance().reference

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().add(R.id.frameLayout, EmailFragment()).commit()
        }
    }

    override fun onNext(email: String) {
        if (email.isNotEmpty()) {
            mEmail = email
            mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener {
                if (it.isSuccessful) {
                    if (it.result.signInMethods?.isEmpty() != false) {
                        supportFragmentManager.beginTransaction().replace(R.id.frameLayout, NamePassFragment())
                                .addToBackStack(null).commit()
                    } else {
                        showToast("This email already exists")
                    }
                } else {
                    showToast(it.exception!!.message!!)
                }
            }
        } else {
            showToast("Please enter email")
        }
    }

    override fun onRegister(fullName: String, pass: String) {
        if (fullName.isNotEmpty() && pass.isNotEmpty()) {
            val email = mEmail
            if (email != null) {
                mAuth.createUserWithEmailAndPassword(email, pass)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                val user = mkUser(fullName, email)
                                val reference = mDatabase.child("users").child(it.result.user.uid)
                                reference.setValue(user).addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        startHomeActivity()
                                    } else {
                                        unknowRegisterError(it)
                                    }
                                }
                            } else {
                                unknowRegisterError(it)
                            }
                        }
            } else {
                Log.e(TAG, "onRegister: Email is null")
                showToast("Please neter email")
                supportFragmentManager.popBackStack()
            }
        } else {
            showToast("Please enter full name and password")
        }
    }

    private fun unknowRegisterError(it: Task<out Any>) {
        Log.e(TAG, "Failed to create user profile", it.exception)
        showToast("Something wrong happened. Please try later")
    }

    private fun startHomeActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun mkUser(fullName: String, email: String): User {
        val username = mkUsername(fullName)
        return User(fullName, username, email = email)
    }

    private fun mkUsername(fullName: String): String {
        return fullName.toLowerCase().replace(" ", ".")
    }
}

class EmailFragment : Fragment() {

    private lateinit var mListener: Listener

    interface Listener {
        fun onNext(email: String)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_register_email, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        coordinateBtnAndInputs(bNext, etEmailInput)
        bNext.setOnClickListener {
            val email = etEmailInput.text.toString()
            mListener.onNext(email)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener = context as Listener
    }
}

class NamePassFragment : Fragment() {

    private lateinit var mListener: Listener

    interface Listener {
        fun onRegister(fullName: String, pass: String)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_register_namepass, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        coordinateBtnAndInputs(bRegister, etFullNameInput, etPasswordInput)
        bRegister.setOnClickListener {
            val fullName = etFullNameInput.text.toString()
            val pass = etPasswordInput.text.toString()
            mListener.onRegister(fullName, pass)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener = context as Listener
    }
}