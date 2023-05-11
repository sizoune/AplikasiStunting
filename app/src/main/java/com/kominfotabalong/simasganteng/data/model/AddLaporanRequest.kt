package com.kominfotabalong.simasganteng.data.model

import kotlin.reflect.KProperty0

interface HasStringProperties {
    val properties: List<KProperty0<String>>
}

data class AddLaporanRequest(
    var alamat: String = "",
    var anak_ke: String = "1",
    var berat: String = "",
    var jenis_kelamin: String = "",
    var lat: String = "-1",
    var lila: String = "",
    var tanggal: String = "",
    var lingkar_kepala: String = "",
    var lng: String = "-1",
    var nama_anak: String = "",
    var nama_ortu: String = "",
    var nik_anak: String = "",
    var nik_ortu: String = "",
    var nomor_kk: String = "",
    var pkm_id: String = "",
    var rt: String = "",
    var rw: String = "",
    var kecamatanCode: String = "",
    var tanggal_lahir: String = "",
    var tempat_lahir: String = "",
    var tinggi: String = "",
    var village_code: String = "",
    var whatsapp: String = ""
) : HasStringProperties {
    // getter function creating a new list of current values every time it's accessed
    override val properties =
        listOf(
            ::alamat,
            ::anak_ke,
            ::berat,
            ::jenis_kelamin,
            ::lat,
            ::lng,
            ::lila,
            ::tanggal,
            ::lingkar_kepala,
            ::nama_anak,
            ::nama_ortu,
            ::nik_anak,
            ::nik_ortu,
            ::nomor_kk,
            ::pkm_id,
            ::rt,
            ::rw,
            ::tanggal_lahir,
            ::tempat_lahir,
            ::tinggi,
            ::village_code,
            ::whatsapp
        )
}