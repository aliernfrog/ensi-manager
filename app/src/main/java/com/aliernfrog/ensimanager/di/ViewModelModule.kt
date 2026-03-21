package com.aliernfrog.ensimanager.di

import com.aliernfrog.ensimanager.ui.viewmodel.*
import com.aliernfrog.ensimanager.ui.viewmodel.settings.APISettingsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::MainViewModel)
    viewModelOf(::APIProfilesViewModel)

    viewModelOf(::DashboardViewModel)
    viewModelOf(::StringsViewModel)
    viewModelOf(::LogsViewModel)

    viewModelOf(::SettingsViewModel)
    viewModelOf(::APISettingsViewModel)
}