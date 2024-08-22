package com.basar.bynews.domain

import com.basar.bynews.data.NewsRepository
import com.basar.bynews.model.uimodel.NewsDetailItemUIModel
import com.basar.bynews.model.uimodel.toUIModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetNewsDetailUseCase(
    private val newsRepository: NewsRepository
) {
    suspend operator fun invoke(id: String, isForceFetch: Boolean): Flow<NewsDetailItemUIModel?> {
        return newsRepository.getNewsDetail(isForceFetch).map { list ->
            val item = list.newsList.find { it.rssDataID == id }
            item?.toUIModel()
        }
    }
}