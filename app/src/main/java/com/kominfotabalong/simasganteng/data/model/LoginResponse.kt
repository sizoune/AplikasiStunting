package com.kominfotabalong.simasganteng.data.model

data class LoginResponse(
    val token: String = "",
    val user: User = User(),
)