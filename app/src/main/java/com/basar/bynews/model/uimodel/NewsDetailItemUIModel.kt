package com.basar.bynews.model.uimodel

data class NewsDetailItemUIModel(
    val rssDataID: String,
    val title: String,
    val description: String,
    val imageUrl: String,
    val link: String,
    val newsChannelName: String,
    val channelCategoryName: String
)