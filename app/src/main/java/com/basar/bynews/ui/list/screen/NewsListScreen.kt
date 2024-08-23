package com.basar.bynews.ui.list.screen

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.basar.bynews.R
import com.basar.bynews.extension.formatDateAccordingToLocale
import com.basar.bynews.extension.isTrue
import com.basar.bynews.extension.orZero
import com.basar.bynews.extension.shimmerEffect
import com.basar.bynews.domain.uimodel.NewsItemUIModel
import com.basar.bynews.domain.uimodel.NewsListUIModel
import com.basar.bynews.domain.uimodel.BaseUIModel
import com.basar.bynews.domain.uimodel.UiStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsListScreen(
    uiModelState: BaseUIModel<NewsListUIModel>,
    modifier: Modifier,
    onRetry: (Boolean) -> Unit,
    onNavigateToDetail: (String) -> Unit = { },
    onToggleSort: () -> Unit,
    onClearCache: () -> Unit,
    onGetPreferencesSize: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "News List") },
                actions = {
                    IconButton(
                        onClick = {
                            onGetPreferencesSize()
                            menuExpanded = !menuExpanded
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = "More",
                        )
                    }

                    DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = if (uiModelState.data?.isDescendingOrder.isTrue()) {
                                        "Change to Oldest Top"
                                    } else {
                                        "Change to Newest Top"
                                    },
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                            },
                            onClick = {
                                menuExpanded = false
                                onToggleSort()
                            },
                        )
                        if (uiModelState.data?.cachedSize.orZero() > 0) {
                            DropdownMenuItem(
                                text = {
                                    Text("Clear Cache (KB): \n" + uiModelState.data?.cachedSize.toString())
                                },
                                onClick = {
                                    menuExpanded = false
                                    onClearCache()
                                },
                            )
                        }
                    }
                }
            )
        }
    ) { contentPadding ->
        val innerModifier = modifier.padding(
            top = contentPadding.calculateTopPadding(),
            bottom = contentPadding.calculateBottomPadding(),
        )
        when (uiModelState.status) {
            UiStatus.Loading -> LoadingState(innerModifier)
            UiStatus.Success -> SuccessState(
                modifier = innerModifier,
                uiModelState = uiModelState,
                onNavigateToDetail = onNavigateToDetail,
                onRefresh = { onRetry(true) }
            )

            is UiStatus.Error -> ErrorState(
                modifier = innerModifier,
                uiModelState = uiModelState,
                onRetry = { onRetry(true) }
            )

            else -> EmptyState(innerModifier, onRetry = { onRetry(true) })
        }
    }
}

@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(10) {
            ShimmerNewsListItem(
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun ShimmerNewsListItem(modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(156.dp, 128.dp)
                .clip(RoundedCornerShape(16.dp))
                .shimmerEffect()
        )
        Column(
            modifier = Modifier
                .padding(start = 12.dp)
                .weight(1f),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(48.dp)
                    .shimmerEffect()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(16.dp)
                    .shimmerEffect()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SuccessState(
    modifier: Modifier = Modifier,
    uiModelState: BaseUIModel<NewsListUIModel>,
    onRefresh: () -> Unit,
    onNavigateToDetail: (id: String) -> Unit,
) {
    val pullRefreshState = rememberPullToRefreshState()

    if (pullRefreshState.isRefreshing) {
        LaunchedEffect(Unit) {
            onRefresh()
        }
    }

    Box(
        modifier = Modifier.nestedScroll(pullRefreshState.nestedScrollConnection)
    ) {
        LazyColumn(
            modifier = modifier.padding(horizontal = 16.dp),
        ) {
            items(uiModelState.data?.newsList.orEmpty()) { item ->
                NewsListItem(
                    item = item,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToDetail(item.rssDataID.orEmpty()) }
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), thickness = (0.5).dp)
            }
        }
        PullToRefreshContainer(
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

@Composable
private fun NewsListItem(item: NewsItemUIModel, modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        AsyncImage(
            modifier = Modifier
                .size(156.dp, 128.dp)
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop,
            model = ImageRequest.Builder(LocalContext.current)
                .data(item.imageUrl)
                .crossfade(true)
                .build(),
            error = painterResource(R.drawable.ic_error),
            contentDescription = null,
            onLoading = { Log.d("AsyncImage", "Loading: ${item.imageUrl}") },
            onSuccess = { Log.d("AsyncImage", "Success: ${item.imageUrl}") },
            onError = { Log.e("AsyncImage", "Error loading ${item.imageUrl}", it.result.throwable) }
        )
        Column(
            modifier = Modifier
                .padding(start = 12.dp)
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = item.title.orEmpty(),
                style = MaterialTheme.typography.titleMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = item.pubDate.orEmpty().formatDateAccordingToLocale(),
                style = MaterialTheme.typography.bodyMedium,
            )

        }

    }
}

@Composable
private fun ErrorState(
    modifier: Modifier = Modifier,
    onRetry: () -> Unit,
    uiModelState: BaseUIModel<NewsListUIModel>
) {
    var message = "Error Occured While Fetching The Data."
    uiModelState.status.onError { message = it }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            modifier = Modifier.size(128.dp),
            tint = Color.Red,
            painter = painterResource(id = R.drawable.ic_error),
            contentDescription = "Error"
        )
        Text(text = message)
        OutlinedButton(onClick = { onRetry.invoke() }) {
            Text(text = "Click To Retry")
        }
    }
}

@Composable
private fun EmptyState(
    modifier: Modifier,
    onRetry: () -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            modifier = Modifier.size(128.dp),
            tint = Color.Red,
            painter = painterResource(id = R.drawable.ic_error),
            contentDescription = "Empty"
        )
        Text(text = "There is no data.")
        OutlinedButton(onClick = { onRetry.invoke() }) {
            Text(text = "Click To Retry")
        }
    }
}