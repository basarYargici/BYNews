package com.basar.bynews.data

import com.basar.bynews.data.CacheStrategy.CacheFirstThenFetch
import com.basar.bynews.data.CacheStrategy.CacheOnly
import com.basar.bynews.data.CacheStrategy.FetchFirstThenCache
import com.basar.bynews.data.CacheStrategy.NoCache
import com.basar.bynews.model.reqres.NewsDetailResponse
import com.basar.bynews.model.reqres.NewsResponse
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
    suspend fun getNews(isForceFetch: Boolean): Flow<NewsResponse> = fetchData(
        cacheStrategy = if (isForceFetch) FetchFirstThenCache else CacheFirstThenFetch,
        getCachedData = { preferencesManager.news },
        getFreshData = { newsService.getNews() },
        saveCache = { preferencesManager.news = it }
    )

    suspend fun getNewsDetail(isForceFetch: Boolean): Flow<NewsDetailResponse> = fetchData(
        cacheStrategy = if (isForceFetch) FetchFirstThenCache else CacheFirstThenFetch,
        getCachedData = { preferencesManager.newsDetail },
        getFreshData = { newsService.getNewsDetail() },
        saveCache = { preferencesManager.newsDetail = it }
    )

    private fun <T : Any> fetchData(
        cacheStrategy: CacheStrategy = FetchFirstThenCache,
        getCachedData: () -> T?,
        getFreshData: suspend () -> T,
        saveCache: (T) -> Unit
    ): Flow<T> = flow {
        when (cacheStrategy) {
            NoCache -> {
                emit(getFreshData())
            }

            CacheOnly -> {
                getCachedData()?.let { cacheData -> emit(cacheData) } ?: throw NoSuchElementException(
                    "No cached data available"
                )
            }

            CacheFirstThenFetch -> {
                getCachedData()?.let { cacheData -> emit(cacheData) } ?: run {
                    getFreshData().also { freshData ->
                        saveCache(freshData)
                        emit(freshData)
                    }
                }
            }

            FetchFirstThenCache -> {
                getFreshData().also { freshData ->
                    saveCache(freshData)
                    emit(freshData)
                }
            }
        }
    }.flowOn(Dispatchers.IO)

}

sealed class CacheStrategy {
    data object NoCache : CacheStrategy()
    data object CacheOnly : CacheStrategy()
    data object CacheFirstThenFetch : CacheStrategy()
    data object FetchFirstThenCache : CacheStrategy()
}
