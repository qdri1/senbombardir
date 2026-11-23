package com.alimapps.senbombardir.di

import com.alimapps.senbombardir.utils.analytics.Analytics
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val appModule = module {

    single { Analytics(application = androidApplication()) }
}
