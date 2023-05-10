package com.kominfotabalong.simasganteng.ui.screen.pengukuran

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kominfotabalong.simasganteng.R
import com.kominfotabalong.simasganteng.data.model.BalitaResponse
import com.kominfotabalong.simasganteng.data.model.Kecamatan
import com.kominfotabalong.simasganteng.data.model.PengukuranRequest
import com.kominfotabalong.simasganteng.data.model.PengukuranResponse
import com.kominfotabalong.simasganteng.ui.common.UiState
import com.kominfotabalong.simasganteng.ui.component.ItemPengukuran
import com.kominfotabalong.simasganteng.ui.component.Loading
import com.kominfotabalong.simasganteng.ui.component.NoData
import com.kominfotabalong.simasganteng.ui.component.ShowSnackbarWithAction
import com.kominfotabalong.simasganteng.ui.destinations.LogoutHandlerDestination
import com.kominfotabalong.simasganteng.ui.destinations.PengukuranInputDestination
import com.kominfotabalong.simasganteng.ui.screen.laporan.detail.CreateTextForm
import com.kominfotabalong.simasganteng.ui.theme.Green800
import com.kominfotabalong.simasganteng.util.toDate
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient

@Composable
@Destination
fun PengukuranScreen(
    modifier: Modifier = Modifier,
    viewModel: PengukuranViewModel,
    navigator: DestinationsNavigator,
    dataKecamatanString: List<Kecamatan>,
    dataBalita: BalitaResponse,
    userToken: String,
    snackbarHostState: SnackbarHostState,
    resultRecipient: ResultRecipient<PengukuranInputDestination, Boolean>
) {

    var isExpanded by remember {
        mutableStateOf(false)
    }
    var isLoading by remember {
        mutableStateOf(false)
    }
    var isRefreshed by remember {
        mutableStateOf(false)
    }
    var dataPengukuran by remember {
        mutableStateOf(listOf<PengukuranResponse>())
    }
    var showDetail by remember {
        mutableStateOf(false)
    }
    var clickedPengukuranID by remember {
        mutableStateOf(-1)
    }

    resultRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {
            }

            is NavResult.Value -> {
                if (result.value) {
                    isRefreshed = true
                    viewModel.getDaftarPengukuran(userToken, dataBalita.balita_id)
                }
            }
        }
    }

    if (dataPengukuran.isEmpty() && !isRefreshed)
        LaunchedEffect(key1 = Unit, block = {
            viewModel.getDaftarPengukuran(userToken, dataBalita.balita_id)
        })

    viewModel.ukurState.collectAsStateWithLifecycle().value.let { uiState ->
        when (uiState) {

            is UiState.Loading -> {
                isLoading = true
            }

            is UiState.Success -> {
                isLoading = false
                uiState.data.data?.let {
                    dataPengukuran = it
                }
            }

            is UiState.Error -> {
                isLoading = false
                ShowSnackbarWithAction(
                    snackbarHostState = snackbarHostState,
                    errorMsg = uiState.errorMessage,
                    onRetryClick = {
                        viewModel.getDaftarPengukuran(userToken, dataBalita.balita_id)
                    },
                )
            }

            is UiState.Unauthorized -> {
                navigator.navigate(
                    LogoutHandlerDestination("Sesi login anda expired!")
                )
            }
        }
    }

    PengukuranDetailDialog(
        showDetail = showDetail,
        onDismiss = { showDetail = it },
        onEditClick = { data ->
            navigator.navigate(
                PengukuranInputDestination(
                    isUpdate = true,
                    userToken = userToken,
                    currentRequest = PengukuranRequest(
                        data.tanggal,
                        data.lila.toString(),
                        data.lingkar_kepala.toString(),
                        data.berat_anak.toString(),
                        data.tinggi_anak.toString(),
                        data.status_laporan,
                        data.cara_ukur
                    ),
                    balitaID = data.balita_id,
                    pengukuranID = data.pengukuran_id
                )
            )
        },
        dataPengukuran = dataPengukuran.find { it.pengukuran_id == clickedPengukuranID }
    )

    ConstraintLayout(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        val (anakTitle, anakContent, pengukuranTitle, pengukuranContent, btnAddPengukuran) = createRefs()
        val bornDate = dataBalita.tanggal_lahir.toDate("yyyy-MM-dd")
        val context = LocalContext.current

        Text(
            text = "Data Balita",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
            modifier = modifier
                .constrainAs(anakTitle) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    width = Dimension.fillToConstraints
                }
                .padding(bottom = 8.dp)
        )

        Card(
            modifier = modifier
                .constrainAs(anakContent) {
                    top.linkTo(anakTitle.bottom)
                    width = Dimension.fillToConstraints
                }
                .zIndex(2f),
            elevation = CardDefaults.cardElevation(10.dp),
            shape = RoundedCornerShape(20.dp),
        ) {
            Column(
                modifier = modifier
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp)

            ) {
                CreateTextForm(
                    modifier = modifier,
                    title = "Nama Anak",
                    value = dataBalita.nama_anak
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
                    value = dataBalita.anak_ke.toString()
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
                    value = if (dataBalita.jenis_kelamin == "L") "Laki Laki" else "Perempuan"
                )

                if (!isExpanded)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = modifier.clickable { isExpanded = !isExpanded }) {
                        Spacer(modifier = modifier.weight(1f))
                        Text(
                            text = "Lihat Detail",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold),
                        )
                        IconButton(
                            onClick = { isExpanded = !isExpanded },
                        ) {
                            Icon(
                                imageVector = Icons.Filled.KeyboardArrowDown,
                                contentDescription = "Detail"
                            )
                        }
                    }

                AnimatedVisibility(visible = isExpanded) {
                    Column {
                        Divider(
                            modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp, bottom = 8.dp),
                            thickness = 1.dp,
                            color = Color.LightGray,
                        )
                        CreateTextForm(
                            modifier = modifier,
                            title = "Nama Orang Tua",
                            value = dataBalita.nama_ortu
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
                            value = dataBalita.nik_anak
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
                            value = dataBalita.tempat_lahir
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
                            value = dataBalita.alamat
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
                            value = "${dataBalita.rt} / ${dataBalita.rw}"
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
                                desa.code == dataBalita.village_code
                            }?.name ?: ""
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
                            value = dataBalita.whatsapp
                        )
                        if (dataBalita.whatsapp != "")
                            Row(modifier = modifier.padding(top = 16.dp)) {
                                Spacer(modifier = modifier.weight(1f))
                                Button(
                                    onClick = {
                                        context.startActivity(Intent().apply {
                                            action = Intent.ACTION_VIEW
                                            data =
                                                Uri.parse("https://api.whatsapp.com/send?phone=+62${dataBalita.whatsapp}")
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

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = modifier
                                .padding(top = 16.dp)
                                .clickable { isExpanded = !isExpanded }) {
                            Spacer(modifier = modifier.weight(1f))
                            Text(
                                text = "Lihat Sedikit",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold),
                            )
                            IconButton(
                                onClick = { isExpanded = !isExpanded },
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.KeyboardArrowUp,
                                    contentDescription = "Lihat Sedikit"
                                )
                            }
                        }
                    }
                }


            }
        }


        Text(text = "Data Pengukuran",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
            modifier = modifier
                .constrainAs(pengukuranTitle) {
                    top.linkTo(anakContent.bottom)
                    start.linkTo(parent.start)
                }
                .padding(top = 24.dp, bottom = 8.dp))

        Card(
            modifier = modifier
                .constrainAs(pengukuranContent) {
                    top.linkTo(pengukuranTitle.bottom)
                    width = Dimension.fillToConstraints
                },
            elevation = CardDefaults.cardElevation(10.dp),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = modifier
                    .padding(16.dp)

            ) {
                if (isLoading) Loading()
                else {
                    if (dataPengukuran.isNotEmpty())
                        dataPengukuran.forEachIndexed { index, data ->
                            ItemPengukuran(
                                tanggal = data.tanggal,
                                tinggi = data.tinggi_anak.toString(),
                                berat = data.berat_anak.toString(),
                            ) {
                                clickedPengukuranID = data.pengukuran_id
                                showDetail = true
                            }
                            if (index + 1 < dataPengukuran.size)
                                Divider(
                                    color = Color.LightGray,
                                    thickness = 1.dp,
                                    modifier = modifier.padding(top = 4.dp, bottom = 4.dp)
                                )
                        }
                    else
                        NoData(emptyDesc = "tidak ada data pengukuran!")

                }
            }
        }

        ExtendedFloatingActionButton(
            onClick = {
                navigator.navigate(
                    PengukuranInputDestination(
                        userToken = userToken,
                        currentRequest = PengukuranRequest(),
                        balitaID = dataBalita.balita_id
                    )
                )
            },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = if (isSystemInDarkTheme()) Color.White else Color.Black,
            modifier = modifier
                .constrainAs(btnAddPengukuran) {
                    if (!isExpanded && dataPengukuran.size < 5)
                        bottom.linkTo(parent.bottom)
                    else
                        top.linkTo(pengukuranContent.bottom)
                    end.linkTo(parent.end)

                }
                .padding(8.dp)
        ) {
            Icon(imageVector = Icons.Filled.Add, contentDescription = "tambah Pengukuran")
            Text(
                text = "Tambah Pengukuran",
                style = MaterialTheme.typography.labelMedium,
                modifier = modifier.padding(start = 6.dp)
            )
        }
    }
}

