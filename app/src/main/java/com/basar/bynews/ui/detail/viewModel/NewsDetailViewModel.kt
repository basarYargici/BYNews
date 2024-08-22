package com.basar.bynews.ui.detail.viewModel

import androidx.lifecycle.SavedStateHandle
import com.basar.bynews.NEWS_DETAIL_ID
import com.basar.bynews.base.BaseViewModel
import com.basar.bynews.domain.GetNewsDetailUseCase
import com.basar.bynews.extension.isNull
import com.basar.bynews.model.NewsDetailItemResponse
import com.basar.bynews.util.BaseUIModel
import com.basar.bynews.util.setError
import com.basar.bynews.util.setSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class NewsDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val getNewsDetailUseCase: GetNewsDetailUseCase
) : BaseViewModel() {

    private val selectedOrderId: StateFlow<String?> = savedStateHandle.getStateFlow(NEWS_DETAIL_ID, null)

    private val _newsListUIModel = MutableStateFlow(BaseUIModel<NewsDetailItemResponse?>())
    val newsListUIModel = _newsListUIModel

    fun getNewsDetail() = launchIO {
        executeFlow(
            callFlow = getNewsDetailUseCase.invoke(selectedOrderId.value.orEmpty()),
            uiModelFlow = _newsListUIModel
        ).collect { response ->
            if (response.isNull()) {
                _newsListUIModel.setError("Haber bulunamadı")
                return@collect
            }
            _newsListUIModel.setSuccess(response)
        }
    }
}
