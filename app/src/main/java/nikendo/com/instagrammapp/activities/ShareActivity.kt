package nikendo.com.instagrammapp.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_share.*
import nikendo.com.instagrammapp.BaseActivity
import nikendo.com.instagrammapp.R
import nikendo.com.instagrammapp.utils.CameraHelper
import nikendo.com.instagrammapp.utils.FirebaseHelper
import nikendo.com.instagrammapp.utils.GlideApp

class ShareActivity : BaseActivity(2) {

    private val TAG = "ShareActivity"
    private lateinit var mCamera: CameraHelper
    private lateinit var mFirebase: FirebaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)
        Log.d(TAG, "onCreate")

        mFirebase = FirebaseHelper(this)
        mCamera = CameraHelper(this)
        mCamera.takeCameraPicture()

        ivBack.setOnClickListener { finish() }
        tvShare.setOnClickListener { share() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == mCamera.REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                GlideApp.with(this).load(mCamera.imageUri).centerCrop().into(ivPost)
            } else {
                finish()
            }
        }
    }

    private fun share() {
        val imageUri = mCamera.imageUri
        if (mCamera.imageUri != null) {
            // upload image to user folder <- storage
            val uid = mFirebase.auth.currentUser!!.uid
            mFirebase.storage.child("users").child(uid)
                    .child("images").child(imageUri!!.lastPathSegment).putFile(imageUri)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            mFirebase.database.child("images").child(uid).push()
                                    .setValue(it.result.downloadUrl.toString())
                                    .addOnCompleteListener {
                                        if (it.isSuccessful) {
                                            startActivity(Intent(this, ProfileActivity::class.java))
                                            finish()
                                        } else {
                                            showToast(it.exception!!.message!!)
                                        }
                                    }
                        } else {
                            showToast(it.exception!!.message!!)
                        }
                    }
            // add image to user images <- db
        }
    }
}