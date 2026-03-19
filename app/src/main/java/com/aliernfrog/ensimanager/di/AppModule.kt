package com.aliernfrog.ensimanager.di

import com.aliernfrog.ensimanager.BuildConfig
import com.aliernfrog.ensimanager.TAG
import com.aliernfrog.ensimanager.domain.APIState
import com.aliernfrog.ensimanager.domain.AppState
import com.aliernfrog.ensimanager.impl.api.APIProfile
import com.aliernfrog.ensimanager.repository.APIProfileRepository
import com.aliernfrog.ensimanager.util.manager.PreferenceManager
import com.aliernfrog.toptoast.state.TopToastState
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import io.github.aliernfrog.shared.impl.VersionManager
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule = module {
    singleOf(::PreferenceManager)

    single {
        GsonBuilder()
            .registerTypeAdapter(APIProfile::class.java, JsonDeserializer { json, _, _ ->
                val obj = json.asJsonObject
                APIProfile(
                    name = obj.get("name").asString,
                    endpointsURL = obj.get("endpointsURL").asString,
                    authorization = obj.get("authorization").asString,
                    trustedSha256 = obj.get("trustedSha256")?.asString
                )
            })
            .create()
    }

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

    singleOf(::APIProfileRepository)

    singleOf(::AppState)
    singleOf(::APIState)
}