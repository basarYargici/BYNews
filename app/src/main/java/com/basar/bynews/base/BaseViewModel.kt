package com.basar.bynews.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.basar.bynews.domain.uimodel.BaseUIModel
import com.basar.bynews.util.ErrorHandler
import com.basar.bynews.domain.uimodel.setError
import com.basar.bynews.domain.uimodel.setLoading
import com.basar.bynews.domain.uimodel.setSuccess
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

abstract class BaseViewModel : ViewModel(), KoinComponent {
    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        println("Handle $exception in CoroutineExceptionHandler")
    }

    protected fun launchIO(block: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) { block() }
    }

    fun <T> executeFlow(
        callFlow: Flow<T>,
        uiModelFlow: MutableStateFlow<BaseUIModel<T>>
    ): Flow<T> = callFlow
        .onStart {
            uiModelFlow.setLoading()
        }
        .catch { exception ->
            ErrorHandler.handle(exception) {
                uiModelFlow.setError(exception.message.orEmpty())
            }
        }
        .onEach { response ->
            uiModelFlow.setSuccess(response)
        }
}