package com.aliernfrog.ensimanager.util.staticutil

import android.content.Context
import org.json.JSONArray

@Suppress("DEPRECATION")
class GeneralUtil {
    companion object {
        fun getAppVersionName(context: Context): String {
            val packageManager = context.packageManager
            val packageInfo = packageManager.getPackageInfo(context.packageName, 0)
            return packageInfo.versionName
        }

        fun getAppVersionCode(context: Context): Int {
            val packageManager = context.packageManager
            val packageInfo = packageManager.getPackageInfo(context.packageName, 0)
            return packageInfo.versionCode
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