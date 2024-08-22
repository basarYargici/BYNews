package com.basar.bynews.di

import com.basar.bynews.ui.detail.viewModel.NewsDetailViewModel
import com.basar.bynews.ui.list.viewModel.NewsListViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { NewsListViewModel(get()) }
    viewModel { NewsDetailViewModel(get(),get()) }
}