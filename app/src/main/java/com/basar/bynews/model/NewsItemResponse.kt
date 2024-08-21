package com.basar.bynews.model

data class NewsItemResponse(
    val rssDataID: String,
    val title: String,
    val imageUrl: String,
    val pubDate: String
)