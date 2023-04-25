package com.kominfotabalong.simasganteng.ui.screen.laporan.list

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.kominfotabalong.simasganteng.data.model.Kecamatan
import com.kominfotabalong.simasganteng.data.model.LoginResponse
import com.kominfotabalong.simasganteng.ui.component.ItemLaporan
import com.kominfotabalong.simasganteng.ui.component.Loading
import com.kominfotabalong.simasganteng.ui.component.NoData
import com.kominfotabalong.simasganteng.ui.component.ObserveLoggedUser
import com.kominfotabalong.simasganteng.ui.component.ShowSnackbarWithAction
import com.kominfotabalong.simasganteng.ui.destinations.LogoutHandlerDestination
import com.kominfotabalong.simasganteng.ui.screen.laporan.LaporanViewModel
import com.kominfotabalong.simasganteng.ui.screen.laporan.ObserveDataTabalong
import com.kominfotabalong.simasganteng.util.showToast
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Composable
@Destination
fun ListLaporanRejectedScreen(
    modifier: Modifier = Modifier,
    viewModel: LaporanViewModel = hiltViewModel(),
    navigator: DestinationsNavigator,
    snackbarHostState: SnackbarHostState,
) {
    val (showSnackBar, setShowSnackBar) = remember {
        mutableStateOf(false)
    }
    var dataKecamatan by remember {
        mutableStateOf(listOf<Kecamatan>())
    }
    var userData by remember {
        mutableStateOf(LoginResponse())
    }
    val context = LocalContext.current

    ObserveLoggedUser(getData = userData.token == "", onUserObserved = {
        userData = it
        viewModel.getTabalongDistricts(userData.token)
    }, onError = {
        context.showToast(it)
    })

    if (dataKecamatan.isEmpty() && userData.token != "")
        ObserveDataTabalong(
            viewModel = viewModel,
            onResultSuccess = {
                dataKecamatan = it
            },
            onResultError = { errorMsg ->
                ShowSnackbarWithAction(snackbarHostState = snackbarHostState,
                    errorMsg = errorMsg,
                    showSnackBar = showSnackBar,
                    onRetryClick = { viewModel.getTabalongDistricts(userData.token) },
                    onDismiss = { setShowSnackBar(it) })
            },
            onUnauthorized = { navigator.navigate(LogoutHandlerDestination("Sesi login anda telah berakhir!")) })

    val daftarLaporan = viewModel.getLaporan(userData.token, "ditolak").collectAsLazyPagingItems()


    LazyColumn(contentPadding = PaddingValues(8.dp)) {

        items(items = daftarLaporan, key = { it.id }) { data ->
            data?.let {
                println("dataKecamatan size = ${dataKecamatan.size}")
                val desa = dataKecamatan.asSequence().flatMap { dataKec ->
                    dataKec.villages
                }.find { desa ->
                    desa.code == it.village_code.toString()
                }
                ItemLaporan(
                    idAnak = it.id,
                    jenisKelamin = it.jenis_kelamin,
                    nama = it.nama_anak,
                    desa = desa?.name ?: "",
                    alamat = it.alamat,
                    onClick = {

                    }
                )
            }
            Divider(
                color = Color.LightGray,
                thickness = 1.dp,
                modifier = modifier.padding(top = 10.dp, bottom = 10.dp)
            )
        }

        val refreshState = daftarLaporan.loadState.refresh
        if (refreshState is LoadState.Loading)
            item {
                Loading()
            }
        else if (refreshState is LoadState.Error)
            item {
                val error = refreshState.error
                ShowSnackbarWithAction(snackbarHostState = snackbarHostState,
                    errorMsg = error.localizedMessage ?: "Terjadi kesalahn saat memuat data!",
                    showSnackBar = showSnackBar,
                    onRetryClick = { viewModel.getLaporan(userData.token, "ditolak") },
                    onDismiss = { setShowSnackBar(it) })
            }
        if (refreshState is LoadState.NotLoading && daftarLaporan.itemCount == 0)
            item {
                NoData(emptyDesc = "Tidak ada Data !")
            }
    }
}