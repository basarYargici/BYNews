package com.basar.bynews.ui.list.viewModel

import com.basar.bynews.base.BaseViewModel
import com.basar.bynews.domain.GetNewsUseCase
import com.basar.bynews.model.NewsResponse
import com.basar.bynews.util.BaseUIModel
import com.basar.bynews.util.setSuccess
import kotlinx.coroutines.flow.MutableStateFlow

class NewsListViewModel(
    private val getNewsUseCase: GetNewsUseCase
) : BaseViewModel() {

    private val _newsListUIModel = MutableStateFlow(BaseUIModel<NewsResponse>())
    val newsListUIModel = _newsListUIModel

    fun getNews() = launchIO {
        executeFlow(
            callFlow = getNewsUseCase.invoke(),
            uiModelFlow = _newsListUIModel
        ).collect { response ->
            _newsListUIModel.setSuccess(response)
        }
    }
}
