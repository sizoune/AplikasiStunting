package com.kominfotabalong.simasganteng.data.model

data class Kecamatan(
    val city_code: String,
    val code: String,
    val created_at: String,
    val id: String,
    val meta: Meta,
    val name: String,
    val updated_at: String,
    val villages: List<Village>
)