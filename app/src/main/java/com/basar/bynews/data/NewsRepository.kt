package com.basar.bynews.data

import com.basar.bynews.network.NewsService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class NewsRepository(
    private val newsService: NewsService
) {
    suspend fun getNews() = sendRequest {
        newsService.getNews()
    }

    suspend fun getNewsDetail() = sendRequest {
        newsService.getNewsDetail()
    }
}


fun <T : Any> sendRequest(call: suspend () -> T) = flow {
    emit(call.invoke())
}.flowOn(Dispatchers.IO)
