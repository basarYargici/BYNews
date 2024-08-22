package com.basar.bynews.ui.list.route

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.basar.bynews.NEWS_DETAIL_ID
import com.basar.bynews.ui.list.screen.NewsListScreen
import com.basar.bynews.ui.list.viewModel.NewsListViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun NewsListRoute(
    modifier: Modifier = Modifier,
    newsListViewModel: NewsListViewModel = koinViewModel(),
    onNavigateToDetail: (String) -> Unit,
) {
    val uiModelState by newsListViewModel.newsListUIModel.collectAsState()
    val isDescendingOrder by newsListViewModel.isDescendingOrder.collectAsState()

    LaunchedEffect(key1 = Unit) {
        uiModelState.status.onInitial {
            newsListViewModel.getNews()
        }
    }

    NewsListScreen(
        uiModelState = uiModelState,
        isDescendingOrder = isDescendingOrder,
        modifier = modifier,
        onRetry = { newsListViewModel.getNews() },
        onToggleSort = { newsListViewModel.toggleSortOrderAndRefresh() },
        onNavigateToDetail = { id ->
            val route = "NEWS_DETAIL_ROUTE?$NEWS_DETAIL_ID=$id"
            onNavigateToDetail(route)
        }
    )
}