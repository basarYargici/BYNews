package com.basar.bynews.domain

import com.basar.bynews.data.NewsRepository
import com.basar.bynews.model.NewsDetailResponse
import kotlinx.coroutines.flow.Flow

class GetNewsDetailUseCase(
    private val newsRepository: NewsRepository
) {
    // TODO: iterate on response to find the requested news, rssDataID should be used to find the news
    suspend operator fun invoke(): Flow<NewsDetailResponse> {
        return newsRepository.getNewsDetail()
    }
}