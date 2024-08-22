package com.basar.bynews.domain

import com.basar.bynews.data.NewsRepository
import com.basar.bynews.model.uimodel.NewsListUIModel
import com.basar.bynews.model.uimodel.toUIModel
import com.basar.bynews.util.PreferencesManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetNewsUseCase(
    private val newsRepository: NewsRepository,
    private val preferencesManager: PreferencesManager
) {
    suspend operator fun invoke(): Flow<NewsListUIModel> {
        return newsRepository.getNews().map { news ->
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