package com.aliernfrog.ensimanager.di

import com.aliernfrog.ensimanager.BuildConfig
import com.aliernfrog.ensimanager.TAG
import com.aliernfrog.ensimanager.util.manager.PreferenceManager
import com.aliernfrog.toptoast.state.TopToastState
import com.google.gson.Gson
import io.github.aliernfrog.shared.impl.VersionManager
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule = module {
    singleOf(::PreferenceManager)
    singleOf(::Gson)

    single {
        get<PreferenceManager>().let { prefs ->
            @Suppress("KotlinConstantConditions") VersionManager(
                tag = TAG,
                appName = "PF Tool",
                releasesURLPref = prefs.releasesURL,
                debugInfoPrefs = prefs.debugInfoPrefs,
                defaultInstallURL = "https://github.com/aliernfrog/pf-tool",
                buildCommit = BuildConfig.GIT_COMMIT,
                buildBranch = BuildConfig.GIT_BRANCH,
                buildHasLocalChanges = BuildConfig.GIT_LOCAL_CHANGES,
                context = get()
            )
        }
    }

    single {
        TopToastState(
            composeView = null,
            appTheme = null,
            allowSwipingByDefault = false
        )
    }
}