package nikendo.com.instagrammapp.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_share.*
import nikendo.com.instagrammapp.BaseActivity
import nikendo.com.instagrammapp.R
import nikendo.com.instagrammapp.utils.CameraHelper
import nikendo.com.instagrammapp.utils.GlideApp

class ShareActivity: BaseActivity(2) {

    private val TAG = "ShareActivity"
    private lateinit var mCameraHelper: CameraHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)
        Log.d(TAG, "onCreate")

        mCameraHelper = CameraHelper(this)
        mCameraHelper.takeCameraPicture()

        ivBack.setOnClickListener { finish() }
        tvShare.setOnClickListener {  }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == mCameraHelper.REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            GlideApp.with(this).load(mCameraHelper.imageUri).centerCrop().into(ivPost)
        }
    }

}