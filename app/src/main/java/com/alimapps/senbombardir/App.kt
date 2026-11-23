package com.alimapps.senbombardir

import android.app.Application
import com.alimapps.senbombardir.di.appModule
import com.alimapps.senbombardir.di.dataModule
import com.alimapps.senbombardir.di.uiModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import com.alimapps.senbombardir.utils.analytics.Analytics
import org.koin.android.ext.android.get

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin()
        get<Analytics>()
    }

    private fun initKoin() {
        val koinModules = arrayListOf(
            appModule,
            uiModule,
            dataModule,
        )
        startKoin {
            androidLogger()
            androidContext(applicationContext)
            modules(koinModules)
        }
    }
}