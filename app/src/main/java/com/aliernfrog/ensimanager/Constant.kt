package com.aliernfrog.ensimanager

import android.os.Build
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import com.aliernfrog.ensimanager.data.PrefEditItem
import com.aliernfrog.ensimanager.data.Social
import com.aliernfrog.ensimanager.impl.CreditData

const val TAG = "EnsiManagerLogs"
const val githubRepoURL = "https://github.com/aliernfrog/ensi-manager"
const val experimentalSettingsRequiredClicks = 10

val imeSupportsSyncAppContent = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R

object ConfigKey {
    const val PREF_NAME = "APP_CONFIG"
    const val KEY_APP_THEME = "appTheme"
    const val KEY_APP_MATERIAL_YOU = "materialYou"
    const val KEY_APP_AUTO_UPDATES = "autoUpdates"
    const val KEY_APP_UPDATES_URL = "updatesUrl"
    const val KEY_API_ENDPOINTS_URL = "apiEndpointsUrl"
    const val KEY_API_AUTHORIZATION = "apiAuthorization"
    const val DEFAULT_UPDATES_URL = "https://aliernfrog.github.io/ensimanager/latest.json"
}

object SettingsConstant {
    val socials = listOf(
        Social(
            label = "GitHub",
            icon = R.drawable.github,
            url = githubRepoURL
        ),
        Social(
            label = "Discord",
            icon = R.drawable.discord,
            url = "https://discord.gg/SQXqBMs"
        ),
        Social(
            label = "Website",
            icon = Icons.Default.Language,
            url = "https://aliernfrog.github.io"
        )
    )

    val credits = listOf(
        CreditData(
            name = "alieRN",
            githubUsername = "aliernfrog",
            description = "Ensi Manager & Ensi developer"
        ),
        CreditData(
            name = "Infini_",
            githubUsername = "infini0083",
            description = "Assisting with Ensi Manager & Ensi"
        ),
        CreditData(
            name = "Exi",
            description = "Assisting with Ensi"
        ),
        CreditData(
            name = "ReVanced Manager",
            githubUsername = "revanced",
            description = "Inspiration",
            link = "https://github.com/revanced/revanced-manager"
        ),
        CreditData(
            name = "Vendetta Manager",
            githubUsername = "vendetta-mod",
            description = "Inspiration",
            link = "https://github.com/vendetta-mod/VendettaManager"
        )
    )

    val experimentalPrefOptions = listOf(
        PrefEditItem(
            labelResourceId = R.string.settings_experimental_updatesURL,
            getValue = { it.updatesURL },
            setValue = { newValue, prefs ->
                prefs.updatesURL = newValue
            },
            default = ConfigKey.DEFAULT_UPDATES_URL
        )
    )
}