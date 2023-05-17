package com.kominfotabalong.simasganteng.ui.screen.laporan.list

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.kominfotabalong.simasganteng.data.model.Kecamatan
import com.kominfotabalong.simasganteng.data.model.LoginResponse
import com.kominfotabalong.simasganteng.ui.component.ItemLaporan
import com.kominfotabalong.simasganteng.ui.component.Loading
import com.kominfotabalong.simasganteng.ui.component.NoData
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
    val submittedSearchText by viewModel.doCariBalita.collectAsStateWithLifecycle()
    val daftarLaporan =
        viewModel.getDaftarBalita(
            userData.token,
            if (submittedSearchText != "") submittedSearchText else null
        ).collectAsLazyPagingItems()

    LazyColumn(contentPadding = PaddingValues(8.dp), modifier = Modifier.navigationBarsPadding()) {
        items(
            count = daftarLaporan.itemCount,
            key = daftarLaporan.itemKey(key = { it.balita_id }),
            contentType = daftarLaporan.itemContentType()
        ) { index ->
            val item = daftarLaporan[index]
            item?.let { dataReport ->
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