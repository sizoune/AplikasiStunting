package com.kominfotabalong.simasganteng.ui.screen.laporan.list

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.kominfotabalong.simasganteng.data.model.Kecamatan
import com.kominfotabalong.simasganteng.data.model.LoginResponse
import com.kominfotabalong.simasganteng.ui.component.ItemLaporan
import com.kominfotabalong.simasganteng.ui.component.Loading
import com.kominfotabalong.simasganteng.ui.component.NoData
import com.kominfotabalong.simasganteng.ui.component.OutlinedTextFieldComp
import com.kominfotabalong.simasganteng.ui.component.ShowSnackbarWithAction
import com.kominfotabalong.simasganteng.ui.destinations.LogoutHandlerDestination
import com.kominfotabalong.simasganteng.ui.destinations.PengukuranScreenDestination
import com.kominfotabalong.simasganteng.ui.screen.laporan.LaporanViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Composable
@Destination
fun ListLaporanVerifiedScreen(
    modifier: Modifier = Modifier,
    viewModel: LaporanViewModel,
    navigator: DestinationsNavigator,
    dataKecamatan: List<Kecamatan>,
    userData: LoginResponse,
    snackbarHostState: SnackbarHostState,
) {
    val doSearchBalita by viewModel.doCariBalita.collectAsStateWithLifecycle()
    var searchText by remember {
        mutableStateOf("")
    }
    val daftarLaporan =
        viewModel.getDaftarBalita(userData.token, if (searchText != "") searchText else null)
            .collectAsLazyPagingItems()

    ShowDialogSearch(
        showDialog = doSearchBalita,
        currentSearch = searchText,
        onSearchClick = { search ->
            searchText = search
        },
        onDismiss = { viewModel.setDoCariBalita(false) })

    LazyColumn(contentPadding = PaddingValues(8.dp), modifier = Modifier.navigationBarsPadding()) {

        items(items = daftarLaporan, key = { it.balita_id }) { data ->
            data?.let { dataReport ->
                println("dataKecamatan size = ${dataKecamatan.size}")
                val desa = dataKecamatan.asSequence().flatMap { dataKec ->
                    dataKec.villages
                }.find { desa ->
                    desa.code == dataReport.village_code
                }
                ItemLaporan(
                    idAnak = dataReport.balita_id,
                    jenisKelamin = dataReport.jenis_kelamin,
                    nama = dataReport.nama_anak,
                    desa = desa?.name ?: "",
                    alamat = dataReport.alamat,
                    onClick = {
                        navigator.navigate(
                            PengukuranScreenDestination(
                                userToken = userData.token,
                                dataBalita = dataReport
                            )
                        )
                    }
                )
                Spacer(modifier = modifier.size(16.dp))
            }
        }
        val refreshState = daftarLaporan.loadState.refresh
        if (refreshState is LoadState.Loading)
            item {
                Loading()
            }
        else if (refreshState is LoadState.Error)
            item {
                val error = refreshState.error
                error.localizedMessage?.let { errorMsg ->
                    if (errorMsg.lowercase().contains("authenticated")) navigator.navigate(
                        LogoutHandlerDestination("Sesi login anda expired!")
                    )
                }
                ShowSnackbarWithAction(snackbarHostState = snackbarHostState,
                    errorMsg = error.localizedMessage ?: "Terjadi kesalahn saat memuat data!",
                    onRetryClick = { daftarLaporan.refresh() })
            }
        if (refreshState is LoadState.NotLoading && daftarLaporan.itemCount == 0)
            item {
                NoData(emptyDesc = "Tidak ada Data !")
            }
    }
}

@Composable
fun ShowDialogSearch(
    modifier: Modifier = Modifier,
    showDialog: Boolean,
    currentSearch:String,
    onSearchClick: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var nik by remember { mutableStateOf(currentSearch) }

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
                Column(modifier = modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) {
                    Text(
                        text = "Pencarian Balita",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )

                    OutlinedTextFieldComp(
                        placeholderText = "Masukkan NIK/Nama Anak",
                        query = nik,
                        onQueryChange = { newText ->
                            nik = newText
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                onSearchClick(nik)
                                onDismiss()
                            }
                        ),
                        modifier = modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            onSearchClick(nik)
                            onDismiss()
                        }, modifier = modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        Text(
                            text = "Cari",
                            color = if (isSystemInDarkTheme()) Color.White else Color.Black
                        )
                    }
                }
            }
        }
}