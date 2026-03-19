package com.aliernfrog.ensimanager.di

import com.aliernfrog.ensimanager.ui.viewmodel.*
import com.aliernfrog.ensimanager.ui.viewmodel.settings.APISettingsViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    // TODO use viewModelOf()
    viewModelOf(::MainViewModel)
    viewModelOf(::APIProfilesViewModel)

    singleOf(::DashboardViewModel)
    singleOf(::StringsViewModel)
    singleOf(::LogsViewModel)
    singleOf(::SettingsViewModel)

    viewModelOf(::APISettingsViewModel)
}