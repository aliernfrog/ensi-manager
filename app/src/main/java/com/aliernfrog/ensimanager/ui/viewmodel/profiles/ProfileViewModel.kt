package com.aliernfrog.ensimanager.ui.viewmodel.profiles

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.domain.APIState
import com.aliernfrog.ensimanager.domain.AppState
import com.aliernfrog.ensimanager.impl.api.APIProfile
import com.aliernfrog.ensimanager.util.ProfileScreenData
import com.aliernfrog.ensimanager.util.extension.showErrorToast
import com.aliernfrog.ensimanager.util.extension.showSuccessToast
import com.aliernfrog.toptoast.state.TopToastState
import kotlinx.coroutines.launch

class ProfileViewModel(
    data: ProfileScreenData,
    val apiState: APIState,
    private val appState: AppState,
    private val topToastState: TopToastState
) : ViewModel() {
    val editingProfile = data.editingProfile

    var name by mutableStateOf(editingProfile?.name ?: "")
    var endpointsURL by mutableStateOf(editingProfile?.endpointsURL ?: "")
    var authorization by mutableStateOf(editingProfile?.authorization ?: "")
    var sha256 by mutableStateOf(editingProfile?.trustedSha256 ?: "")

    var showAuthorization by mutableStateOf(false)
    var certDialogProfile by mutableStateOf<APIProfile?>(null)
    var fetching by mutableStateOf(false)

    fun saveProfile(profile: APIProfile?) = viewModelScope.launch {
        fetching = true
        if (profile == null) {
            val newProfile = APIProfile(
                name = name,
                endpointsURL = endpointsURL,
                authorization = authorization,
                trustedSha256 = sha256.ifBlank { null }
            )
            newProfile.fetchAPIEndpoints()
            if (newProfile.error != null) topToastState.showErrorToast(newProfile.error.orEmpty())
            else certDialogProfile = newProfile
        } else {
            if (editingProfile != null) apiState.updateProfile(editingProfile.id, profile)
            else apiState.addProfile(profile)
            appState.navController.removeLastIfMultiple()
            topToastState.showSuccessToast(R.string.profiles_add_saved)
        }
        fetching = false
    }
}