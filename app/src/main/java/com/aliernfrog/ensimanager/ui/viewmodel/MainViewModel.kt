package com.aliernfrog.ensimanager.ui.viewmodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.PriorityHigh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.util.Destination
import com.aliernfrog.ensimanager.util.MainDestinationGroup
import com.aliernfrog.ensimanager.util.NavigationConstant
import com.aliernfrog.ensimanager.util.manager.PreferenceManager
import com.aliernfrog.toptoast.enum.TopToastColor
import com.aliernfrog.toptoast.state.TopToastState
import io.github.aliernfrog.shared.impl.UpdateCheckResult
import io.github.aliernfrog.shared.impl.VersionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
class MainViewModel(
    val prefs: PreferenceManager,
    val topToastState: TopToastState,
    val versionManager: VersionManager
) : ViewModel() {
    lateinit var scope: CoroutineScope

    val navigationBackStack = mutableStateListOf<Any>(
        NavigationConstant.INITIAL_DESTINATION
    )
    var currentMainDestination by mutableStateOf(NavigationConstant.INITIAL_MAIN_DESTINATION)
    val isAtMainDestination: Boolean
        get() = navigationBackStack.last() == MainDestinationGroup

    val availableUpdates = versionManager.availableUpdates
    val currentVersionInfo = versionManager.currentVersionInfo
    val isCompatibleWithLatestVersion = versionManager.isCompatibleWithLatestVersion
    val isCheckingForUpdates = versionManager.isCheckingForUpdates
    var showUpdateNotification by mutableStateOf(false)

    var updateAvailable by mutableStateOf(false)
        private set

    fun checkUpdates(
        manuallyTriggered: Boolean = false,
        skipVersionCheck: Boolean = false
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val updateCheckResult = versionManager.checkUpdates(skipVersionCheck = skipVersionCheck)
            when (updateCheckResult) {
                UpdateCheckResult.NoUpdates -> {
                    if (manuallyTriggered) withContext(Dispatchers.Main) {
                        topToastState.showToast(
                            text = R.string.updates_noUpdates,
                            icon = Icons.Rounded.Info,
                            iconTintColor = TopToastColor.ON_SURFACE
                        )
                    }
                }
                UpdateCheckResult.Error -> {
                    if (manuallyTriggered) withContext(Dispatchers.Main) {
                        topToastState.showToast(
                            text = R.string.updates_error,
                            icon = Icons.Rounded.PriorityHigh,
                            iconTintColor = TopToastColor.ERROR
                        )
                    }
                }
                is UpdateCheckResult.UpdatesAvailable -> {
                    withContext(Dispatchers.Main) {
                        if (manuallyTriggered && navigationBackStack.first() !is Destination.Updates)
                            navigationBackStack.add(Destination.Updates)
                        else showUpdateToast()
                    }
                }
            }
        }
    }

    fun showUpdateToast() {
        io.github.aliernfrog.shared.util.showUpdateToast {
            if (navigationBackStack.first() !is Destination.Updates)
                navigationBackStack.add(Destination.Updates)
        }
    }
}