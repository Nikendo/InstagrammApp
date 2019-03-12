package nikendo.com.instagrammapp.views

import android.support.v7.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.support.v4.app.DialogFragment
import android.os.Bundle
import kotlinx.android.synthetic.main.dialog_password.view.*
import nikendo.com.instagrammapp.R

class PasswordDialog: DialogFragment() {

    private val TAG = "PasswordDialog"

    private lateinit var mListener: Listener

    interface Listener {
        fun onPasswordConfirm(password: String)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mListener = context as Listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = activity!!.layoutInflater.inflate(R.layout.dialog_password, null)
        return AlertDialog
                .Builder(context!!)
                .setView(view)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    mListener.onPasswordConfirm(view.etPasswordInput.text.toString())
                }
                .setNegativeButton(android.R.string.cancel) { _, _ ->
                    // do nothing
                }
                .setTitle(R.string.please_enter_password)
                .create()
    }

}