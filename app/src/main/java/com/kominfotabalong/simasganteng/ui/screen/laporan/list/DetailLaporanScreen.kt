package com.kominfotabalong.simasganteng.ui.screen.laporan.list

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.kominfotabalong.simasganteng.R
import com.kominfotabalong.simasganteng.data.model.Kecamatan
import com.kominfotabalong.simasganteng.data.model.LaporanResponse
import com.kominfotabalong.simasganteng.ui.theme.Green800
import com.kominfotabalong.simasganteng.util.toDate
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.spec.DestinationStyle

@Destination(style = FullScreenDialog::class)
@Composable
fun DetailLaporanScreen(
    modifier: Modifier = Modifier.systemBarsPadding(),
    dataKecamatanString: List<Kecamatan>,
    dataLaporan: LaporanResponse,
    navigator: DestinationsNavigator,
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(16.dp),
    ) {
        ConstraintLayout(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            val (header, anakTitle, anakContent, domisiliTitle, domisiliContent, dataAnakTitle, dataAnakContent, ortuTitle, ortuContent) = createRefs()
            val bornDate = dataLaporan.tanggal_lahir.toDate("yyyy-MM-dd")
            val fillDate = dataLaporan.tanggal.toDate("yyyy-MM-dd")
            val context = LocalContext.current

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier
                    .constrainAs(header) {
                        top.linkTo(parent.top)
                        width = Dimension.fillToConstraints
                    }
                    .height(IntrinsicSize.Max),
            ) {
                Text(
                    text = "Periksa Laporan",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                    modifier = modifier.weight(1f)
                )
                IconButton(onClick = { navigator.navigateUp() }) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Kembali",
                        modifier = Modifier
                    )
                }
            }

            Text(text = "Identitas Anak",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                modifier = modifier
                    .constrainAs(anakTitle) {
                        top.linkTo(header.bottom)
                        start.linkTo(parent.start)
                    }
                    .padding(top = 16.dp, bottom = 8.dp))

            Card(
                modifier = modifier
                    .constrainAs(anakContent) {
                        top.linkTo(anakTitle.bottom)
                        width = Dimension.fillToConstraints
                    },
                elevation = CardDefaults.cardElevation(10.dp),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = modifier
                        .padding(16.dp)

                ) {
                    CreateTextForm(
                        modifier = modifier,
                        title = "Nama Anak",
                        value = dataLaporan.nama_anak
                    )
                    Divider(
                        modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 8.dp),
                        thickness = 1.dp,
                        color = Color.LightGray,
                    )
                    CreateTextForm(
                        modifier = modifier,
                        title = "Anak Ke Berapa",
                        value = dataLaporan.anak_ke.toString()
                    )
                    Divider(
                        modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 8.dp),
                        thickness = 1.dp,
                        color = Color.LightGray,
                    )
                    CreateTextForm(
                        modifier = modifier,
                        title = "Jenis Kelamin",
                        value = if (dataLaporan.jenis_kelamin == "L") "Laki Laki" else "Perempuan"
                    )
                    Divider(
                        modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 8.dp),
                        thickness = 1.dp,
                        color = Color.LightGray,
                    )
                    CreateTextForm(
                        modifier = modifier,
                        title = "NIK Anak",
                        value = dataLaporan.nik_anak
                    )
                    Divider(
                        modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 8.dp),
                        thickness = 1.dp,
                        color = Color.LightGray,
                    )
                    CreateTextForm(
                        modifier = modifier,
                        title = "Tempat Lahir",
                        value = dataLaporan.tempat_lahir
                    )
                    Divider(
                        modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 8.dp),
                        thickness = 1.dp,
                        color = Color.LightGray,
                    )
                    CreateTextForm(
                        modifier = modifier,
                        title = "Tanggal Lahir",
                        value = "${
                            bornDate.dayOfWeek.toString().lowercase().capitalize(Locale.current)
                        }, ${bornDate.dayOfMonth} ${
                            bornDate.month.toString().lowercase().capitalize(Locale.current)
                        } ${bornDate.year}"
                    )
                }
            }

            Text(text = "Domisili Anak",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                modifier = modifier
                    .constrainAs(domisiliTitle) {
                        top.linkTo(anakContent.bottom)
                        start.linkTo(parent.start)
                    }
                    .padding(top = 24.dp, bottom = 8.dp))

            Card(
                modifier = modifier
                    .constrainAs(domisiliContent) {
                        top.linkTo(domisiliTitle.bottom)
                        width = Dimension.fillToConstraints
                    },
                elevation = CardDefaults.cardElevation(10.dp),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = modifier
                        .padding(16.dp)

                ) {
                    CreateTextForm(
                        modifier = modifier,
                        title = "Alamat",
                        value = dataLaporan.alamat
                    )
                    Divider(
                        modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 8.dp),
                        thickness = 1.dp,
                        color = Color.LightGray,
                    )
                    CreateTextForm(
                        modifier = modifier,
                        title = "RT / RW",
                        value = "${dataLaporan.rt} / ${dataLaporan.rw}"
                    )
                    Divider(
                        modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 8.dp),
                        thickness = 1.dp,
                        color = Color.LightGray,
                    )
                    CreateTextForm(
                        modifier = modifier,
                        title = "Desa / Kelurahan",
                        value = dataKecamatanString.asSequence().flatMap { dataKec ->
                            dataKec.villages
                        }.find { desa ->
                            desa.code == dataLaporan.village_code.toString()
                        }?.name ?: ""
                    )
                }
            }

            Text(text = "Data Anak",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                modifier = modifier
                    .constrainAs(dataAnakTitle) {
                        top.linkTo(domisiliContent.bottom)
                        start.linkTo(parent.start)
                    }
                    .padding(top = 24.dp, bottom = 8.dp))
            Card(
                modifier = modifier
                    .constrainAs(dataAnakContent) {
                        top.linkTo(dataAnakTitle.bottom)
                        width = Dimension.fillToConstraints
                    },
                elevation = CardDefaults.cardElevation(10.dp),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = modifier
                        .padding(16.dp)

                ) {
                    CreateTextForm(
                        modifier = modifier,
                        title = "Berat Badan",
                        value = "${dataLaporan.berat} kg"
                    )
                    Divider(
                        modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 8.dp),
                        thickness = 1.dp,
                        color = Color.LightGray,
                    )
                    CreateTextForm(
                        modifier = modifier,
                        title = "Tinggi Badan",
                        value = "${dataLaporan.tinggi} cm"
                    )
                    Divider(
                        modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 8.dp),
                        thickness = 1.dp,
                        color = Color.LightGray,
                    )
                    CreateTextForm(
                        modifier = modifier,
                        title = "Lingkar Lengan",
                        value = "${dataLaporan.lila} cm"
                    )
                    Divider(
                        modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 8.dp),
                        thickness = 1.dp,
                        color = Color.LightGray,
                    )
                    CreateTextForm(
                        modifier = modifier,
                        title = "Lingkar Kepala",
                        value = "${dataLaporan.lingkar_kepala} cm"
                    )
                    Divider(
                        modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 8.dp),
                        thickness = 1.dp,
                        color = Color.LightGray,
                    )
                    CreateTextForm(
                        modifier = modifier,
                        title = "Tanggal Pengisian",
                        value = "${
                            fillDate.dayOfWeek.toString().lowercase().capitalize(Locale.current)
                        }, ${fillDate.dayOfMonth} ${
                            fillDate.month.toString().lowercase().capitalize(Locale.current)
                        } ${fillDate.year}"
                    )
                }
            }

            Text(text = "Identitas Orang Tua",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                modifier = modifier
                    .constrainAs(ortuTitle) {
                        top.linkTo(dataAnakContent.bottom)
                        start.linkTo(parent.start)
                    }
                    .padding(top = 24.dp, bottom = 8.dp))
            Card(
                modifier = modifier
                    .constrainAs(ortuContent) {
                        top.linkTo(ortuTitle.bottom)
                        width = Dimension.fillToConstraints
                    },
                elevation = CardDefaults.cardElevation(10.dp),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = modifier
                        .padding(16.dp)

                ) {
                    CreateTextForm(
                        modifier = modifier,
                        title = "Nama Orang Tua",
                        value = dataLaporan.nama_ortu
                    )
                    Divider(
                        modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 8.dp),
                        thickness = 1.dp,
                        color = Color.LightGray,
                    )
                    CreateTextForm(
                        modifier = modifier,
                        title = "NIK Orang Tua",
                        value = dataLaporan.nik_ortu ?: ""
                    )
                    Divider(
                        modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 8.dp),
                        thickness = 1.dp,
                        color = Color.LightGray,
                    )
                    CreateTextForm(
                        modifier = modifier,
                        title = "Nomor KK",
                        value = dataLaporan.nomor_kk
                    )
                    Divider(
                        modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 8.dp),
                        thickness = 1.dp,
                        color = Color.LightGray,
                    )
                    CreateTextForm(
                        modifier = modifier,
                        title = "No. Whatsapp",
                        value = dataLaporan.whatsapp
                    )
                    Row(modifier = modifier.padding(top = 16.dp)) {
                        Spacer(modifier = modifier.weight(1f))
                        Button(
                            onClick = {
                                context.startActivity(Intent().apply {
                                    action = Intent.ACTION_VIEW
                                    data =
                                        Uri.parse("https://api.whatsapp.com/send?phone=+62${dataLaporan.whatsapp}")
                                })
                            },
                            colors = ButtonDefaults.buttonColors(Green800),
                            modifier = modifier.weight(1f)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.whatsapp),
                                contentDescription = "chat",
                                modifier = modifier.size(24.dp)
                            )
                            Text(
                                text = "Kirim Pesan",
                                modifier = modifier.padding(start = 8.dp),
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }

}

@Composable
fun CreateTextForm(modifier: Modifier, title: String, value: String) {
    Row(modifier = modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
            modifier = modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            modifier = modifier.weight(1f)
        )
    }
}

object FullScreenDialog : DestinationStyle.Dialog {
    override val properties = DialogProperties(
        usePlatformDefaultWidth = false
    )
}