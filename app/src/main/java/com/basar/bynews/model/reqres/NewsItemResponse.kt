package com.basar.bynews.model.reqres

data class NewsItemResponse(
    val rssDataID: String? = null,
    val title: String? = null,
    val imageUrl: String? = null,
    val pubDate: String? = null
)