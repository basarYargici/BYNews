package com.basar.bynews.ui.detail.route

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.basar.bynews.data.CacheStrategy.CacheFirstThenFetch
import com.basar.bynews.data.CacheStrategy.CacheOnly
import com.basar.bynews.ui.detail.screen.NewsDetailScreen
import com.basar.bynews.ui.detail.viewModel.NewsDetailViewModel
import com.basar.bynews.ui.list.route.rememberNetworkStateListener
import org.koin.androidx.compose.koinViewModel

@Composable
fun NewsDetailRoute(
    modifier: Modifier = Modifier,
    newsDetailViewModel: NewsDetailViewModel = koinViewModel(),
    onGoBack: () -> Unit
) {
    val uiModelState by newsDetailViewModel.newsListUIModel.collectAsState()
    val networkStateListener = rememberNetworkStateListener()
    val isNetworkAvailable by networkStateListener.isNetworkAvailable.collectAsState()
    DisposableEffect(Unit) {
        networkStateListener.startListening()
        onDispose { networkStateListener.stopListening() }
    }

    LaunchedEffect(key1 = Unit) {
        newsDetailViewModel.getNewsDetail(
            cacheStrategy = when {
                !isNetworkAvailable -> CacheOnly
                else -> CacheFirstThenFetch
            }
        )
    }

    NewsDetailScreen(
        uiModelState = uiModelState,
        modifier = modifier,
        onRetry = {
            newsDetailViewModel.getNewsDetail(
                cacheStrategy = when {
                    !isNetworkAvailable -> CacheOnly
                    else -> CacheFirstThenFetch
                }
            )
        },
        onGoBack = onGoBack
    )
}