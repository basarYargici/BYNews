package com.basar.bynews.di

import com.basar.bynews.data.newsDetailResponse
import com.basar.bynews.data.newsResponse
import com.basar.bynews.network.MockNetworkInterceptor
import com.basar.bynews.network.NewsService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val BASE_URL = "http://localhost"

fun remoteModule() = module {
    single {
        OkHttpClient.Builder()
            .addInterceptor(getMockInterceptor())
            .build()
    }

    single {
        Retrofit.Builder()
            .client(get<OkHttpClient>())
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    factory { get<Retrofit>().create(NewsService::class.java) }
}

private fun getMockInterceptor(): Interceptor = MockNetworkInterceptor()
    .mock(
        path = "$BASE_URL/news",
        body = newsResponse,
        status = 200,
        delayInMs = 2000
    )
    .mock(
        path = "$BASE_URL/newsDetail",
        body = newsDetailResponse,
        status = 200,
        delayInMs = 2000
    )
