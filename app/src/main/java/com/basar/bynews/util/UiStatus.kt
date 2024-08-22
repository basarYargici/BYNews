package com.basar.bynews.util

sealed interface UiStatus<out T> {
    data object Loading : UiStatus<Nothing>
    data object Success : UiStatus<Nothing>
    data class Error(val message: String) : UiStatus<Nothing>
    data object Initial : UiStatus<Nothing>

    fun isLoading() = this is Loading
    fun isError() = this is Error
    fun isSuccess() = this is Success

    fun onSuccess(action: () -> Unit) {
        (this as? Success)?.let { action() }
    }

    fun onInitial(action: () -> Unit) {
        (this as? Initial)?.let { action() }
    }


    fun onError(action: (String) -> Unit) {
        (this as? Error)?.let { action(it.message) }
    }
}