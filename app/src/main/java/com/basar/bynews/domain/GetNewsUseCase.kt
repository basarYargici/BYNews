package com.basar.bynews.domain

import com.basar.bynews.data.CacheStrategy
import com.basar.bynews.data.NewsRepository
import com.basar.bynews.domain.uimodel.NewsListUIModel
import com.basar.bynews.domain.uimodel.toUIModel
import com.basar.bynews.data.PreferencesManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetNewsUseCase(
    private val newsRepository: NewsRepository,
    private val preferencesManager: PreferencesManager
) {
    suspend operator fun invoke(cacheStrategy: CacheStrategy): Flow<NewsListUIModel> {
        return newsRepository.getNews(cacheStrategy).map { news ->
            val sortedNews = news.newsList?.let { list ->
                if (preferencesManager.isDescending) {
                    list.sortedByDescending { it.pubDate }
                } else {
                    list.sortedBy { it.pubDate }
                }
            }
            NewsListUIModel(
                newsList = sortedNews?.map { it.toUIModel() },
                isDescendingOrder = preferencesManager.isDescending
            )
        }
    }
}