package com.basar.bynews.ui.list.viewModel

import com.basar.bynews.base.BaseViewModel
import com.basar.bynews.domain.GetNewsUseCase
import com.basar.bynews.model.NewsResponse
import com.basar.bynews.util.BaseUIModel
import com.basar.bynews.util.PreferencesManager
import com.basar.bynews.util.setSuccess
import kotlinx.coroutines.flow.MutableStateFlow

class NewsListViewModel(
    private val getNewsUseCase: GetNewsUseCase,
    private val preferencesManager: PreferencesManager
) : BaseViewModel() {

    // The conversion from API response to UI model should occur within the use case layer.
    // The ViewModel should only work with already converted UI models, not raw API responses. But time..
    private val _newsListUIModel = MutableStateFlow(BaseUIModel<NewsResponse>())
    val newsListUIModel = _newsListUIModel

    private val _isDescendingOrder = MutableStateFlow(preferencesManager.isDescending) // Should be in UIModel
    val isDescendingOrder = _isDescendingOrder

    fun getNews() = launchIO {
        executeFlow(
            callFlow = getNewsUseCase.invoke(),
            uiModelFlow = _newsListUIModel
        ).collect { response ->
            _newsListUIModel.setSuccess(response)
        }
    }

    fun toggleSortOrderAndRefresh() = launchIO {
        preferencesManager.isDescending = !preferencesManager.isDescending
        _isDescendingOrder.value = preferencesManager.isDescending
        getNews() // Could be optimized to only sort the _newsListUIModel
    }
}
