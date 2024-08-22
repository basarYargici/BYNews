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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.basar.bynews.R
import com.basar.bynews.extension.shimmerEffect
import com.basar.bynews.model.NewsItemResponse
import com.basar.bynews.model.NewsResponse
import com.basar.bynews.util.BaseUIModel
import com.basar.bynews.util.UiStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsListScreen(
    uiModelState: BaseUIModel<NewsResponse>,
    modifier: Modifier,
    onRetry: () -> Unit,
    onNavigateToDetail: (String) -> Unit = { }
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "News List") }
            )
        }
    ) { contentPadding ->
        val innerModifier = modifier.padding(
            top = contentPadding.calculateTopPadding(),
            bottom = contentPadding.calculateBottomPadding(),
        )
        when (uiModelState.status) {
            UiStatus.Loading -> LoadingState(innerModifier)
            UiStatus.Success -> SuccessState(innerModifier, uiModelState, onNavigateToDetail)
            is UiStatus.Error -> ErrorState(innerModifier, onRetry = onRetry)
            else -> EmptyState(innerModifier, onRetry = onRetry)
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

@Composable
private fun SuccessState(
    modifier: Modifier = Modifier,
    uiModelState: BaseUIModel<NewsResponse>,
    onNavigateToDetail: (id: String) -> Unit
) {
    LazyColumn(
        modifier = modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        uiModelState.data?.newsList?.let {
            items(it.size) { index ->
                val item = it[index]
                NewsListItem(
                    item = item,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onNavigateToDetail.invoke(item.rssDataID.orEmpty())
                        }
                )
            }
        }
    }
}

@Composable
private fun NewsListItem(item: NewsItemResponse, modifier: Modifier = Modifier) {
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
        Text(
            text = item.title.orEmpty(),
            modifier = Modifier
                .padding(start = 12.dp)
                .weight(1f)
        )
    }
}


@Composable
private fun ErrorState(
    modifier: Modifier = Modifier,
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
            contentDescription = "Error"
        )
        Text(text = "Error Occured While Fetching The Data.", modifier = Modifier.clickable { onRetry.invoke() })
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

@Preview
@Composable
fun NewsListScreenPreview() {
    NewsListScreen(
        uiModelState = BaseUIModel(
            status = UiStatus.Success,
            data = NewsResponse(
                newsList = listOf(
                    NewsItemResponse(
                        rssDataID = "1",
                        title = "Title 1",
                    ),
                    NewsItemResponse(
                        rssDataID = "2",
                        title = "Title 2",
                    ),
                    NewsItemResponse(
                        rssDataID = "3",
                        title = "Title 3",
                    )
                )
            )
        ),
        modifier = Modifier,
        onRetry = {}
    )
}

@Preview
@Composable
fun NewsListErrorPreview() {
    NewsListScreen(
        uiModelState = BaseUIModel(
            status = UiStatus.Error("Error"),
            data = null
        ),
        modifier = Modifier,
        onRetry = {}
    )
}