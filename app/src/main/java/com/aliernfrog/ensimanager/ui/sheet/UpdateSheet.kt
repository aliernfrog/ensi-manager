package com.aliernfrog.ensimanager.ui.sheet

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.data.ReleaseInfo
import com.aliernfrog.ensimanager.ui.component.BaseModalBottomSheet
import com.aliernfrog.ensimanager.ui.component.ButtonIcon
import com.aliernfrog.ensimanager.ui.viewmodel.MainViewModel
import com.aliernfrog.ensimanager.util.extension.horizontalFadingEdge
import dev.jeziellago.compose.markdowntext.MarkdownText
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun UpdateSheet(
    mainViewModel: MainViewModel = getViewModel(),
    sheetState: ModalBottomSheetState = mainViewModel.updateSheetState,
    latestVersionInfo: ReleaseInfo = mainViewModel.latestVersionInfo
) {
    val uriHandler = LocalUriHandler.current
    BaseModalBottomSheet(
        sheetState = sheetState
    ) {
        Actions(
            versionName = latestVersionInfo.versionName,
            preRelease = latestVersionInfo.preRelease,
            onGithubClick = { uriHandler.openUri(latestVersionInfo.htmlUrl) },
            onUpdateClick = { uriHandler.openUri(latestVersionInfo.downloadLink) }
        )
        HorizontalDivider(
            modifier = Modifier.alpha(0.3f),
            thickness = 1.dp
        )
        MarkdownText(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            markdown = latestVersionInfo.body,
            color = LocalContentColor.current,
            linkColor = MaterialTheme.colorScheme.primary,
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
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .weight(1f)
                .horizontalFadingEdge(
                    scrollState = versionNameScrollState,
                    edgeColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp),
                    //TODO isRTL = LocalLayoutDirection.current == LayoutDirection.Rtl
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(versionNameScrollState),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = versionName,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = stringResource(
                        if (preRelease) R.string.updates_preRelease
                        else R.string.updates_stable
                    ),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Light,
                    color = LocalContentColor.current.copy(alpha = 0.7f),
                    modifier = Modifier.padding(horizontal = 6.dp)
                )
            }
        }
        IconButton(onClick = onGithubClick) {
            Icon(
                painter = painterResource(R.drawable.github),
                contentDescription = stringResource(R.string.updates_openInGithub),
                modifier = Modifier.padding(6.dp)
            )
        }
        Button(
            onClick = onUpdateClick
        ) {
            ButtonIcon(
                painter = rememberVectorPainter(Icons.Default.Update)
            )
            Text(stringResource(R.string.updates_update))
        }
    }
}