package nikendo.com.instagrammapp.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.firebase.database.ServerValue
import kotlinx.android.synthetic.main.activity_share.*
import nikendo.com.instagrammapp.BaseActivity
import nikendo.com.instagrammapp.R
import nikendo.com.instagrammapp.models.User
import nikendo.com.instagrammapp.utils.CameraHelper
import nikendo.com.instagrammapp.utils.FirebaseHelper
import nikendo.com.instagrammapp.utils.GlideApp
import nikendo.com.instagrammapp.utils.ValueEventListenerAdapter
import java.util.*

class ShareActivity : BaseActivity(2) {

    private val TAG = "ShareActivity"
    private lateinit var mCamera: CameraHelper
    private lateinit var mFirebase: FirebaseHelper
    private lateinit var mUser: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)
        Log.d(TAG, "onCreate")

        mFirebase = FirebaseHelper(this)
        mCamera = CameraHelper(this)
        mCamera.takeCameraPicture()

        ivBack.setOnClickListener { finish() }
        tvShare.setOnClickListener { share() }

        mFirebase.currentUserReference().addValueEventListener(ValueEventListenerAdapter {
            mUser = it.asUser()!!
        })
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
            val uid = mFirebase.auth.currentUser!!.uid
            mFirebase.storage.child("users").child(uid).child("images")
                    .child(imageUri!!.lastPathSegment).putFile(imageUri).addOnCompleteListener {
                        if (it.isSuccessful) {
                            val imageDownloadUrl = it.result.downloadUrl.toString()
                            mFirebase.database.child("images").child(uid).push()
                                    .setValue(imageDownloadUrl)
                                    .addOnCompleteListener {
                                        if (it.isSuccessful) {
                                            mFirebase.database.child("feed-posts").child(uid)
                                                    .push()
                                                    .setValue(makeFeedPost(uid, imageDownloadUrl))
                                                    .addOnCompleteListener {
                                                        if (it.isSuccessful) {
                                                            startActivity(Intent(this, ProfileActivity::class.java))
                                                            finish()
                                                        }
                                                    }
                                        } else {
                                            showToast(it.exception!!.message!!)
                                        }
                                    }
                        } else {
                            showToast(it.exception!!.message!!)
                        }
                    }
        }
    }

    private fun makeFeedPost(uid: String, imageDownloadUrl: String): FeedPost {
        return FeedPost(
                uid = uid,
                username = mUser.username,
                image = imageDownloadUrl,
                caption = etCaption.text.toString(),
                photo = mUser.photo
        )
    }
}

data class FeedPost(val uid: String = "", val username: String = "",
                    val image: String = "", val likesCount: Int = 0, val commentsCount: Int = 0,
                    val caption: String = "", val comments: List<Comment> = emptyList(),
                    val timestamp: Any = ServerValue.TIMESTAMP, val photo: String? = null) {
    fun timestampDate(): Date = Date(timestamp as Long)
}

data class Comment(val uid: String,
                   val username: String,
                   val text: String)
