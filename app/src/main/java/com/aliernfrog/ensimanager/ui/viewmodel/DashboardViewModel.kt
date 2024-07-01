package com.aliernfrog.ensimanager.ui.viewmodel

import android.util.Log
import androidx.compose.foundation.ScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.TAG
import com.aliernfrog.ensimanager.data.EnsiAPIDashboard
import com.aliernfrog.ensimanager.data.doRequest
import com.aliernfrog.ensimanager.util.extension.isSuccessful
import com.aliernfrog.ensimanager.util.extension.showErrorToast
import com.aliernfrog.ensimanager.util.extension.summary
import com.aliernfrog.toptoast.state.TopToastState
import com.google.gson.Gson

@OptIn(ExperimentalMaterial3Api::class)
class DashboardViewModel(
    val topToastState: TopToastState,
    private val apiViewModel: APIViewModel,
    private val gson: Gson
) : ViewModel() {
    val topAppBarState = TopAppBarState(0F, 0F, 0F)
    val scrollState = ScrollState(0)

    val isFetching get() = apiViewModel.fetching

    var dashboardData by mutableStateOf<EnsiAPIDashboard?>(null)
        private set

    suspend fun fetchDashboardData() {
        try {
            val response = apiViewModel.apiData?.getDashboard?.doRequest()
            if (response == null || !response.isSuccessful) return topToastState.showErrorToast(response.summary)
            dashboardData = gson.fromJson(response.responseBody, EnsiAPIDashboard::class.java)
        } catch (e: Exception) {
            Log.e(TAG, "fetchDashboardData: ", e)
            topToastState.showErrorToast(R.string.dashboard_couldntFetch)
        }
    }
}