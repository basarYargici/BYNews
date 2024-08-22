package com.basar.bynews.model.uimodel

import com.basar.bynews.model.NewsItemResponse

data class NewsItemUIModel(
    val rssDataID: String? = null,
    val title: String? = null,
    val imageUrl: String? = null,
    val pubDate: String? = null
)

fun NewsItemResponse.toUIModel() = NewsItemUIModel(
    rssDataID = rssDataID,
    title = title,
    imageUrl = imageUrl,
    pubDate = pubDate
)
