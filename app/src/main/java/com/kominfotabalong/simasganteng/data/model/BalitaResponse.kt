package com.kominfotabalong.simasganteng.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BalitaResponse(
    val added_by: Int = -1,
    val alamat: String = "",
    val anak_ke: Int = -1,
    val balita_id: Int = -1,
    val berat_lahir: Int = -1,
    val created_at: String = "",
    val jenis_kelamin: String = "",
    val lat: Double = (-1).toDouble(),
    val lila_lahir: Int = -1,
    val lingkar_kepala_lahir: Int = -1,
    val lng: Double = (-1).toDouble(),
    val nama_anak: String = "",
    val nama_ortu: String = "",
    val nik_anak: String = "",
    val nik_ortu: String? = null,
    val no_kk: String = "",
    val pkm_id: Int = -1,
    val code: String = "",
    val rt: String = "",
    val rw: String = "",
    val status_stunting: String? = null,
    val tanggal_lahir: String = "",
    val tempat_lahir: String = "",
    val tinggi_lahir: Int = -1,
    val updated_at: String = "",
    val usia: Int = -1,
    val village_code: String = "",
    val whatsapp: String = "",
) : Parcelable