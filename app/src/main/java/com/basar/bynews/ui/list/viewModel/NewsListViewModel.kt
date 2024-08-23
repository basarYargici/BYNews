package com.basar.bynews.ui.list.viewModel

import com.basar.bynews.base.BaseViewModel
import com.basar.bynews.data.CacheStrategy
import com.basar.bynews.data.CacheStrategy.CacheOnly
import com.basar.bynews.domain.GetNewsUseCase
import com.basar.bynews.extension.millisToRoundedSeconds
import com.basar.bynews.domain.uimodel.NewsListUIModel
import com.basar.bynews.domain.uimodel.BaseUIModel
import com.basar.bynews.data.PreferenceKey
import com.basar.bynews.data.PreferencesManager
import com.basar.bynews.domain.uimodel.setSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class NewsListViewModel(
    private val getNewsUseCase: GetNewsUseCase,
    private val preferencesManager: PreferencesManager
) : BaseViewModel() {

    private val _newsListUIModel = MutableStateFlow(BaseUIModel<NewsListUIModel>())
    val newsListUIModel = _newsListUIModel

    fun getNews(cacheStrategy: CacheStrategy) = launchIO {
        executeFlow(
            callFlow = getNewsUseCase.invoke(cacheStrategy),
            uiModelFlow = _newsListUIModel
        ).collect { result ->
            _newsListUIModel.setSuccess(result)
        }
    }

    fun toggleSortOrderAndRefresh() = launchIO {
        preferencesManager.isDescending = !preferencesManager.isDescending
        updateIsDescending()
        getNews(cacheStrategy = CacheOnly)
    }

    private fun updateIsDescending() {
        _newsListUIModel.update { it.copy(data = it.data?.copy(isDescendingOrder = preferencesManager.isDescending)) }
    }

    fun getPreferencesSize() {
        val kbSize = preferencesManager.getDetailedKBSize()
        _newsListUIModel.update { it.copy(data = it.data?.copy(cachedSize = kbSize)) }
    }

    fun getLatestSyncTime(): String {
        val lastUpdatedTime = preferencesManager.getLastUpdated(PreferenceKey.NEWS)
        val diff = (System.currentTimeMillis() - lastUpdatedTime)
        return diff.millisToRoundedSeconds().toString() + "s"
    }

    fun clearCache() {
        preferencesManager.clearAllCache()
        _newsListUIModel.update { it.copy(data = it.data?.copy(cachedSize = null)) }
    }
}
