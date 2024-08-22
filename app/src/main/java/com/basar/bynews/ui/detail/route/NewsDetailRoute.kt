package com.basar.bynews.ui.detail.route

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.basar.bynews.BYNewsScreens
import com.basar.bynews.ui.detail.screen.NewsDetailScreen
import com.basar.bynews.ui.detail.viewModel.NewsDetailViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun NewsDetailRoute(
    modifier: Modifier = Modifier,
    navController: NavController,
    newsDetailViewModel: NewsDetailViewModel = koinViewModel()
) {
    val uiModelState by newsDetailViewModel.newsListUIModel.collectAsState()

    LaunchedEffect(key1 = Unit) {
        newsDetailViewModel.getNewsDetail()
    }

    NewsDetailScreen(
        uiModelState = uiModelState,
        modifier = modifier,
        onRetry = { newsDetailViewModel.getNewsDetail() },
    )
}