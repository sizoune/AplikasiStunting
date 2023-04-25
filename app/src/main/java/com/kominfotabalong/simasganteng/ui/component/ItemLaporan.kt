package com.kominfotabalong.simasganteng.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kominfotabalong.simasganteng.R
import com.kominfotabalong.simasganteng.ui.theme.SIMASGANTENGTheme

@Composable
fun ItemLaporan(
    modifier: Modifier = Modifier,
    idAnak: Int,
    jenisKelamin: String,
    nama: String,
    desa: String,
    alamat: String,
    onClick: (Int) -> Unit,
) {
    val childIcon = if (jenisKelamin == "L") R.drawable.male_child else R.drawable.female_child
    Row(modifier = modifier
        .fillMaxWidth()
        .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = rememberRipple(bounded = true), // You can also change the color and radius of the ripple
            onClick = { onClick(idAnak) }
        ), verticalAlignment = Alignment.Top) {
        Icon(
            painter = painterResource(childIcon),
            contentDescription = "child icon",
            tint = MaterialTheme.colorScheme.primary,
            modifier = modifier.size(48.dp)
        )
        Column(
            modifier = modifier
                .padding(start = 6.dp)
                .weight(1f)
        ) {

            Text(text = "Nama Anak", fontWeight = FontWeight.ExtraBold, fontSize = 10.sp)
            Text(
                text = nama,
                style = MaterialTheme.typography.labelMedium
            )

            Text(
                text = "Desa",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 10.sp,
                modifier = modifier.padding(top = 4.dp)
            )
            Text(
                text = desa,
                style = MaterialTheme.typography.labelMedium
            )

            Text(
                text = "Alamat",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 10.sp,
                modifier = modifier.padding(top = 4.dp)
            )
            Text(
                text = alamat,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelMedium
            )
        }
        IconButton(onClick = { onClick(idAnak) }) {
            Icon(
                imageVector = Icons.Filled.ArrowForwardIos,
                contentDescription = "detail",
                tint = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Preview
@Composable
fun ItemLaporanPreview() {
    SIMASGANTENGTheme {
        ItemLaporan(
            idAnak = 1,
            jenisKelamin = "L",
            nama = "wildan",
            desa = "ampukung",
            alamat = "jl. ampukung uhuy",
            onClick = {})
    }

}
