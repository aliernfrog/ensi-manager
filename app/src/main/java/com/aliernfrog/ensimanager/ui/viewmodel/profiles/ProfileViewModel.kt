package com.aliernfrog.ensimanager.ui.viewmodel.profiles

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.domain.APIState
import com.aliernfrog.ensimanager.domain.AppState
import com.aliernfrog.ensimanager.impl.api.APIProfile
import com.aliernfrog.ensimanager.util.ProfileScreenData
import com.aliernfrog.ensimanager.util.extension.randomWithoutTransparency
import com.aliernfrog.ensimanager.util.extension.showSuccessToast
import com.aliernfrog.toptoast.state.TopToastState
import io.github.aliernfrog.shared.impl.InsetsManager
import kotlinx.coroutines.launch

class ProfileViewModel(
    data: ProfileScreenData,
    val apiState: APIState,
    val insetsManager: InsetsManager,
    private val appState: AppState,
    private val topToastState: TopToastState
) : ViewModel() {
    val editingProfile = data.editingProfile

    var name by mutableStateOf(editingProfile?.name ?: "")
    var color by mutableIntStateOf(editingProfile?.color ?: Color.randomWithoutTransparency().toArgb())
    var endpointsURL by mutableStateOf(editingProfile?.endpointsURL ?: "")
    var authorization by mutableStateOf(editingProfile?.authorization ?: "")
    var sha256 by mutableStateOf(editingProfile?.trustedSha256 ?: "")
    var manualFetch by mutableStateOf(editingProfile?.manualFetch ?: false)

    var showAuthorization by mutableStateOf(false)
    var certDialogProfile by mutableStateOf<APIProfile?>(null)
    var fetching by mutableStateOf(false)

    fun saveProfile(profile: APIProfile?) = viewModelScope.launch {
        fetching = true
        if (profile == null) {
            val newProfile = APIProfile(
                name = name,
                color = color,
                endpointsURL = endpointsURL,
                authorization = authorization,
                trustedSha256 = sha256.ifBlank { null },
                manualFetch = manualFetch
            )
            newProfile.fetchAPIEndpoints()
            certDialogProfile = newProfile
        } else {
            if (editingProfile != null) apiState.updateProfile(editingProfile.id, profile)
            else apiState.addProfile(profile)
            appState.navController.removeLastIfMultiple()
            topToastState.showSuccessToast(R.string.profiles_add_saved)
        }
        fetching = false
    }
}