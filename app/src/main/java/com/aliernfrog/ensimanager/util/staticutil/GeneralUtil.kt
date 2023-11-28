package com.aliernfrog.ensimanager.util.staticutil

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.aliernfrog.ensimanager.di.appModules
import com.aliernfrog.ensimanager.ui.activity.MainActivity
import org.json.JSONArray
import org.koin.core.context.GlobalContext.loadKoinModules
import org.koin.core.context.GlobalContext.unloadKoinModules

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

        fun restartApp(context: Context, withModules: Boolean = true) {
            val intent = Intent(context, MainActivity::class.java)
            (context as Activity).finish()
            if (withModules) {
                unloadKoinModules(appModules)
                loadKoinModules(appModules)
            }
            context.startActivity(intent)
        }
    }
}