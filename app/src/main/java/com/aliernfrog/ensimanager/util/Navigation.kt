package com.aliernfrog.ensimanager.util

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.automirrored.outlined.Article
import androidx.compose.material.icons.automirrored.outlined.Notes
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.rounded.Api
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.ui.NavDisplay
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.data.api.APIEndpoints
import io.github.aliernfrog.shared.ui.screen.settings.SettingsDestination
import io.github.aliernfrog.shared.ui.screen.settings.category
import io.github.aliernfrog.shared.util.SharedStringResolvable

object NavigationConstant {
    val INITIAL_DESTINATION = Destination.APIProfiles
    val INITIAL_MAIN_DESTINATION = MainDestination.DASHBOARD
}

object MainDestinationGroup

enum class MainDestination(
    @StringRes val label: Int,
    val vectorFilled: ImageVector,
    val vectorOutlined: ImageVector,
    val isAvailableInEndpoints: ((APIEndpoints) -> Boolean)?
) {
    DASHBOARD(
        label = R.string.dashboard,
        vectorFilled = Icons.Default.Dashboard,
        vectorOutlined = Icons.Outlined.Dashboard,
        isAvailableInEndpoints = {
            it.getDashboard != null
        }
    ),

    STRINGS(
        label = R.string.strings,
        vectorFilled = Icons.AutoMirrored.Filled.Article,
        vectorOutlined = Icons.AutoMirrored.Outlined.Article,
        isAvailableInEndpoints = {
            it.getStrings != null
        }
    ),

    LOGS(
        label = R.string.logs,
        vectorFilled = Icons.AutoMirrored.Filled.Notes,
        vectorOutlined = Icons.AutoMirrored.Outlined.Notes,
        isAvailableInEndpoints = {
            it.getLogs != null
        }
    )
}

sealed class Destination {
    object APIProfiles : Destination()
    object Updates : Destination()
}

class AppSettingsDestination {
    companion object {
        val api = SettingsDestination(
            title = SharedStringResolvable.Resource(R.string.settings_api),
            description = SharedStringResolvable.Resource(R.string.settings_api_description),
            icon = Icons.Rounded.Api,
            iconContainerColor = Color.Gray
        )

        val security = SettingsDestination(
            title = SharedStringResolvable.Resource(R.string.settings_security),
            description = SharedStringResolvable.Resource(R.string.settings_security_description),
            icon = Icons.Rounded.Lock,
            iconContainerColor = Color.Red,
        )
    }
}

val appSettingsCategories = listOf(
    category(
        title = SharedStringResolvable.Resource(R.string.settings_category_api)
    ) {
        +AppSettingsDestination.api
        +AppSettingsDestination.security
    },

    category(
        title = SharedStringResolvable.Resource(R.string.settings_category_app)
    ) {
        +SettingsDestination.appearance
        +SettingsDestination.experimental
        +SettingsDestination.about
    }
)

val slideTransitionMetadata = NavDisplay.transitionSpec {
    slideIntoContainer(
        AnimatedContentTransitionScope.SlideDirection.Start
    ) + fadeIn() togetherWith slideOutOfContainer(
        AnimatedContentTransitionScope.SlideDirection.Start
    ) + fadeOut()
} + NavDisplay.popTransitionSpec {
    slideIntoContainer(
        AnimatedContentTransitionScope.SlideDirection.End
    ) togetherWith slideOutOfContainer(
        AnimatedContentTransitionScope.SlideDirection.End
    )
} + NavDisplay.predictivePopTransitionSpec {
    slideIntoContainer(
        AnimatedContentTransitionScope.SlideDirection.End
    ) togetherWith slideOutOfContainer(
        AnimatedContentTransitionScope.SlideDirection.End
    )
}

val slideVerticalTransitionMetadata = NavDisplay.transitionSpec {
    slideInVertically(
        initialOffsetY = { it }
    ) + fadeIn() togetherWith slideOutVertically(
        targetOffsetY = { -it }
    ) + fadeOut()
} + NavDisplay.popTransitionSpec {
    slideInVertically(
        initialOffsetY = { -it }
    ) + fadeIn() togetherWith slideOutVertically(
        targetOffsetY = { -it }
    ) + fadeOut()
} + NavDisplay.predictivePopTransitionSpec {
    slideInVertically(
        initialOffsetY = { -it }
    ) + fadeIn() togetherWith slideOutVertically(
        targetOffsetY = { it }
    ) + fadeOut()
}

enum class NavigationBarType {
    HIDDEN,
    BOTTOM_BAR,
    SIDE_RAIL
}