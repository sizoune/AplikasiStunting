package com.kominfotabalong.simasganteng.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Meta(
    val lat: String="",
    val long: String="",
    val pos: String=""
):Parcelable