package com.basar.bynews.domain.uimodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

data class BaseUIModel<T>(
    val data: T? = null,
    var status: UiStatus<T> = UiStatus.Initial
) {
    fun setLoading() = this.copy(status = UiStatus.Loading)
    fun setError(error: String) = this.copy(status = UiStatus.Error(error))
    fun setEmpty(message: String) = this.copy(status = UiStatus.Empty(message))
    fun setSuccess(data: T): BaseUIModel<T> = this.copy(data = data, status = UiStatus.Success)
}

fun <T> MutableStateFlow<BaseUIModel<T>>.setLoading() = update { it.setLoading() }
fun <T> MutableStateFlow<BaseUIModel<T>>.setError(error: String) = update { it.setError(error) }
fun <T> MutableStateFlow<BaseUIModel<T>>.setEmpty(message: String) = update { it.setEmpty(message) }
fun <T> MutableStateFlow<BaseUIModel<T>>.setSuccess(data: T) = update { it.setSuccess(data) }