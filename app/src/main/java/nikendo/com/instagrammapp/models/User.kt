package nikendo.com.instagrammapp.models

import com.google.firebase.database.Exclude

data class User(val email: String = "", val name: String = "", val username: String = "", val website: String? = null,
                val follows: Map<String, Boolean> = emptyMap(),
                val followes: Map<String, Boolean> = emptyMap(),
                val bio: String? = null, val phone: String? = null, val photo: String? = null,
                @Exclude val uid: String = "")