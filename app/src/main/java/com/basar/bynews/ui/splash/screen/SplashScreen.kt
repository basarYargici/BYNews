package com.basar.bynews.ui.splash.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
        delay(2000L)
        onNavigateToList.invoke()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = Color(0xFF1A1A1A))
    ) {
        Image(
            modifier = Modifier
                .align(Alignment.Center)
                .size(144.dp),
            painter = painterResource(id = R.drawable.ic_logo),
            contentDescription = "splash_logo"
        )
    }
}
