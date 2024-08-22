package com.basar.bynews.ui.list.route

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.basar.bynews.ui.list.screen.NewsListScreen
import com.basar.bynews.ui.list.viewModel.NewsListViewModel
import org.koin.androidx.compose.koinViewModel

const val NEWS_DETAIL_ID = "newsDetailId"

@Composable
fun NewsListRoute(
    modifier: Modifier = Modifier,
    newsListViewModel: NewsListViewModel = koinViewModel(),
    onNavigateToDetail: (String) -> Unit,
) {
    val uiModelState by newsListViewModel.newsListUIModel.collectAsState()

    LaunchedEffect(key1 = Unit) {
        uiModelState.status.onInitial {
            newsListViewModel.getNews()
        }
    }

    NewsListScreen(
        uiModelState = uiModelState,
        modifier = modifier,
        onRetry = { newsListViewModel.getNews(true) },
        onToggleSort = { newsListViewModel.toggleSortOrderAndRefresh() },
        onClearCache = { newsListViewModel.clearCache() },
        onGetPreferencesSize = { newsListViewModel.getPreferencesSize() },
        onNavigateToDetail = { id ->
            val route = "newsDetail?$NEWS_DETAIL_ID=$id"
            onNavigateToDetail(route)
        }
    )
}