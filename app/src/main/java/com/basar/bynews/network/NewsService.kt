package com.basar.bynews.network

import com.basar.bynews.model.NewsDetailResponse
import com.basar.bynews.model.NewsResponse
import retrofit2.http.GET

interface NewsService {

    @GET("news")
    suspend fun getNews(): NewsResponse

    @GET("newsDetail")
    suspend fun getNewsDetail(): NewsDetailResponse
}
