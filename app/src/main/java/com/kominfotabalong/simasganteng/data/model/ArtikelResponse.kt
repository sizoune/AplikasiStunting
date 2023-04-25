package com.kominfotabalong.simasganteng.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ArtikelResponse(
    val aktif: Boolean,
    val created_at: String,
    val gambar: String,
    val id: Int,
    val isi: String,
    val judul: String,
    val sumber: String,
    val updated_at: String
) : Parcelable