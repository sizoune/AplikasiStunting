package com.kominfotabalong.simasganteng.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun ItemPengukuran(
    modifier: Modifier = Modifier,
    tanggal: String,
    tinggi: String,
    showTitle: Boolean = true,
    berat: String,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column {
            if (showTitle)
                Text(
                    text = "Tanggal Ukur",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = modifier
                )
            Text(
                text = tanggal,
                style = MaterialTheme.typography.titleMedium,
                modifier = modifier
            )

        }
        Column {
            if (showTitle)
                Text(
                    text = "Berat",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = modifier
                )
            Text(
                text = "$berat kg",
                style = MaterialTheme.typography.titleMedium,
                modifier = modifier
            )

        }
        Column {
            if (showTitle)
                Text(
                    text = "Tinggi",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = modifier
                )
            Text(
                text = "$tinggi cm",
                style = MaterialTheme.typography.titleMedium,
                modifier = modifier
            )
        }
        IconButton(onClick = onClick) {
            Icon(
                imageVector = Icons.Filled.ArrowForwardIos,
                contentDescription = "Lihat Detail"
            )
        }
    }
}