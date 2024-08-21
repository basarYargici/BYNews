package com.basar.bynews.ui.list.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.basar.bynews.model.NewsResponse
import com.basar.bynews.ui.list.viewModel.BaseUIModel
import com.basar.bynews.ui.list.viewModel.UiStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsListScreen(
    uiModelState: BaseUIModel<NewsResponse>,
    modifier: Modifier,
    onNavigateToDetail: () -> Unit = { }
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "News List")
                }
            )
        }
    ) { contentPadding ->

        when (uiModelState.status) {
            UiStatus.Loading -> {
                Text(modifier = modifier.padding(contentPadding), text = "Loading" + uiModelState.data.toString())
            }

            UiStatus.Success -> {
                Text(modifier = modifier.padding(contentPadding), text = "Success" + uiModelState.data.toString())
            }

            is UiStatus.Error -> {
                Text(modifier = modifier.padding(contentPadding), text = "Error" + uiModelState.data.toString())
            }

            else -> {
                Text(modifier = modifier.padding(contentPadding), text = "Empty" + uiModelState.data.toString())
            }
        }

    }

}
