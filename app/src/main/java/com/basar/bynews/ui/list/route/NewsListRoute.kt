package com.basar.bynews.ui.list.route

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.basar.bynews.data.CacheStrategy.CacheFirstThenFetch
import com.basar.bynews.data.CacheStrategy.CacheOnly
import com.basar.bynews.data.CacheStrategy.FetchFirstThenCache
import com.basar.bynews.ui.list.screen.NewsListScreen
import com.basar.bynews.ui.list.viewModel.NewsListViewModel
import com.basar.bynews.util.NetworkStateListener
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

const val NEWS_DETAIL_ID = "newsDetailId"

@Composable
fun NewsListRoute(
    modifier: Modifier = Modifier,
    newsListViewModel: NewsListViewModel = koinViewModel(),
    onNavigateToDetail: (String) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val uiModelState by newsListViewModel.newsListUIModel.collectAsState()
    val networkStateListener = rememberNetworkStateListener()
    val isNetworkAvailable by networkStateListener.isNetworkAvailable.collectAsState()
    val context = LocalContext.current

    DisposableEffect(Unit) {
        networkStateListener.startListening()
        onDispose { networkStateListener.stopListening() }
    }

    LaunchedEffect(isNetworkAvailable) {
        if (!isNetworkAvailable) {
            scope.launch {
                Toast.makeText(
                    context,
                    "No network connection\nCache last updated ${newsListViewModel.getLatestSyncTime()} before",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    LaunchedEffect(key1 = Unit) {
        uiModelState.status.onInitial {
            newsListViewModel.getNews(
                cacheStrategy = when {
                    !isNetworkAvailable -> CacheOnly
                    else -> CacheFirstThenFetch
                }
            )
        }
    }

    NewsListScreen(
        uiModelState = uiModelState,
        modifier = modifier,
        onRetry = { forceToFetch ->
            newsListViewModel.getNews(
                cacheStrategy = when {
                    !isNetworkAvailable -> CacheOnly
                    forceToFetch -> FetchFirstThenCache
                    else -> CacheFirstThenFetch
                }
            )
        },
        onToggleSort = { newsListViewModel.toggleSortOrderAndRefresh() },
        onClearCache = { newsListViewModel.clearCache() },
        onGetPreferencesSize = { newsListViewModel.getPreferencesSize() },
        onNavigateToDetail = { id ->
            val route = "newsDetail?$NEWS_DETAIL_ID=$id"
            onNavigateToDetail(route)
        }
    )
}

@Composable
fun rememberNetworkStateListener(): NetworkStateListener {
    val context = LocalContext.current
    return remember { NetworkStateListener(context) }
}
