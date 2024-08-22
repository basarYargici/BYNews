package com.basar.bynews.ui.splash.route

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.basar.bynews.BYNewsScreens
import com.basar.bynews.ui.splash.screen.SplashScreen

@Composable
fun SplashRoute(
    modifier: Modifier = Modifier,
    navController: NavController,
) {
    SplashScreen(
        modifier = modifier,
        onNavigateToList = {
            navController.navigate(BYNewsScreens.NewsList.route) {
                popUpTo(BYNewsScreens.Splash.route) { inclusive = true }
            }
        }
    )
}