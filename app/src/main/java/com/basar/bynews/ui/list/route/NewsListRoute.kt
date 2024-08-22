package com.basar.bynews.ui.list.route

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.basar.bynews.NEWS_DETAIL_ID
import com.basar.bynews.ui.list.screen.NewsListScreen
import com.basar.bynews.ui.list.viewModel.NewsListViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun NewsListRoute(
    modifier: Modifier = Modifier,
    navController: NavController,
    newsListViewModel: NewsListViewModel = koinViewModel()
) {
    val uiModelState by newsListViewModel.newsListUIModel.collectAsState()

    LaunchedEffect(key1 = Unit) {
        newsListViewModel.getNews()
    }

    NewsListScreen(
        uiModelState = uiModelState,
        modifier = modifier,
        onRetry = { newsListViewModel.getNews() },
        onNavigateToDetail = { id ->
            val route = "NEWS_DETAIL_ROUTE?$NEWS_DETAIL_ID=$id"
            navController.navigate(route)
        }
    )
}