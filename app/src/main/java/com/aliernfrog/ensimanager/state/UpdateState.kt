package com.aliernfrog.ensimanager.state

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.PriorityHigh
import androidx.compose.material.icons.rounded.Update
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.aliernfrog.ensimanager.ConfigKey
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.data.ReleaseInfo
import com.aliernfrog.ensimanager.githubRepoURL
import com.aliernfrog.ensimanager.util.staticutil.GeneralUtil
import com.aliernfrog.toptoast.enum.TopToastColor
import com.aliernfrog.toptoast.state.TopToastState
import kotlinx.coroutines.*
import org.json.JSONObject
import java.net.URL

@OptIn(ExperimentalMaterialApi::class)
class UpdateState(
    private val topToastState: TopToastState,
    config: SharedPreferences,
    context: Context
) {
    val updateSheetState = ModalBottomSheetState(ModalBottomSheetValue.Hidden)

    private val releaseUrl = config.getString(ConfigKey.KEY_APP_UPDATES_URL, ConfigKey.DEFAULT_UPDATES_URL)!!
    private val autoUpdatesEnabled = config.getBoolean(ConfigKey.KEY_APP_AUTO_UPDATES, true)
    private val currentVersionName = GeneralUtil.getAppVersionName(context)
    private val currentVersionCode = GeneralUtil.getAppVersionCode(context)
    private val isCurrentPreRelease = GeneralUtil.getAppVersionName(context).contains("-alpha")

    var latestVersionInfo by mutableStateOf(ReleaseInfo(
        versionName = currentVersionName,
        preRelease = isCurrentPreRelease,
        body = context.getString(R.string.updates_noUpdates),
        htmlUrl = githubRepoURL,
        downloadLink = githubRepoURL
    ))

    init {
        if (autoUpdatesEnabled) CoroutineScope(Dispatchers.Default).launch {
            checkUpdates()
        }
    }

    suspend fun checkUpdates(
        manuallyTriggered: Boolean = false,
        ignoreVersion: Boolean = false
    ) {
        withContext(Dispatchers.IO) {
            try {
                val responseJson = JSONObject(URL(releaseUrl).readText())
                val branchKey = if (isCurrentPreRelease && responseJson.has("preRelease")) "preRelease" else "stable"
                val json = responseJson.getJSONObject(branchKey)
                val latestVersionCode = json.getInt("versionCode")
                val latestVersionName = json.getString("versionName")
                val latestIsPreRelease = json.getBoolean("preRelease")
                val latestBody = json.getString("body")
                val latestHtmlUrl = json.getString("htmlUrl")
                val latestDownload = json.getString("downloadUrl")
                val isUpToDate = !ignoreVersion && latestVersionCode <= currentVersionCode
                if (!isUpToDate) {
                    latestVersionInfo = ReleaseInfo(
                        versionName = latestVersionName,
                        preRelease = latestIsPreRelease,
                        body = latestBody,
                        htmlUrl = latestHtmlUrl,
                        downloadLink = latestDownload
                    )
                    if (!manuallyTriggered) topToastState.showToast(
                        text = R.string.updates_updateAvailable,
                        icon = Icons.Rounded.Update,
                        stayMs = 20000,
                        onToastClick = {
                            CoroutineScope(Dispatchers.Default).launch { updateSheetState.show() }
                        }
                    ) else withContext(Dispatchers.Default) { updateSheetState.show() }
                } else {
                    if (manuallyTriggered) topToastState.showToast(
                        text = R.string.updates_noUpdates,
                        icon = Icons.Rounded.Info,
                        iconTintColor = TopToastColor.ON_SURFACE
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                if (manuallyTriggered) topToastState.showToast(
                    text = R.string.updates_error,
                    icon = Icons.Rounded.PriorityHigh,
                    iconTintColor = TopToastColor.ERROR
                )
            }
        }
    }
}