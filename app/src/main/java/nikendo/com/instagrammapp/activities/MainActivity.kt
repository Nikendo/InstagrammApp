package nikendo.com.instagrammapp.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.feed_item.view.*
import nikendo.com.instagrammapp.BaseActivity
import nikendo.com.instagrammapp.R
import nikendo.com.instagrammapp.models.FeedPost
import nikendo.com.instagrammapp.utils.FirebaseHelper
import nikendo.com.instagrammapp.utils.ValueEventListenerAdapter

class MainActivity : BaseActivity(0), FeedAdapter.Listener {

    private val TAG = "MainActivity"
    private lateinit var mFirebase: FirebaseHelper
    private lateinit var mAdapter: FeedAdapter
    private var mLikesListeners: Map<String, ValueEventListener> = emptyMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        Log.d(TAG, "onCreate")
        setupBottomNavigation()

        mFirebase = FirebaseHelper(this)

        mFirebase.auth.addAuthStateListener {
            if (it.currentUser == null) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }


    }

    override fun onStart() {
        super.onStart()
        val currentUser = mFirebase.auth.currentUser
        if (currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {
            mFirebase.database.child("feed-posts").child(currentUser.uid)
                    .addValueEventListener(ValueEventListenerAdapter {
                        val posts = it.children.map { it.asFeedPost()!! }
                                .sortedByDescending { it.timestampDate() }
                        mAdapter = FeedAdapter(this, posts)
                        rvFeed.adapter = mAdapter
                        rvFeed.layoutManager = LinearLayoutManager(this)
                    })
        }
    }

    override fun toggleLike(postId: String) {
        Log.d(TAG, "toggleLike: $postId")
        val reference = mFirebase.database.child("likes").child(postId).child(mFirebase.currentUid())
        reference.addListenerForSingleValueEvent(ValueEventListenerAdapter{
            reference.setValueTrueOrRemove(!it.exists())
        })
    }

    override fun loadLikes(postId: String, position: Int) {
        fun createListener() = mFirebase.database.child("likes").child(postId)
                .addValueEventListener(ValueEventListenerAdapter {
                    val userLikes = it.children.map {it.key}.toSet()
                    val postLikes = FeedPostLikes(
                            userLikes.size,
                            userLikes.contains(mFirebase.currentUid())
                    )
                    mAdapter.updatePostLikes(position, postLikes)
        })

        val createNewListener = mLikesListeners[postId] == null
        Log.d(TAG, "toggleLike: $position $createNewListener")
        if (createNewListener) {
            mLikesListeners += (postId to createListener())
            Log.d(TAG, "toggleLike: newListener")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mLikesListeners.values.forEach { mFirebase.database.removeEventListener(it) }
    }
}

data class FeedPostLikes(val likesCount: Int, val isLikedByUser: Boolean)

class FeedAdapter(private val listener: Listener, private val posts: List<FeedPost>)
    : RecyclerView.Adapter<FeedAdapter.ViewHolder>() {

    interface Listener {
        fun toggleLike(postId: String)
        fun loadLikes(postId: String, position: Int)
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    private var postLikes: Map<Int, FeedPostLikes> = emptyMap()
    private val defaultPostLikes = FeedPostLikes(0, false)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.feed_item, parent, false)
        return ViewHolder(view)
    }


    fun updatePostLikes(position: Int, likes: FeedPostLikes) {
        postLikes += (position to likes)
        notifyItemChanged(position)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = posts[position]
        val likes = postLikes[position] ?: defaultPostLikes
        with(holder.view) {
            userPhotoImage.loadUserPhoto(post.photo)
            tvUsename.text = post.username
            ivPost.loadUserPhoto(post.image)
            if (likes.likesCount == 0) {
                tvLikes.visibility = View.GONE
            } else {
                tvLikes.visibility = View.VISIBLE
                tvLikes.text = "${likes.likesCount} likes"
            }
            tvCaption.setCaptionText(post.username, post.caption)
            ivLike.setOnClickListener { listener.toggleLike(post.id) }
            ivLike.setImageResource(
                    if (likes.isLikedByUser)
                        R.drawable.ic_likes_active
                    else
                        R.drawable.ic_likes_border
            )
            listener.loadLikes(post.id, position)
        }
    }

    private fun TextView.setCaptionText(username: String, caption: String) {
        val usernameSpannable = SpannableString(username)
        usernameSpannable.setSpan(StyleSpan(Typeface.BOLD), 0, usernameSpannable.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        usernameSpannable.setSpan(object: ClickableSpan() {
            override fun onClick(widget: View) {
                widget.context.showToast("Username is clicked")
            }
            override fun updateDrawState(ds: TextPaint?) { }
        }, 0, usernameSpannable.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        text = SpannableStringBuilder().append(usernameSpannable).append(" ")
                .append(caption)
        movementMethod = LinkMovementMethod.getInstance()
    }

    override fun getItemCount() = posts.size
}