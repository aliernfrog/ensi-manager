package com.aliernfrog.ensimanager.di

import com.aliernfrog.ensimanager.util.manager.ContextUtils
import com.aliernfrog.ensimanager.util.manager.PreferenceManager
import com.aliernfrog.toptoast.state.TopToastState
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule = module {
    singleOf(::ContextUtils)
    singleOf(::PreferenceManager)
    single {
        TopToastState(composeView = null)
    }
}