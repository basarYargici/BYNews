package com.basar.bynews

import com.basar.bynews.ui.list.route.NEWS_DETAIL_ID

sealed class BYNewsScreens(val route: String) {
    data object Splash : BYNewsScreens(route = "splash")
    data object NewsList : BYNewsScreens(route = "list")
    data object NewsDetail : BYNewsScreens(route = "newsDetail?$NEWS_DETAIL_ID={$NEWS_DETAIL_ID}")
}