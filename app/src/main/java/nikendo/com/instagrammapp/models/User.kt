package nikendo.com.instagrammapp.models

data class User(val email: String = "", val name: String = "", val username: String = "", val website: String? = null,
                val bio: String? = null, val phone: String? = null, val photo: String? = null)