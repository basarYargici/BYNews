package com.basar.bynews.ui.list.viewModel

import com.basar.bynews.base.BaseViewModel
import com.basar.bynews.domain.GetNewsUseCase
import com.basar.bynews.model.uimodel.NewsListUIModel
import com.basar.bynews.util.BaseUIModel
import com.basar.bynews.util.PreferencesManager
import com.basar.bynews.util.setSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class NewsListViewModel(
    private val getNewsUseCase: GetNewsUseCase,
    private val preferencesManager: PreferencesManager
) : BaseViewModel() {

    private val _newsListUIModel = MutableStateFlow(BaseUIModel<NewsListUIModel>())
    val newsListUIModel = _newsListUIModel

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
        updateIsDescending()
        getNews() // Could be optimized to only sort the _newsListUIModel
    }

    private fun updateIsDescending() {
        _newsListUIModel.update { it.copy(data = it.data?.copy(isDescendingOrder = preferencesManager.isDescending)) }
    }
}
