package com.kominfotabalong.simasganteng.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun ColorScheme.isLight() = this.background.luminance() > 0.5

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun createJsonRequestBody(vararg params: Pair<String, *>) =
    JSONObject(mapOf(*params)).toString()
        .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

fun Context.openMap(lat: Double, lng: Double) {
    val gmmIntentUri: Uri =
        Uri.parse("google.navigation:q=$lat,$lng")
    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
    mapIntent.setPackage("com.google.android.apps.maps")
    if (packageManager?.let { it1 -> mapIntent.resolveActivity(it1) } != null) {
        startActivity(mapIntent)
    } else {
        this.showToast("Aplikasi Google Maps belum terinstall di perangkat anda !")
    }
}

fun String.toDate(format: String): LocalDate {
    return LocalDate.parse(this, DateTimeFormatter.ofPattern(format))
}

fun getListOfMonth() = listOf(
    "Januari",
    "Februari",
    "Maret",
    "April",
    "Mei",
    "Juni",
    "Juli",
    "Agustus",
    "September",
    "Oktober",
    "November",
    "Desember"
)

fun getListOfGiziStatus() = listOf(
    "Berat Badan per Usia",
    "Tinggi / Panjang Badan per Usia",
    "Berat Badan per Tinggi / Panjang Badan"
)

fun getSelectedStatistik(tipe: String) =
    if (tipe == "Berat Badan per Usia") "bb_per_u"
    else if (tipe == "Tinggi / Panjang Badan per Usia") "tb_per_u"
    else "bb_per_tb"

fun getMonthIndex(selectedMonth: String): String {
    getListOfMonth().forEachIndexed { index, month ->
        if (month == selectedMonth) {
            return if (index + 1 < 10) "0${index + 1}" else (index + 1).toString()
        }
    }
    return ""
}

fun getStatusColor(status: String): Color {
    return if (status.lowercase() == "normal"
        || status.lowercase() == "gizi baik"
    )
        Color.Green
    else if (status.lowercase() == "kurang"
        || status.lowercase() == "pendek"
        || status.lowercase() == "gizi lebih"
        || status.lowercase() == "risiko gizi lebih"
    ) Color.Yellow
    else Color.Red
}