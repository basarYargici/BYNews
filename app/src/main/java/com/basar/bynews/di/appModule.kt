package com.basar.bynews.di

val appModule = mutableListOf(
    remoteModule(),
    repositoryModule,
    domainModule,
    viewModelModule
)