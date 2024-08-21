package com.basar.bynews

import androidx.navigation.NamedNavArgument

sealed class BYNewsScreens(
    val route: String,
    val navArguments: List<NamedNavArgument> = emptyList()
) {
    data object Splash : BYNewsScreens(route = "splash")
    data object NewsList : BYNewsScreens(route = "list")
    data object NewsDetail : BYNewsScreens(route = "detail")
}