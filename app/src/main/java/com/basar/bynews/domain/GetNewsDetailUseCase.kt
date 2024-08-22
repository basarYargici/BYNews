package com.basar.bynews.domain

import com.basar.bynews.data.NewsRepository
import com.basar.bynews.model.NewsDetailItemResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetNewsDetailUseCase(
    private val newsRepository: NewsRepository
) {
    suspend operator fun invoke(id: String): Flow<NewsDetailItemResponse?> {
        return newsRepository.getNewsDetail().map { list ->
            list.newsList.find { it.rssDataID == id }
        }
    }
}