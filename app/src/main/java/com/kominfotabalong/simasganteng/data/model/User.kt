package com.kominfotabalong.simasganteng.data.model

data class User(
    val active: Boolean = false,
    val api_token: String? = "",
    val avatar: String? = "",
    val created_at: String = "",
    val email: String = "",
    val email_verified_at: String = "",
    val firebase_token: String? = "",
    val name: String = "",
    val pkm_id: String? = "",
    val role: String = "",
    val updated_at: String = "",
    val user_id: Int = -1,
    val username: String = "",
    val whatsapp: String? = ""
)