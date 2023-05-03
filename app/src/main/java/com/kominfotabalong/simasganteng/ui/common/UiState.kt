package com.kominfotabalong.simasganteng.ui.common

sealed class UiState<out T : Any?> {

    object Loading : UiState<Nothing>()

    object Unauthorized : UiState<Nothing>()

    data class Error(val errorMessage: String) : UiState<Nothing>()

    data class Success<out T : Any>(val data: T) : UiState<T>()
}