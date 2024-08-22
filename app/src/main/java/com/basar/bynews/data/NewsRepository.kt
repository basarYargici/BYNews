package com.basar.bynews.data

import com.basar.bynews.model.NewsDetailResponse
import com.basar.bynews.model.NewsResponse
import com.basar.bynews.network.NewsService
import com.basar.bynews.util.CacheManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class NewsRepository(
    private val newsService: NewsService,
    private val cacheManager: CacheManager
) {
    suspend fun getNews(): Flow<NewsResponse> = fetchData(
        getCachedData = { cacheManager.news },
        getFreshData = { newsService.getNews() },
        saveCache = { cacheManager.news = it }
    )

    suspend fun getNewsDetail(): Flow<NewsDetailResponse> = fetchData(
        getCachedData = { cacheManager.newsDetail },
        getFreshData = { newsService.getNewsDetail() },
        saveCache = { cacheManager.newsDetail = it }
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