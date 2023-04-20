package com.aliernfrog.ensimanager.util.staticutil

import org.json.JSONArray

class GeneralUtil {
    companion object {
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