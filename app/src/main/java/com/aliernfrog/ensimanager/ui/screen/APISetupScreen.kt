package com.aliernfrog.ensimanager.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.Api
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.enum.TopBarStyle
import com.aliernfrog.ensimanager.state.EnsiAPIState
import com.aliernfrog.ensimanager.ui.component.AppScaffold
import com.aliernfrog.ensimanager.ui.theme.AppComponentShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun APISetupScreen(
    apiState: EnsiAPIState,
    onNavigateSettings: () -> Unit
) {
    AppScaffold(
        title = stringResource(R.string.setup),
        topBarStyle = TopBarStyle.PINNED,
        topBarActions = {
            IconButton(onClick = onNavigateSettings) {
                Icon(
                    painter = rememberVectorPainter(Icons.Outlined.Settings),
                    contentDescription = null
                )
            }
        }
    ) {
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            Configuration()
        }
    }
}

@Composable
private fun Configuration() {
    var endpointsUrl by remember { mutableStateOf("") }
    OutlinedTextField(
        value = endpointsUrl,
        onValueChange = { endpointsUrl = it },
        label = { Text(stringResource(R.string.setup_endpoints_url)) },
        leadingIcon = {
            Icon(
                painter = rememberVectorPainter(Icons.Rounded.Api),
                contentDescription = null
            )
        },
        supportingText = { Text(stringResource(R.string.setup_endpoints_url_info)) },
        shape = AppComponentShape,
        modifier = Modifier.fillMaxWidth().padding(8.dp)
    )
}