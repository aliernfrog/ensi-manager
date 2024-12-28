package com.aliernfrog.ensimanager.ui.sheet

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.data.api.APIProfile
import com.aliernfrog.ensimanager.data.api.id
import com.aliernfrog.ensimanager.ui.component.AppModalBottomSheet
import com.aliernfrog.ensimanager.ui.component.ButtonIcon
import com.aliernfrog.ensimanager.ui.viewmodel.APIViewModel
import com.aliernfrog.ensimanager.util.extension.showErrorToast
import com.aliernfrog.ensimanager.util.extension.showSuccessToast
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAPIProfileSheet(
    apiViewModel: APIViewModel = koinViewModel(),
    sheetState: SheetState = apiViewModel.addProfileSheetState,
    onAddProfile: (APIProfile) -> Unit = {
        apiViewModel.apiProfiles.add(it)
        apiViewModel.saveProfiles()
    }
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var name by rememberSaveable { mutableStateOf("") }
    var iconModel by rememberSaveable { mutableStateOf("") }
    var endpointsURL by rememberSaveable { mutableStateOf("") }
    var authorization by rememberSaveable { mutableStateOf("") }
    var fetching by rememberSaveable { mutableStateOf(false) }

    val valid by remember { derivedStateOf {
        name.isNotEmpty() && endpointsURL.isNotEmpty()
    } }

    AppModalBottomSheet(
        title = stringResource(R.string.api_profiles_add),
        sheetState = sheetState
    ) {
        Column(Modifier.padding(horizontal = 8.dp)) {
            OutlinedTextField(
                label = { Text(stringResource(R.string.api_profiles_add_name)) },
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                label = { Text(stringResource(R.string.api_profiles_add_icon)) },
                value = iconModel,
                onValueChange = { iconModel = it },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                label = { Text(stringResource(R.string.api_profiles_add_endpointsURL)) },
                value = endpointsURL,
                onValueChange = { endpointsURL = it },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                label = { Text(stringResource(R.string.api_profiles_add_authorization)) },
                value = authorization,
                onValueChange = { authorization = it },
                modifier = Modifier.fillMaxWidth()
            )

            Crossfade(
                targetState = valid,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .align(Alignment.End)
            ) { buttonEnabled ->
                Button(
                    enabled = buttonEnabled && !fetching,
                    onClick = {
                        if (!valid) return@Button
                        val profile = APIProfile(
                            name = name,
                            iconModel = iconModel.ifBlank {
                                "${endpointsURL.split("/").getOrNull(2)}/favicon.png"
                            },
                            endpointsURL = endpointsURL,
                            authorization = authorization
                        )
                        scope.launch {
                            fetching = true
                            apiViewModel.fetchAPIEndpoints(profile)
                            val error = apiViewModel.profileErrors[profile.id]
                            if (error != null) apiViewModel.topToastState.showErrorToast(error, androidToast = true)
                            else {
                                onAddProfile(profile)
                                apiViewModel.topToastState.showSuccessToast(context.getString(R.string.api_profiles_add_added), androidToast = true)
                                sheetState.hide()
                                name = ""
                                iconModel = ""
                                endpointsURL = ""
                                authorization = ""
                            }
                            fetching = false
                        }
                    }
                ) {
                    Row(Modifier.alpha(
                        if (fetching) 0f else 1f
                    )) {
                        ButtonIcon(rememberVectorPainter(Icons.Default.Add))
                        Text(stringResource(R.string.api_profiles_add))
                    }
                    if (fetching) CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(18.dp).align(Alignment.CenterVertically)
                    )
                }
            }
        }
    }
}