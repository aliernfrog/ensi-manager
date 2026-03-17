package com.aliernfrog.ensimanager.di

import com.aliernfrog.ensimanager.util.sharedString
import io.github.aliernfrog.shared.di.getSharedModule

val appModules = listOf(
    appModule,
    viewModelModule,
    getSharedModule(
        sharedString = sharedString
    )
)