package com.aliernfrog.ensimanager.ui.viewmodel

import android.content.Context
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aliernfrog.ensimanager.impl.api.APIProfile
import com.aliernfrog.ensimanager.domain.APIState
import com.aliernfrog.ensimanager.domain.AppState
import com.aliernfrog.ensimanager.util.Destination
import com.aliernfrog.ensimanager.util.ProfileScreenData
import com.aliernfrog.ensimanager.util.manager.PreferenceManager
import com.aliernfrog.toptoast.state.TopToastState
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
class ProfilesViewModel(
    val appState: AppState,
    val apiState: APIState,
    val prefs: PreferenceManager,
    val topToastState: TopToastState,
    context: Context
) : ViewModel() {
    val apiProfiles: StateFlow<List<APIProfile>>
        get() = apiState.apiProfiles

    init {
        viewModelScope.launch {
            apiState.loadProfiles(context)
            apiState.refetchAllProfiles()
        }
    }

    fun navigateToProfileScreen(editingProfile: APIProfile?) {
        appState.navController.add(Destination.Profile(
            ProfileScreenData(
                editingProfile = editingProfile
            )
        ))
    }
}