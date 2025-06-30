package com.alimapps.senbombardir

import android.app.Application
import com.alimapps.senbombardir.di.appModule
import com.alimapps.senbombardir.di.dataModule
import com.alimapps.senbombardir.di.uiModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin()
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