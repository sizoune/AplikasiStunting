package com.kominfotabalong.simasganteng.data.model

data class SebaranResponse(
    val added_by: Int = -1,
    val balita_id: Int = -1,
    val bb_per_tb: String = "",
    val bb_per_u: String = "",
    val berat_anak: Double = 1.0,
    val cara_ukur: String = "",
    val created_at: String? = null,
    val lila: Double = 1.0,
    val lingkar_kepala: Double = 1.0,
    val pengukuran_id: Int = 1,
    val status_laporan: String = "",
    val tanggal: String = "",
    val tb_per_u: String = "",
    val tinggi_anak: Double = 1.0,
    val updated_at: String? = null,
    val zs_bb_per_tb: Double = 1.0,
    val zs_bb_per_u: Double = 1.0,
    val zs_tb_per_u: Double = 1.0,
    val balita: BalitaResponse = BalitaResponse()
) {
    override fun toString(): String {
        return "BB/TB : $bb_per_tb, " +
                "BB/U : $bb_per_u, " +
                "TB/U : $tb_per_u"
    }
}