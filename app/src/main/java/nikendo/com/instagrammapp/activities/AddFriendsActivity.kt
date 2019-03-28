package nikendo.com.instagrammapp.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import kotlinx.android.synthetic.main.add_friends_activity.*
import kotlinx.android.synthetic.main.add_friends_item.view.*
import nikendo.com.instagrammapp.R
import nikendo.com.instagrammapp.models.User
import nikendo.com.instagrammapp.utils.FirebaseHelper
import nikendo.com.instagrammapp.utils.TaskSourceOnCompleteListener
import nikendo.com.instagrammapp.utils.ValueEventListenerAdapter

class AddFriendsActivity : AppCompatActivity(), FriendsAdapter.Listener {
    private val TAG = "AddFriendsActivity"

    private lateinit var mFirebase: FirebaseHelper

    private lateinit var mUser: User
    private lateinit var mUsers: List<User>
    private lateinit var mAdapter: FriendsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_friends_activity)
        Log.d(TAG, "onCreate")

        mFirebase = FirebaseHelper(this)
        mAdapter = FriendsAdapter(this)

        ivBack.setOnClickListener { onBackPressed() }

        val uid = mFirebase.auth.currentUser!!.uid

        rvAddFriends.adapter = mAdapter
        rvAddFriends.layoutManager = LinearLayoutManager(this)
        mFirebase.database.child("users").addValueEventListener(ValueEventListenerAdapter {
            val allUsers = it.children.map { it.asUser()!! }
            val (userList, otherUsersList) = allUsers.partition { it.uid == uid }
            mUser = userList.first()
            mUsers = otherUsersList

            mAdapter.update(mUsers, mUser.follows)
        })
    }

    override fun follow(uid: String) {
        setFollow(uid, true) {
            mAdapter.followed(uid)
        }
    }

    override fun unfollow(uid: String) {
        setFollow(uid, false) {
            mAdapter.unfollowed(uid)
        }
    }

    private fun setFollow(uid: String, follow: Boolean, onSuccess: () -> Unit) {
        fun DatabaseReference.setValueTrueOrRemove(value: Boolean) =
                if (value) setValue(true) else removeValue()

        val followsTask = mFirebase.database.child("users").child(mUser.uid).child("follows")
                .child(uid).setValueTrueOrRemove(follow)
        val followersTask = mFirebase.database.child("users").child(uid).child("followers")
                .child(mUser.uid).setValueTrueOrRemove(follow)

        val feedPostsTask = task<Void> { taskSource ->
            mFirebase.database.child("feed-posts").child(uid).addListenerForSingleValueEvent(ValueEventListenerAdapter {
                val postsMap = if (follow) {
                    it.children.map { it.key to it.value }.toMap()
                } else {
                    it.children.map { it.key to null }.toMap()
                }
                mFirebase.database.child("feed-posts").child(mUser.uid).updateChildren(postsMap)
                        .addOnCompleteListener(TaskSourceOnCompleteListener(taskSource))
            })
        }

        Tasks.whenAll(followsTask, followersTask, feedPostsTask).addOnCompleteListener {
            if (it.isSuccessful) {
                onSuccess()
            } else {
                showToast(it.exception!!.message!!)
            }
        }
    }
}

class FriendsAdapter(private val listener: Listener): RecyclerView.Adapter<FriendsAdapter.ViewHolder>() {

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    interface Listener {
        fun follow(uid: String)
        fun unfollow(uid: String)
    }

    private var mUsers = listOf<User>()
    private var mPosition = mapOf<String, Int>()
    private var mFollows = mapOf<String, Boolean>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendsAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.add_friends_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: FriendsAdapter.ViewHolder, position: Int) {
        with(holder) {
            val user = mUsers[position]
            view.ivUserPhoto.loadUserPhoto(user.photo)
            view.tvUsername.text = user.username
            view.tvName.text = user.name
            view.btnFollow.setOnClickListener { listener.follow(user.uid) }
            view.btnUnfollow.setOnClickListener { listener.unfollow(user.uid) }

            val follows = mFollows[user.uid] ?: false
            if (follows) {
                view.btnFollow.visibility = View.GONE
                view.btnUnfollow.visibility = View.VISIBLE
            } else {
                view.btnFollow.visibility = View.VISIBLE
                view.btnUnfollow.visibility = View.GONE
            }
        }
    }

    override fun getItemCount(): Int = mUsers.size

    fun update(users: List<User>, follows: Map<String, Boolean>) {
        mUsers = users
        mPosition = users.withIndex().map { (idx, user) -> user.uid to idx }.toMap()
        mFollows = follows
        notifyDataSetChanged()
    }

    fun followed(uid: String) {
        mFollows += (uid to true)
        notifyItemChanged(mPosition[uid]!!)
    }

    fun unfollowed(uid: String) {
        mFollows -= uid
        notifyItemChanged(mPosition[uid]!!)
    }

}
