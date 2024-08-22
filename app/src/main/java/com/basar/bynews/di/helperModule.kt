package com.basar.bynews.di

import com.basar.bynews.util.PreferencesManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val helperModule = module {
    single { PreferencesManager(androidContext()) }
}