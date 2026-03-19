package com.aliernfrog.ensimanager.ui.viewmodel

import android.content.Context
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.Density
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aliernfrog.ensimanager.impl.api.APIProfile
import com.aliernfrog.ensimanager.domain.APIState
import com.aliernfrog.ensimanager.util.manager.PreferenceManager
import com.aliernfrog.toptoast.state.TopToastState
import io.github.aliernfrog.shared.ui.component.createSheetStateWithDensity
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
class APIProfilesViewModel(
    val apiState: APIState,
    val prefs: PreferenceManager,
    val topToastState: TopToastState,
    context: Context
) : ViewModel() {
    val profileSheetState = createSheetStateWithDensity(skipPartiallyExpanded = true, Density(context))

    var profileSheetEditingProfile by mutableStateOf<APIProfile?>(null)
        private set

    val apiProfiles: StateFlow<List<APIProfile>>
        get() = apiState.apiProfiles

    init {
        viewModelScope.launch {
            apiState.loadProfiles(context)
            apiState.refetchAllProfiles()
        }
    }

    suspend fun openProfileSheetToAddNew() {
        profileSheetEditingProfile = null
        profileSheetState.show()
    }

    suspend fun openProfileSheetToEdit(profile: APIProfile) {
        profileSheetEditingProfile = profile
        profileSheetState.show()
    }
}
