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

const val BASE_URL = "http://localhost"

fun remoteModule() = module {
    single {
        OkHttpClient.Builder()
            .addInterceptor(mockInterceptor())
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

private fun mockInterceptor(): Interceptor = MockNetworkInterceptor()
    .mock(
        "$BASE_URL/news",
        { newsResponse },
        200,
        2000
    )
    .mock(
        "$BASE_URL/newsDetail",
        { newsDetailResponse },
        200,
        2000
    )
