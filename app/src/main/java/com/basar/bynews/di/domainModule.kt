package com.basar.bynews.di

import com.basar.bynews.domain.GetNewsDetailUseCase
import com.basar.bynews.domain.GetNewsUseCase
import org.koin.dsl.module

val domainModule = module {
    factory { GetNewsUseCase(get()) }
    factory { GetNewsDetailUseCase(get()) }
}