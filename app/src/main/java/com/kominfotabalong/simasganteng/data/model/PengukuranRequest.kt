package com.kominfotabalong.simasganteng.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PengukuranRequest(
    var tanggal_pengisian: String = "",
    var lila: String = "",
    var lingkar_kepala: String = "",
    var berat_anak: String = "",
    var tinggi_anak: String = "",
    var status_laporan: String = "",
    var cara_ukur: String = "",
) : Parcelable