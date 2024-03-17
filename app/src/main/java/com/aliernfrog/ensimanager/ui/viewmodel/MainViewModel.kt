package com.aliernfrog.ensimanager.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.PriorityHigh
import androidx.compose.material.icons.rounded.Update
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.Density
import androidx.lifecycle.ViewModel
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.TAG
import com.aliernfrog.ensimanager.data.ReleaseInfo
import com.aliernfrog.ensimanager.githubRepoURL
import com.aliernfrog.ensimanager.util.Destination
import com.aliernfrog.ensimanager.util.manager.PreferenceManager
import com.aliernfrog.ensimanager.util.staticutil.GeneralUtil
import com.aliernfrog.toptoast.enum.TopToastColor
import com.aliernfrog.toptoast.state.TopToastState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

@OptIn(ExperimentalMaterial3Api::class)
class MainViewModel(
    context: Context,
    val topToastState: TopToastState,
    val prefs: PreferenceManager
) : ViewModel() {
    lateinit var scope: CoroutineScope

    val updateSheetState = SheetState(skipPartiallyExpanded = false, Density(context))

    val applicationVersionName = "v${GeneralUtil.getAppVersionName(context)}"
    val applicationVersionCode = GeneralUtil.getAppVersionCode(context)
    private val applicationIsPreRelease = applicationVersionName.contains("-alpha")

    var latestVersionInfo by mutableStateOf(ReleaseInfo(
        versionName = applicationVersionName,
        preRelease = applicationIsPreRelease,
        body = context.getString(R.string.settings_about_changelog_noChangelog),
        htmlUrl = githubRepoURL,
        downloadLink = githubRepoURL
    ))
        private set

    var updateAvailable by mutableStateOf(false)
        private set

    suspend fun checkUpdates(
        manuallyTriggered: Boolean = false,
        ignoreVersion: Boolean = false
    ) {
        withContext(Dispatchers.IO) {
            try {
                val updatesURL = prefs.updatesURL
                val responseJson = JSONObject(URL(updatesURL).readText())
                val json = responseJson.getJSONObject(
                    if (applicationIsPreRelease && responseJson.has("preRelease")) "preRelease" else "stable"
                )
                val latestVersionCode = json.getInt("versionCode")
                latestVersionInfo = ReleaseInfo(
                    versionName = json.getString("versionName"),
                    preRelease = json.getBoolean("preRelease"),
                    body = json.getString("body"),
                    htmlUrl = json.getString("htmlUrl"),
                    downloadLink = json.getString("downloadUrl")
                )
                updateAvailable = ignoreVersion || latestVersionCode > applicationVersionCode
                if (updateAvailable) {
                    if (manuallyTriggered) coroutineScope {
                        updateSheetState.show()
                    } else {
                        showUpdateToast()
                        Destination.SETTINGS.hasNotification.value = true
                    }
                } else {
                    if (manuallyTriggered) topToastState.showToast(
                        text = R.string.updates_noUpdates,
                        icon = Icons.Rounded.Info,
                        iconTintColor = TopToastColor.ON_SURFACE
                    )
                }
            } catch (_: CancellationException) {
            } catch (e: Exception) {
                Log.e(TAG, "checkUpdates: ", e)
                if (manuallyTriggered) topToastState.showToast(
                    text = R.string.updates_error,
                    icon = Icons.Rounded.PriorityHigh,
                    iconTintColor = TopToastColor.ERROR
                )
            }
        }
    }

    fun showUpdateToast() {
        topToastState.showToast(
            text = R.string.updates_updateAvailable,
            icon = Icons.Rounded.Update,
            duration = 20000,
            swipeToDismiss = true,
            dismissOnClick = true,
            onToastClick = {
                scope.launch { updateSheetState.show() }
            }
        )
    }
}