package com.basar.bynews.ui.splash.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.basar.bynews.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    modifier: Modifier,
    onNavigateToList: () -> Unit = { }
) {
    LaunchedEffect(key1 = Unit) {
        delay(1000L)
        onNavigateToList.invoke()
    }

    val isSystemInDarkTheme = isSystemInDarkTheme()
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = if (isSystemInDarkTheme) Color(0xFF1A1A1A) else Color(0xFFCECCCC))
    ) {
        Image(
            modifier = Modifier
                .align(Alignment.Center)
                .size(144.dp),
            painter = painterResource(id = R.drawable.ic_logo),
            colorFilter = ColorFilter.tint(if (isSystemInDarkTheme) Color(0xFFCECCCC) else Color(0xFF1A1A1A)),
            contentDescription = "splash_logo"
        )
    }
}
