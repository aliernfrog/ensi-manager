package com.aliernfrog.ensimanager.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Api
import androidx.compose.material.icons.rounded.Key
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.ui.component.AppScaffold
import com.aliernfrog.ensimanager.ui.component.form.FormSection
import com.aliernfrog.ensimanager.ui.viewmodel.APIViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun APIScreen(
    apiViewModel: APIViewModel = koinViewModel(),
    content: @Composable () -> Unit
) {
    AnimatedContent(targetState = !apiViewModel.isReady) { showAPIConfiguration ->
        if (showAPIConfiguration) APIConfigurationScreen()
        else content()
    }

    BackHandler(apiViewModel.fetching) {}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun APIConfigurationScreen(
    apiViewModel: APIViewModel = koinViewModel(),
    onBackClick: (() -> Unit)? = null
) {
    val scope = rememberCoroutineScope()

    // Prevent user from going back while a connection is being made.
    BackHandler(apiViewModel.fetching) {}

    AppScaffold(
        title = stringResource(R.string.setup),
        bottomBar = {
            BottomAppBar(
                tonalElevation = 0.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
                ) {
                    onBackClick?.let {
                        OutlinedButton(
                            onClick = it,
                            enabled = !apiViewModel.fetching
                        ) {
                            Text(stringResource(R.string.action_cancel))
                        }
                    }
                    Button(
                        onClick = {
                            scope.launch {
                                apiViewModel.fetchApiData()
                            }
                        },
                        enabled = !apiViewModel.fetching && apiViewModel.setupEndpointsURL.isNotBlank()
                    ) {
                        Box {
                            Text(
                                text = stringResource(R.string.action_done),
                                color = if (apiViewModel.fetching) Color.Transparent
                                else LocalContentColor.current
                            )
                            if (apiViewModel.fetching) CircularProgressIndicator(
                                strokeWidth = 2.dp,
                                modifier = Modifier
                                    .size(18.dp)
                                    .align(Alignment.Center)
                            )
                        }
                    }
                }
            }
        },
        onBackClick = if (apiViewModel.fetching) null else onBackClick
    ) {
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            OutlinedTextField(
                value = apiViewModel.setupEndpointsURL,
                onValueChange = { apiViewModel.setupEndpointsURL = it },
                label = { Text(stringResource(R.string.setup_endpoints_url)) },
                leadingIcon = {
                    Icon(
                        painter = rememberVectorPainter(Icons.Rounded.Api),
                        contentDescription = null
                    )
                },
                supportingText = { Text(stringResource(R.string.setup_endpoints_url_info)) },
                singleLine = true,
                readOnly = apiViewModel.fetching,
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            )

            var authHidden by remember { mutableStateOf(true) }
            OutlinedTextField(
                value = apiViewModel.setupAuthorization,
                onValueChange = { apiViewModel.setupAuthorization = it },
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
                readOnly = apiViewModel.fetching,
                visualTransformation = if (authHidden) PasswordVisualTransformation() else VisualTransformation.None,
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            )

            AnimatedVisibility(visible = apiViewModel.error != null) {
                FormSection(
                    title = stringResource(R.string.error),
                    topDivider = true,
                    bottomDivider = false
                ) {
                    Text(
                        text = apiViewModel.error ?: "",
                        color = MaterialTheme.colorScheme.onError,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}