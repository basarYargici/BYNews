package com.basar.bynews.ui.detail.screen

import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.basar.bynews.R
import com.basar.bynews.model.NewsDetailItemResponse
import com.basar.bynews.model.uimodel.NewsDetailItemUIModel
import com.basar.bynews.util.BaseUIModel
import com.basar.bynews.util.UiStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsDetailScreen(
    uiModelState: BaseUIModel<NewsDetailItemUIModel?>,
    modifier: Modifier = Modifier,
    onRetry: () -> Unit,
    onGoBack: () -> Unit
) {
    val context = LocalContext.current
    val backPressDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    var isReaderModeActive by remember { mutableStateOf(false) }
    val webView = remember {
        WebView(context).apply {
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                setLayerType(WebView.LAYER_TYPE_HARDWARE, null)
            }
            webViewClient = WebViewClient()
        }
    }

    LaunchedEffect(uiModelState.data?.link) {
        uiModelState.data?.link?.let { url ->
            webView.loadUrl(url)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail") },
                navigationIcon = {
                    IconButton(onClick = { backPressDispatcher?.onBackPressed() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { isReaderModeActive = !isReaderModeActive }) {
                        Icon(
                            painter = painterResource(
                                id = if (isReaderModeActive) R.drawable.ic_web else R.drawable.ic_reader_mode_24
                            ),
                            contentDescription = "Toggle Reader Mode"
                        )
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
                innerModifier,
                uiModelState.data,
                isReaderModeActive,
                webView
            )

            is UiStatus.Error -> ErrorState(innerModifier, onRetry)
            else -> EmptyState(innerModifier, onGoBack)
        }
    }


    // Clean up the WebView when the composable is disposed
    DisposableEffect(Unit) {
        onDispose {
            webView.stopLoading()
            webView.destroy()
        }
    }
}


@Composable
fun ReaderModeView(
    title: String,
    description: String,
    imageUrl: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(16.dp)),
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .crossfade(true)
                .build(),
            contentScale = ContentScale.Crop,
            error = painterResource(R.drawable.ic_error),
            contentDescription = null,
            onLoading = { Log.d("AsyncImage", "Loading: $imageUrl") },
            onSuccess = { Log.d("AsyncImage", "Success: $imageUrl") },
            onError = { Log.e("AsyncImage", "Error loading $imageUrl", it.result.throwable) }
        )
        Text(text = title, style = MaterialTheme.typography.headlineSmall)
        Text(text = description, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun SuccessState(
    modifier: Modifier = Modifier,
    data: NewsDetailItemUIModel?,
    isReaderModeActive: Boolean,
    webView: WebView
) {
    Box(modifier = modifier) {
        // Always keep the WebView in the composition, but control its visibility
        AndroidView(
            factory = { webView },
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(alpha = if (isReaderModeActive) 0f else 1f)
        )

        if (isReaderModeActive) {
            ReaderModeView(
                title = data?.title.orEmpty(),
                description = data?.description.orEmpty(),
                imageUrl = data?.imageUrl.orEmpty()
            )
        }
    }
}

@Composable
private fun ErrorState(
    modifier: Modifier = Modifier,
    onRetry: () -> Unit
) {
    StateLayout(
        modifier = modifier,
        iconRes = R.drawable.ic_error,
        message = "Error Occurred While Fetching The Detail Data.",
        buttonText = "Click To Retry",
        onButtonClick = onRetry
    )
}

@Composable
private fun EmptyState(
    modifier: Modifier = Modifier,
    onGoBack: () -> Unit
) {
    StateLayout(
        modifier = modifier,
        iconRes = R.drawable.ic_explore_off,
        message = "There is no data.",
        buttonText = "Click To Go Back",
        onButtonClick = onGoBack
    )
}

@Composable
private fun StateLayout(
    modifier: Modifier = Modifier,
    iconRes: Int,
    message: String,
    buttonText: String,
    onButtonClick: () -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = "Empty",
            tint = Color.Red,
            modifier = Modifier.size(128.dp)
        )
        Text(text = message, style = MaterialTheme.typography.bodyLarge)
        OutlinedButton(onClick = onButtonClick) {
            Text(text = buttonText)
        }
    }
}