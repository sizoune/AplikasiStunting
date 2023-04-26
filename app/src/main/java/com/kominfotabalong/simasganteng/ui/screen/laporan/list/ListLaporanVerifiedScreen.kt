package com.kominfotabalong.simasganteng.ui.screen.laporan.list

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.google.gson.Gson
import com.kominfotabalong.simasganteng.data.model.Kecamatan
import com.kominfotabalong.simasganteng.data.model.LoginResponse
import com.kominfotabalong.simasganteng.ui.component.ItemLaporan
import com.kominfotabalong.simasganteng.ui.component.Loading
import com.kominfotabalong.simasganteng.ui.component.NoData
import com.kominfotabalong.simasganteng.ui.component.ShowSnackbarWithAction
import com.kominfotabalong.simasganteng.ui.destinations.DetailLaporanScreenDestination
import com.kominfotabalong.simasganteng.ui.destinations.LogoutHandlerDestination
import com.kominfotabalong.simasganteng.ui.screen.laporan.LaporanViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Composable
@Destination
fun ListLaporanVerifiedScreen(
    modifier: Modifier = Modifier.navigationBarsPadding(),
    viewModel: LaporanViewModel = hiltViewModel(),
    navigator: DestinationsNavigator,
    dataKecamatan: List<Kecamatan>,
    userData: LoginResponse,
    snackbarHostState: SnackbarHostState,
) {
    val (showSnackBar, setShowSnackBar) = remember {
        mutableStateOf(false)
    }

    val daftarLaporan =
        viewModel.getLaporan(userData.token, "terverifikasi").collectAsLazyPagingItems()


    LazyColumn(contentPadding = PaddingValues(8.dp)) {

        items(items = daftarLaporan, key = { it.id }) { data ->
            data?.let { dataReport ->
                println("dataKecamatan size = ${dataKecamatan.size}")
                val desa = dataKecamatan.asSequence().flatMap { dataKec ->
                    dataKec.villages
                }.find { desa ->
                    desa.code == dataReport.village_code.toString()
                }
                ItemLaporan(
                    idAnak = dataReport.id,
                    jenisKelamin = dataReport.jenis_kelamin,
                    nama = dataReport.nama_anak,
                    desa = desa?.name ?: "",
                    alamat = dataReport.alamat,
                    onClick = {
                        navigator.navigate(
                            DetailLaporanScreenDestination(
                                dataReport
                            )
                        )
                    }
                )
                Spacer(modifier = modifier.size(8.dp))
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
                    showSnackBar = showSnackBar,
                    onRetryClick = { viewModel.getLaporan(userData.token, "terverifikasi") },
                    onDismiss = { setShowSnackBar(it) })
            }
        if (refreshState is LoadState.NotLoading && daftarLaporan.itemCount == 0)
            item {
                NoData(emptyDesc = "Tidak ada Data !")
            }
    }
}