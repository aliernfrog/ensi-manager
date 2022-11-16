package com.aliernfrog.ensimanager.util

import com.aliernfrog.ensimanager.data.ApiRoute
import org.json.JSONArray

class GeneralUtil {
    companion object {
        fun getApiRouteFromString(string: String): ApiRoute? {
            return try {
                val split = string.split(" ## ")
                ApiRoute(split[0].uppercase(), split[1])
            } catch (_: Exception) {
                null
            }
        }

        fun isJsonArray(string: String): Boolean {
            return try {
                JSONArray(string)
                true
            } catch (_: Exception) {
                false
            }
        }

        fun jsonArrayToList(jsonArray: JSONArray): List<String> {
            val list = arrayListOf<String>()
            for (i in 0 until jsonArray.length()) {
                list.add(jsonArray.get(i).toString())
            }
            return list
        }
    }
}