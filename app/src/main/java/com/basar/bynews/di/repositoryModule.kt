package com.basar.bynews.di

import com.basar.bynews.data.NewsRepository
import org.koin.dsl.module

val repositoryModule = module {
    single { NewsRepository(get(), get()) }
}