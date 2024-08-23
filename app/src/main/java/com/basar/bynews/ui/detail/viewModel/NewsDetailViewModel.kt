package com.basar.bynews.ui.detail.viewModel

import androidx.lifecycle.SavedStateHandle
import com.basar.bynews.base.BaseViewModel
import com.basar.bynews.data.CacheStrategy
import com.basar.bynews.domain.GetNewsDetailUseCase
import com.basar.bynews.extension.isNull
import com.basar.bynews.domain.uimodel.NewsDetailItemUIModel
import com.basar.bynews.ui.list.route.NEWS_DETAIL_ID
import com.basar.bynews.domain.uimodel.BaseUIModel
import com.basar.bynews.domain.uimodel.setEmpty
import com.basar.bynews.domain.uimodel.setSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class NewsDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val getNewsDetailUseCase: GetNewsDetailUseCase
) : BaseViewModel() {

    private val selectedOrderId: StateFlow<String?> = savedStateHandle.getStateFlow(NEWS_DETAIL_ID, null)

    private val _newsListUIModel = MutableStateFlow(BaseUIModel<NewsDetailItemUIModel?>())
    val newsListUIModel = _newsListUIModel

    fun getNewsDetail(cacheStrategy: CacheStrategy) = launchIO {
        executeFlow(
            callFlow = getNewsDetailUseCase.invoke(id = selectedOrderId.value.orEmpty(), cacheStrategy = cacheStrategy),
            uiModelFlow = _newsListUIModel
        ).collect { result ->
            if (result.isNull()) {
                _newsListUIModel.setEmpty("Haber bulunamadı")
                return@collect
            }
            _newsListUIModel.setSuccess(result)
        }
    }
}
