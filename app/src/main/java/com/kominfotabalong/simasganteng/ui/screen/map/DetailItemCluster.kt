package com.kominfotabalong.simasganteng.ui.screen.map

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.google.android.gms.maps.model.LatLng
import com.kominfotabalong.simasganteng.data.model.SebaranResponse
import com.kominfotabalong.simasganteng.ui.screen.laporan.detail.CreateTextForm
import com.kominfotabalong.simasganteng.util.getStatusColor

@Composable
fun DetailItemCluster(
    modifier: Modifier = Modifier,
    showDialog: Boolean,
    data: SebaranResponse,
    onDirectionClick: (LatLng) -> Unit,
    onWhatsApp: (String) -> Unit,
    onDismiss: () -> Unit
) {
    if (showDialog)
        Dialog(onDismissRequest = { onDismiss() }) {
            Card(
                elevation = CardDefaults.cardElevation(10.dp),
                shape = RoundedCornerShape(15.dp),
                modifier = modifier.navigationBarsPadding()
            ) {
                IconButton(
                    onClick = {
                        onDismiss()
                    },
                    modifier = modifier.align(Alignment.End)
                ) {
                    Icon(imageVector = Icons.Filled.Close, contentDescription = "tutup")
                }

                Column(
                    modifier = modifier
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                        .verticalScroll(
                            rememberScrollState()
                        )
                ) {
                    Text(
                        text = "Data Anak",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold)
                    )
                    CreateTextForm(
                        modifier = modifier.padding(top = 8.dp),
                        title = "NIK",
                        value = data.balita.nik_anak
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
                        title = "Nama",
                        value = data.balita.nama_anak
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
                        title = "Alamat",
                        value = data.balita.alamat
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
                        value = data.balita.tanggal_lahir
                    )
                    Text(
                        text = "Data Pengukuran",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                        modifier = modifier.padding(top = 24.dp)
                    )
                    CreateTextForm(
                        modifier = modifier.padding(top = 8.dp),
                        title = "Tanggal Pengukuran",
                        value = data.tanggal
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
                        title = "Panjang/Tinggi Badan",
                        value = "${data.tinggi_anak} cm",
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
                        title = "Berat Badan",
                        value = "${data.berat_anak} kg"
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
                        title = "BB/TB",
                        value = data.bb_per_tb,
                        valueColor = getStatusColor(data.bb_per_tb)
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
                        title = "BB/U",
                        value = data.bb_per_u,
                        valueColor = getStatusColor(data.bb_per_u)
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
                        title = "TB/U",
                        value = data.tb_per_u,
                        valueColor = getStatusColor(data.tb_per_u)
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
                        title = "Z Score PB/TB per Usia",
                        value = data.zs_tb_per_u.toString()
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
                        title = "Z Score BB per Usia",
                        value = data.zs_bb_per_u.toString()
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
                        title = "Z Score BB per PB/TB",
                        value = data.zs_bb_per_tb.toString()
                    )
                    Divider(
                        modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 8.dp),
                        thickness = 1.dp,
                        color = Color.LightGray,
                    )
                    Row(modifier = modifier.fillMaxWidth()) {
                        TextButton(onClick = {
                            onDirectionClick(
                                LatLng(
                                    data.balita.lat,
                                    data.balita.lng
                                )
                            )
                            onDismiss()
                        }, modifier = modifier.weight(1f)) {
                            Text(
                                text = "Petunjuk Arah",
                                color = if (isSystemInDarkTheme()) Color.White else Color.Black
                            )
                        }
                        if (data.balita.whatsapp != "")
                            TextButton(onClick = {
                                onWhatsApp(data.balita.whatsapp)
                                onDismiss()
                            }, modifier = modifier.weight(1f)) {
                                Text(
                                    text = "Hubungi Orang Tua",
                                    color = Color.Green
                                )
                            }
                    }
                }

            }
        }
}