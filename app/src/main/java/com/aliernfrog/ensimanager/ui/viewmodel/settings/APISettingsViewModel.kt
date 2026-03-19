package com.aliernfrog.ensimanager.ui.viewmodel.settings

import androidx.lifecycle.ViewModel
import com.aliernfrog.ensimanager.domain.APIState
import com.aliernfrog.ensimanager.util.manager.PreferenceManager
import com.aliernfrog.toptoast.state.TopToastState

class APISettingsViewModel(
    val apiState: APIState,
    val prefs: PreferenceManager,
    val topToastState: TopToastState
) : ViewModel()