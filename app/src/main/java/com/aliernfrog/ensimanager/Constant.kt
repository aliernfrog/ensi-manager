package com.aliernfrog.ensimanager

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.ui.graphics.Color
import io.github.aliernfrog.shared.data.Social
import io.github.aliernfrog.shared.impl.CreditData

const val TAG = "EnsiManagerLogs"
const val githubRepoURL = "https://github.com/aliernfrog/ensi-manager"

/**
 * Link to a releases JSON file.
 * Should return a JSON array of [io.github.aliernfrog.shared.data.ReleaseInfo].
 *
 * To automate generating such file, you can use the "/generate-releases-json.js"
 * and "/.github/workflows/generate-releases-json.yml" files, which can be found in the source code of this project.
 */
const val defaultReleasesURL = "https://raw.githubusercontent.com/aliernfrog/ensi-manager/refs/heads/main/releases.json"

/**
 * Link to a crash report handler API endpoint.
 * Should listen for POST request with "app" and "details" strings in the JSON body.
 *
 * Example API source code: https://github.com/aliernfrog/proxy-api (forwards the report to a Discord webhook)
 */
const val crashReportURL = "https://aliernfrog.vercel.app/crash-report"

object SettingsConstant {
    val socials = listOf(
        Social(
            label = "GitHub",
            icon = R.drawable.github,
            iconContainerColor = Color(0xFF104C35),
            url = githubRepoURL
        ),
        Social(
            label = "Discord",
            icon = R.drawable.discord,
            iconContainerColor = Color(0xFF5865F2),
            url = "https://discord.gg/SQXqBMs"
        ),
        Social(
            label = "Website",
            icon = Icons.Default.Language,
            url = "https://aliernfrog.github.io"
        )
    )

    val supportLinks = listOf(
        Social(
            label = "Discord",
            icon = io.github.aliernfrog.shared.R.drawable.discord,
            url = "https://discord.gg/SQXqBMs"
        ),
        Social(
            label = "GitHub Issues",
            icon = io.github.aliernfrog.shared.R.drawable.github,
            url = "$githubRepoURL/issues"
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
            githubUsername = "Exi277",
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
}