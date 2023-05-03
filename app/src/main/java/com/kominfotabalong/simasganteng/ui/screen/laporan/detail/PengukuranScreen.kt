package com.kominfotabalong.simasganteng.ui.screen.laporan.detail

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.kominfotabalong.simasganteng.data.model.Kecamatan
import com.kominfotabalong.simasganteng.data.model.LaporanResponse
import com.kominfotabalong.simasganteng.ui.screen.laporan.LaporanViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Composable
@Destination
fun PengukuranScreen(
    viewModel: LaporanViewModel = hiltViewModel(),
    navigator: DestinationsNavigator,
    dataKecamatanString: List<Kecamatan>,
    dataLaporan: LaporanResponse,
    userToken: String,
) {

}