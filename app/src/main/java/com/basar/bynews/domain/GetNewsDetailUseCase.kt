package com.basar.bynews.domain

import com.basar.bynews.data.CacheStrategy
import com.basar.bynews.data.NewsRepository
import com.basar.bynews.domain.uimodel.NewsDetailItemUIModel
import com.basar.bynews.domain.uimodel.toUIModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetNewsDetailUseCase(
    private val newsRepository: NewsRepository
) {
    suspend operator fun invoke(id: String, cacheStrategy: CacheStrategy): Flow<NewsDetailItemUIModel?> {
        return newsRepository.getNewsDetail(cacheStrategy).map { list ->
            val item = list.newsList.find { it.rssDataID == id }
            item?.toUIModel()
        }
    }
}