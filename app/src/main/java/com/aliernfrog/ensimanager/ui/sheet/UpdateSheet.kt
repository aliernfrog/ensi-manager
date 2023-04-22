package com.aliernfrog.ensimanager.ui.sheet

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Update
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.state.UpdateState
import com.aliernfrog.ensimanager.ui.component.BaseModalBottomSheet
import com.aliernfrog.ensimanager.util.extension.horizontalFadingEdge
import dev.jeziellago.compose.markdowntext.MarkdownText

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun UpdateSheet(
    updateState: UpdateState
) {
    val uriHandler = LocalUriHandler.current
    BaseModalBottomSheet(
        sheetState = updateState.updateSheetState
    ) {
        Actions(
            versionName = updateState.latestVersionInfo.versionName,
            preRelease = updateState.latestVersionInfo.preRelease,
            onGithubClick = { uriHandler.openUri(updateState.latestVersionInfo.htmlUrl) },
            onUpdateClick = { uriHandler.openUri(updateState.latestVersionInfo.downloadLink) }
        )
        MarkdownText(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .padding(horizontal = 16.dp),
            markdown = updateState.latestVersionInfo.body,
            color = LocalContentColor.current,
            style = LocalTextStyle.current,
            onLinkClicked = {
                uriHandler.openUri(it)
            }
        )
    }
}

@Composable
private fun Actions(
    versionName: String,
    preRelease: Boolean,
    onGithubClick: () -> Unit,
    onUpdateClick: () -> Unit,
) {
    val versionNameScrollState = rememberScrollState()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .weight(1f)
                .horizontalFadingEdge(versionNameScrollState, MaterialTheme.colorScheme.surface)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(versionNameScrollState),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = versionName,
                    fontSize = 25.sp
                )
                Text(
                    text = stringResource(
                        if (preRelease) R.string.updates_preRelease
                        else R.string.updates_stable
                    ),
                    fontSize = 15.sp,
                    color = LocalContentColor.current.copy(alpha = 0.7f),
                    modifier = Modifier.padding(horizontal = 6.dp)
                )
            }
        }
        IconButton(onClick = onGithubClick) {
            Icon(
                painter = painterResource(R.drawable.github),
                contentDescription = stringResource(R.string.updates_openInGithub)
            )
        }
        Button(
            onClick = onUpdateClick
        ) {
            Icon(
                painter = rememberVectorPainter(Icons.Rounded.Update),
                contentDescription = null,
                modifier = Modifier.padding(end = 4.dp)
            )
            Text(stringResource(R.string.updates_update))
        }
    }
}