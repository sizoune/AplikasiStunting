package com.kominfotabalong.simasganteng.data.model

sealed class GoogleAuthResponse<out T> {
    object Loading : GoogleAuthResponse<Nothing>()

    data class Success<out T>(
        val data: T?
    ) : GoogleAuthResponse<T>()

    data class Failure(
        val e: Exception
    ) : GoogleAuthResponse<Nothing>()
}