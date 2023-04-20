package com.aliernfrog.ensimanager.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.enum.TopBarStyle
import com.aliernfrog.ensimanager.state.EnsiAPIState
import com.aliernfrog.ensimanager.ui.component.AppScaffold
import com.aliernfrog.ensimanager.ui.theme.AppBottomSheetShape
import com.aliernfrog.ensimanager.ui.theme.AppComponentShape
import com.aliernfrog.ensimanager.util.Destination
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun APISetupScreen(
    apiState: EnsiAPIState,
    navController: NavController
) {
    AppScaffold(
        title = stringResource(R.string.setup),
        topBarStyle = TopBarStyle.PINNED,
        topBarActions = {
            IconButton(
                onClick = {
                    navController.navigate(Destination.SETTINGS_SUBSCREEN.route)
                },
                enabled = !apiState.setupFetching
            ) {
                Icon(
                    painter = rememberVectorPainter(Icons.Outlined.Settings),
                    contentDescription = null
                )
            }
        },
        bottomBar = {
            BottomAppBar(Modifier.clip(AppBottomSheetShape)) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
                ) {
                    BottomBarActions(apiState)
                }
            }
        }
    ) {
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            Configuration(apiState)
            Error(apiState)
        }
    }
}

@Composable
private fun Configuration(apiState: EnsiAPIState) {
    var authHidden by remember { mutableStateOf(true) }
    OutlinedTextField(
        value = apiState.setupEndpointsUrl,
        onValueChange = { apiState.setupEndpointsUrl = it },
        label = { Text(stringResource(R.string.setup_endpoints_url)) },
        leadingIcon = {
            Icon(
                painter = rememberVectorPainter(Icons.Rounded.Api),
                contentDescription = null
            )
        },
        supportingText = { Text(stringResource(R.string.setup_endpoints_url_info)) },
        shape = AppComponentShape,
        singleLine = true,
        readOnly = apiState.setupFetching,
        modifier = Modifier.fillMaxWidth().padding(8.dp)
    )
    OutlinedTextField(
        value = apiState.setupAuth,
        onValueChange = { apiState.setupAuth = it },
        label = { Text(stringResource(R.string.setup_auth)) },
        leadingIcon = {
            Icon(
                painter = rememberVectorPainter(Icons.Rounded.Key),
                contentDescription = null
            )
        },
        trailingIcon = {
            IconButton(onClick = { authHidden = !authHidden }) {
                Icon(
                    painter = rememberVectorPainter(
                        if (authHidden) Icons.Rounded.Visibility
                        else Icons.Rounded.VisibilityOff
                    ),
                    contentDescription = null
                )
            }
        },
        supportingText = { Text(stringResource(R.string.setup_auth_info)) },
        shape = AppComponentShape,
        readOnly = apiState.setupFetching,
        visualTransformation = if (authHidden) PasswordVisualTransformation() else VisualTransformation.None,
        modifier = Modifier.fillMaxWidth().padding(8.dp)
    )
}

@Composable
private fun Error(apiState: EnsiAPIState) {
    AnimatedVisibility(visible = apiState.setupError != null) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clip(AppComponentShape)
                .background(MaterialTheme.colorScheme.error)
                .padding(8.dp)
        ) {
            Text(
                text = stringResource(R.string.error),
                color = MaterialTheme.colorScheme.onError,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
            Text(
                text = apiState.setupError ?: "",
                color = MaterialTheme.colorScheme.onError,
                fontFamily = FontFamily.Monospace,
                fontSize = 13.sp,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
private fun BottomBarActions(apiState: EnsiAPIState) {
    val scope = rememberCoroutineScope()
    if (apiState.setupCancellable) OutlinedButton(
        onClick = {
            apiState.dismissApiSetup()
        },
        enabled = !apiState.setupFetching
    ) {
        Text(stringResource(R.string.action_cancel))
    }
    Button(
        onClick = { scope.launch { apiState.fetchApiData() } },
        enabled = !apiState.setupFetching && apiState.setupEndpointsUrl.isNotBlank(),
        modifier = Modifier.animateContentSize()
    ) {
        Crossfade(targetState = apiState.setupFetching) {
            if (it) CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp
            ) else Text(stringResource(R.string.action_done))
        }
    }
}