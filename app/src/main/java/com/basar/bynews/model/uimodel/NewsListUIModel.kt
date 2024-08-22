package com.basar.bynews.model.uimodel

data class NewsListUIModel(
    val isDescendingOrder: Boolean? = false,
    val newsList: List<NewsItemUIModel>? = null
)