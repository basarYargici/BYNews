package com.basar.bynews.domain.uimodel

import com.basar.bynews.model.NewsDetailItemResponse

data class NewsDetailItemUIModel(
    val rssDataID: String,
    val title: String,
    val description: String,
    val imageUrl: String,
    val link: String,
    val newsChannelName: String,
    val channelCategoryName: String
)

fun NewsDetailItemResponse.toUIModel() = NewsDetailItemUIModel(
    rssDataID = rssDataID,
    title = title,
    description = description,
    imageUrl = imageUrl,
    link = link,
    newsChannelName = newsChannelName,
    channelCategoryName = channelCategoryName
)
