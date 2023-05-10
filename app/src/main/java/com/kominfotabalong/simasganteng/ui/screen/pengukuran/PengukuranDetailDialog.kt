package com.kominfotabalong.simasganteng.ui.screen.pengukuran

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.kominfotabalong.simasganteng.data.model.PengukuranResponse
import com.kominfotabalong.simasganteng.ui.screen.laporan.detail.CreateTextForm

@Composable
fun PengukuranDetailDialog(
    modifier: Modifier = Modifier,
    showDetail: Boolean,
    onDismiss: (Boolean) -> Unit,
    onEditClick: (PengukuranResponse) -> Unit,
    dataPengukuran: PengukuranResponse?,
) {
    if (showDetail)
        Dialog(onDismissRequest = { onDismiss(false) }) {
            dataPengukuran?.let {
                Card(
                    elevation = CardDefaults.cardElevation(10.dp),
                    shape = RoundedCornerShape(15.dp),
                    modifier = modifier.navigationBarsPadding()
                ) {
                    IconButton(
                        onClick = { onDismiss(false) },
                        modifier = modifier.align(Alignment.End)
                    ) {
                        Icon(imageVector = Icons.Filled.Close, contentDescription = "tutup")
                    }
                    Column(modifier = modifier.padding(start = 16.dp, end = 16.dp)) {
                        Text(
                            text = "Detail Pengukuran",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        CreateTextForm(
                            modifier = modifier.padding(top = 8.dp),
                            title = "Berat",
                            value = "${dataPengukuran.berat_anak} kg"
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
                            title = "Tinggi",
                            value = "${dataPengukuran.tinggi_anak} cm"
                        )
                        Text(
                            text = "Status Gizi Anak",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            modifier = modifier.padding(top = 24.dp)
                        )
                        CreateTextForm(
                            modifier = modifier.padding(top = 8.dp),
                            title = "Index BB/U",
                            value = "${dataPengukuran.zs_bb_per_u} SD"
                        )
                        Text(
                            text = dataPengukuran.bb_per_u,
                            style = MaterialTheme.typography.titleMedium,
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
                            title = "Index PB/U atau TB/U",
                            value = "${dataPengukuran.zs_tb_per_u} SD"
                        )
                        Text(
                            text = dataPengukuran.tb_per_u,
                            style = MaterialTheme.typography.titleMedium,
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
                            title = "Index BB/PB atau BB/TB",
                            value = "${dataPengukuran.zs_bb_per_tb} SD"
                        )
                        Text(
                            text = dataPengukuran.bb_per_tb,
                            style = MaterialTheme.typography.titleMedium,
                        )
                        TextButton(
                            onClick = {
                                onEditClick(dataPengukuran)
                                onDismiss(false)
                            }, modifier = modifier
                                .padding(top = 20.dp)
                                .align(Alignment.End)
                        ) {
                            Text(
                                text = "Ubah Data Pengukuran",
                                color = Color.Blue
                            )
                        }
                    }
                }
            }
        }
}