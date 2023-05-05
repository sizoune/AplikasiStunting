package com.kominfotabalong.simasganteng.ui.component

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.kominfotabalong.simasganteng.ui.destinations.AddLaporanScreenDestination
import com.kominfotabalong.simasganteng.ui.destinations.Destination
import com.kominfotabalong.simasganteng.ui.destinations.DetailLaporanScreenDestination
import com.kominfotabalong.simasganteng.ui.destinations.ListLaporanMasukScreenDestination
import com.kominfotabalong.simasganteng.ui.destinations.ListLaporanRejectedScreenDestination
import com.kominfotabalong.simasganteng.ui.destinations.ListLaporanVerifiedScreenDestination
import com.kominfotabalong.simasganteng.ui.destinations.MapScreenDestination
import com.kominfotabalong.simasganteng.ui.destinations.PengukuranScreenDestination

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    destination: Destination,
    onBackClick: () -> Unit
) {
    val titleColor = if (isSystemInDarkTheme()) Color.White else Color.Black
    TopAppBar(
        title = {
            Text(
                text = destination.topBarTitle(),
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.ExtraBold),
                textAlign = TextAlign.Center,
                color = titleColor,
            )
        },
        navigationIcon = {
            IconButton(
                onClick = onBackClick,
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBackIos,
                    contentDescription = "Kembali",
                    tint = titleColor
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(MaterialTheme.colorScheme.primary)
    )
}

@Composable
fun Destination.topBarTitle(): String {
    return when (this) {
        AddLaporanScreenDestination -> {
            // Here you can also call another Composable on another file like TaskScreenTopBar
            // 👇 access the same viewmodel instance the screen is using, by passing the back stack entry
            "Tambah Laporan"
        }

        ListLaporanMasukScreenDestination -> {
            "Laporan Masuk"
        }

        ListLaporanVerifiedScreenDestination -> {
            "Balita Terverifikasi"
        }

        ListLaporanRejectedScreenDestination -> {
            "Laporan Ditolak"
        }

        DetailLaporanScreenDestination -> {
            "Periksa Laporan"
        }

        MapScreenDestination -> {
            "Peta Sebaran Stunting"
        }
        PengukuranScreenDestination -> {
            "Detail Balita"
        }

        else -> javaClass.simpleName.removeSuffix("Destination")
    }
}