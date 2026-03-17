package com.aliernfrog.ensimanager.di

import com.aliernfrog.ensimanager.ui.viewmodel.*
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val viewModelModule = module {
    // TODO use viewModelOf()
    singleOf(::MainViewModel)
    singleOf(::APIViewModel)

    singleOf(::DashboardViewModel)
    singleOf(::StringsViewModel)
    singleOf(::LogsViewModel)
    singleOf(::SettingsViewModel)
}