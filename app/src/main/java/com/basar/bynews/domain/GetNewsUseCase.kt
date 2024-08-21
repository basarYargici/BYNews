package com.basar.bynews.domain

import com.basar.bynews.data.NewsRepository
import com.basar.bynews.model.NewsResponse
import kotlinx.coroutines.flow.Flow

class GetNewsUseCase(
    private val newsRepository: NewsRepository
) {
    suspend operator fun invoke(): Flow<NewsResponse> {
        return newsRepository.getNews()
    }
}