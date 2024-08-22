package com.basar.bynews.domain

import com.basar.bynews.data.NewsRepository
import com.basar.bynews.model.NewsResponse
import com.basar.bynews.util.PreferencesManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetNewsUseCase(
    private val newsRepository: NewsRepository,
    private val preferencesManager: PreferencesManager
) {
    suspend operator fun invoke(): Flow<NewsResponse> {
        return newsRepository.getNews().map { news ->
            val isDescending = preferencesManager.isDescending
            news.copy(
                newsList = news.newsList?.sortedBy { it.pubDate }.let {
                    if (isDescending) it?.reversed() else it
                }
            )
        }
    }
}