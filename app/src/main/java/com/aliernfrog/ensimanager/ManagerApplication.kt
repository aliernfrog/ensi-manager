package com.aliernfrog.ensimanager

import android.app.Application
import com.aliernfrog.ensimanager.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class ManagerApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@ManagerApplication)
            modules(appModules)
        }
    }
}