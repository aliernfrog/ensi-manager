package com.aliernfrog.ensimanager.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.aliernfrog.ensimanager.util.appSettingsCategories
import com.aliernfrog.ensimanager.util.manager.PreferenceManager
import com.aliernfrog.toptoast.state.TopToastState
import io.github.aliernfrog.shared.impl.VersionManager

class SettingsViewModel(
    val versionManager: VersionManager,
    val prefs: PreferenceManager,
    val topToastState: TopToastState
) : ViewModel() {
    val categories = appSettingsCategories

    val debugInfo: String
        get() = versionManager.getDebugInfo()
}