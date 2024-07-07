package com.aliernfrog.ensimanager.impl

import android.util.Log
import com.aliernfrog.ensimanager.TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

class CreditData(
    val name: String,
    val githubUsername: String? = null,
    val description: String,
    val link: String? = githubUsername?.let { "https://github.com/username/$githubUsername" }
) {
    private var fetchedAvatar = false
    var avatarURL: String? = null

    suspend fun fetchAvatar() {
        if (fetchedAvatar) return
        fetchedAvatar = true
        if (githubUsername == null) return
        withContext(Dispatchers.IO) {
            try {
                val res = URL("https://api.github.com/users/$githubUsername").readText()
                val json = JSONObject(res)
                avatarURL = json.getString("avatar_url")
            } catch (e: Exception) {
                Log.e(TAG, "CreditData/fetchAvatar: ", e)
            }
        }
    }
}