package com.basar.bynews.model

data class NewsDetailItemResponse(
    val rssDataID: String,
    val title: String,
    val description: String,
    val imageUrl: String,
    val link: String,
    val newsChannelName: String,
    val channelCategoryName: String
)