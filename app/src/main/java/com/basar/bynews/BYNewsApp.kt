package com.basar.bynews

import android.app.Application
import com.basar.bynews.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin

class BYNewsApp : Application() {
    override fun onCreate() {
        super.onCreate()
        configurationKoin()
    }

    private fun configurationKoin() = startKoin {
        androidLogger()
        androidContext(this@BYNewsApp)
        koin.loadModules(appModule)
    }
}