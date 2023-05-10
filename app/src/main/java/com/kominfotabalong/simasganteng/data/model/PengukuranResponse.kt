package com.kominfotabalong.simasganteng.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PengukuranResponse(
    val added_by: Int,
    val balita_id: Int,
    val bb_per_tb: String,
    val bb_per_u: String,
    val berat_anak: Double,
    val cara_ukur: String,
    val created_at: String?,
    val lila: Double,
    val lingkar_kepala: Double,
    val pengukuran_id: Int,
    val status_laporan: String,
    val tanggal: String,
    val tb_per_u: String,
    val tinggi_anak: Double,
    val updated_at: String?,
    val zs_bb_per_tb: Double,
    val zs_bb_per_u: Double,
    val zs_tb_per_u: Double
) : Parcelable