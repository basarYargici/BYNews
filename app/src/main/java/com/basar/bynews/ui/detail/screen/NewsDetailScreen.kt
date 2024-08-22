package com.basar.bynews.ui.detail.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.basar.bynews.R
import com.basar.bynews.model.NewsDetailItemResponse
import com.basar.bynews.util.BaseUIModel
import com.basar.bynews.util.UiStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsDetailScreen(
    uiModelState: BaseUIModel<NewsDetailItemResponse?>,
    modifier: Modifier,
    onRetry: () -> Unit,
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
            UiStatus.Success -> SuccessState(innerModifier, uiModelState)
            is UiStatus.Error -> ErrorState(innerModifier, onRetry = onRetry)
            else -> EmptyState(innerModifier, onRetry = onRetry)
        }
    }
}

@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    Text(modifier = modifier,text = "Loading...")
}

@Composable
private fun SuccessState(
    modifier: Modifier = Modifier,
    uiModelState: BaseUIModel<NewsDetailItemResponse?>,
) {
    val item = uiModelState.data
    Text(modifier = modifier, text = item?.title.orEmpty())
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
        Text(text = "Error Occured While Fetching The Detail Data.", modifier = Modifier.clickable { onRetry.invoke() })
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