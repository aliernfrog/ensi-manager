package com.aliernfrog.ensimanager.di

import org.koin.mp.KoinPlatformTools

inline fun <reified T : Any> getKoinInstance(): T = KoinPlatformTools.defaultContext().get().get<T>()