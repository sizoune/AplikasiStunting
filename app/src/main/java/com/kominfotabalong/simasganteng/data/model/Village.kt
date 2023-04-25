package com.kominfotabalong.simasganteng.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Village(
    val code: String,
    val created_at: String,
    val district_code: String,
    val id: String,
    val meta: Meta,
    val name: String,
    val updated_at: String
):Parcelable