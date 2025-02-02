package com.basar.bynews

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.basar.bynews.ui.detail.route.NewsDetailRoute
import com.basar.bynews.ui.list.route.NEWS_DETAIL_ID
import com.basar.bynews.ui.list.route.NewsListRoute
import com.basar.bynews.ui.splash.route.SplashRoute
import com.basar.bynews.ui.theme.BYNewsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BYNewsTheme {
                val navHostController = rememberNavController()
                NavHost(
                    navController = navHostController,
                    startDestination = BYNewsScreens.Splash.route
                ) {
                    navigation(navController = navHostController)
                }
            }
        }
    }
}


fun NavGraphBuilder.navigation(
    navController: NavController
) {
    composable(route = BYNewsScreens.Splash.route) {
        SplashRoute(modifier = Modifier, navController = navController)
    }

    composable(route = BYNewsScreens.NewsList.route) {
        NewsListRoute(
            modifier = Modifier,
            onNavigateToDetail = { route ->
                navController.navigate(route)
            }
        )
    }

    composable(
        route = BYNewsScreens.NewsDetail.route,
        arguments = listOf(
            navArgument(NEWS_DETAIL_ID) {
                defaultValue = null
                nullable = true
                type = NavType.StringType
            },
        ),
    ) {
        NewsDetailRoute(modifier = Modifier, onGoBack = { navController.popBackStack() })
    }
}