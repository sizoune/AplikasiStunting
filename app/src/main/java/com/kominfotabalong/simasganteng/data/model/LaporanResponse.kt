package com.kominfotabalong.simasganteng.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LaporanResponse(
    val added_by: Int,
    val alamat: String,
    val anak_ke: Int,
    val berat: String,
    val created_at: String,
    val id: Int,
    val jenis_kelamin: String,
    val lat: Double,
    val lila: String,
    val lingkar_kepala: String,
    val lng: Double,
    val nama_anak: String,
    val nama_ortu: String,
    val nik_anak: String,
    val nik_ortu: String?="",
    val nomor_kk: String?="",
    val pkm_id: Int,
    val rt: String,
    val rw: String,
    val status: String,
    val tanggal: String,
    val tanggal_lahir: String,
    val tempat_lahir: String,
    val tinggi: String,
    val updated_at: String,
    val village_code: Long,
    val whatsapp: String
) : Parcelable