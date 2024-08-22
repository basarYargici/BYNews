package com.basar.bynews.data

import com.basar.bynews.model.NewsDetailResponse
import com.basar.bynews.model.NewsResponse
import com.basar.bynews.network.NewsService
import com.basar.bynews.util.PreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class NewsRepository(
    private val newsService: NewsService,
    private val preferencesManager: PreferencesManager
) {
    suspend fun getNews(): Flow<NewsResponse> = fetchData(
        getCachedData = { preferencesManager.news },
        getFreshData = { newsService.getNews() },
        saveCache = { preferencesManager.news = it }
    )

    suspend fun getNewsDetail(): Flow<NewsDetailResponse> = fetchData(
        getCachedData = { preferencesManager.newsDetail },
        getFreshData = { newsService.getNewsDetail() },
        saveCache = { preferencesManager.newsDetail = it }
    )

    private fun <T : Any> fetchData(
        getCachedData: () -> T?,
        getFreshData: suspend () -> T,
        saveCache: (T) -> Unit
    ): Flow<T> = flow {
        val cachedData = getCachedData()
        cachedData?.let { emit(it) }

        try {
            val freshData = getFreshData()
            saveCache(freshData)
            if (cachedData != freshData) {
                emit(freshData)
            }
        } catch (e: Exception) {
            if (cachedData != null) {
                emit(cachedData)
            } else {
                throw e
            }
        }
    }.flowOn(Dispatchers.IO)
}