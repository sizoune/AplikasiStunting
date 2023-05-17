package com.kominfotabalong.simasganteng.ui.screen.laporan

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.maps.model.LatLng
import com.kominfotabalong.simasganteng.data.model.AddLaporanRequest
import com.kominfotabalong.simasganteng.data.model.Kecamatan
import com.kominfotabalong.simasganteng.data.model.LoginResponse
import com.kominfotabalong.simasganteng.data.model.PengukuranResponse
import com.kominfotabalong.simasganteng.data.model.PuskesmasResponse
import com.kominfotabalong.simasganteng.ui.common.UiState
import com.kominfotabalong.simasganteng.ui.component.Loading
import com.kominfotabalong.simasganteng.ui.component.ShowSnackbarWithAction
import com.kominfotabalong.simasganteng.ui.component.StepperComp
import com.kominfotabalong.simasganteng.ui.component.StuntingDialog
import com.kominfotabalong.simasganteng.ui.component.SuccessDialog
import com.kominfotabalong.simasganteng.ui.destinations.LogoutHandlerDestination
import com.kominfotabalong.simasganteng.ui.screen.laporan.tambah.LaporanAlamatContent
import com.kominfotabalong.simasganteng.ui.screen.laporan.tambah.NikSearchDialog
import com.kominfotabalong.simasganteng.util.showToast
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Composable
@Destination
fun AddLaporanScreen(
    modifier: Modifier = Modifier,
    viewModel: LaporanViewModel = hiltViewModel(),
    navigator: DestinationsNavigator,
    snackbarHostState: SnackbarHostState,
    userData: LoginResponse,
    dataKecamatan: List<Kecamatan>,
    dataPuskesmas: List<PuskesmasResponse>,
) {
    val context = LocalContext.current
    var laporanRequest by remember {
        mutableStateOf(AddLaporanRequest())
    }
    var isSubmitted by remember {
        mutableStateOf(false)
    }


    var currentStep by rememberSaveable { mutableStateOf(1) }
    val titleList = arrayListOf("Data Alamat", "Data Anak", "Data Orang Tua")

    BackHandler {
        if (currentStep != 1) currentStep--
        else navigator.navigateUp()
    }

    var showSuccessDialog by remember {
        mutableStateOf(true)
    }
    var collectDataAlamat by remember {
        mutableStateOf(false)
    }
    var collectDataAnak by remember {
        mutableStateOf(false)
    }
    var collectDataOrtu by remember {
        mutableStateOf(false)
    }

    val scrollState = rememberScrollState()

    NikSearchDialog(
        viewModel = viewModel,
        snackbarHostState = snackbarHostState,
        userData = userData,
        onCloseClick = {
            navigator.navigateUp()
        },
        onSearchResponse = {
            if (it.lat != "" && it.lng != "")
                viewModel.setLatLng(LatLng(it.lat.toDouble(), it.lng.toDouble()))
            laporanRequest = it
            viewModel.setSearchingStatustoTrue()
        },
        onUnauthorized = {
            navigator.navigate(LogoutHandlerDestination("Sesi login anda telah berakhir"))
        })

    ConstraintLayout(
        modifier = modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .verticalScroll(scrollState)
    ) {

        val (stepper, contentAlamat, contentAnak, contentOrtu, prevButton, nextButton) = createRefs()
        val stepperUnselectedColor =
            if (isSystemInDarkTheme()) Color.DarkGray else Color.LightGray

        StepperComp(
            numberOfSteps = titleList.size,
            currentStep = currentStep,
            stepDescriptionList = titleList,
            selectedColor = if (isSystemInDarkTheme()) Color.White else Color.Black,
            unSelectedColor = stepperUnselectedColor,
            modifier = modifier.constrainAs(stepper) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            }
        )

        AnimatedVisibility(
            currentStep == 1 && userData.token != "",
            enter = slideInHorizontally(),
            exit = slideOutHorizontally(),
            modifier = modifier.constrainAs(contentAlamat) {
                top.linkTo(stepper.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        ) {
            LaporanAlamatContent(
                getData = collectDataAlamat,
                viewModel = viewModel,
                currentRequest = laporanRequest,
                dataKecamatan = dataKecamatan,
                dataPuskesmas = dataPuskesmas,
                onNextClick = {
                    laporanRequest = it
                    collectDataAlamat = false
                    currentStep = 2
                },
            )
        }

        AnimatedVisibility(
            currentStep == 2,
            enter = slideInHorizontally(),
            exit = slideOutHorizontally(),
            modifier = modifier.constrainAs(contentAnak) {
                top.linkTo(stepper.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                height = Dimension.fillToConstraints
            }
        ) {
            LaporanAnakContent(
                viewModel = viewModel,
                getData = collectDataAnak,
                currentRequest = laporanRequest,
                onNextClick = {
                    laporanRequest = it
                    collectDataAnak = false
                    currentStep = 3
                }
            )
        }

        AnimatedVisibility(
            currentStep == 3,
            enter = slideInHorizontally(),
            exit = slideOutHorizontally(),
            modifier = modifier.constrainAs(contentOrtu) {
                top.linkTo(stepper.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        ) {
            LaporanOrtuContent(
                getData = collectDataOrtu,
                currentRequest = laporanRequest,
                viewModel = viewModel,
                onNextClick = {
                    laporanRequest = it
                    collectDataOrtu = false
                }
            )
        }

        AnimatedVisibility(visible = currentStep != 1, modifier = modifier
            .constrainAs(prevButton) {
                if (currentStep != 2)
                    bottom.linkTo(parent.bottom)
                else
                    top.linkTo(contentAnak.bottom)
                start.linkTo(parent.start)
            }
            .padding(16.dp)) {
            FloatingActionButton(
                onClick = {
                    if (currentStep != 1) currentStep--
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = if (isSystemInDarkTheme()) Color.White else Color.Black
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBackIos,
                    contentDescription = "selanjutnya",
                )
            }
        }

        AnimatedVisibility(visible = true, modifier = modifier
            .constrainAs(nextButton) {
                if (currentStep != 2)
                    bottom.linkTo(parent.bottom)
                else
                    top.linkTo(contentAnak.bottom)
                end.linkTo(parent.end)
                if (currentStep == titleList.size) {
                    start.linkTo(prevButton.end)
                    width = Dimension.fillToConstraints
                }
            }
            .padding(16.dp)) {
            FloatingActionButton(
                onClick = {
                    collectDataAlamat = currentStep == 1
                    collectDataAnak = currentStep == 2
                    collectDataOrtu = currentStep == 3
                    viewModel.collectData(currentStep)
                    if (currentStep >= titleList.size) {
                        println("reqData = $laporanRequest")
                        if (laporanRequest.properties.any { it.get().isBlank() }) {
                            context.showToast("Masih ada data yang kosong!, silahkan periksa kembali")
                            println(laporanRequest.properties.filter {
                                it.get().isBlank()
                            })
                        } else {
                            if (laporanRequest.lat != "-1" && laporanRequest.lng != "-1") {
                                isSubmitted = true
                                viewModel.addLaporan(userData.token, laporanRequest)
                            } else {
                                currentStep = 1
                                context.showToast("data koordinat masih kosong!, silahkan klik icon di bagian kanan alamat!")
                            }
                        }

                    }
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = if (isSystemInDarkTheme()) Color.White else Color.Black
            ) {
                if (currentStep == titleList.size)
                    Text(text = "Submit", textAlign = TextAlign.Center)
                if (currentStep < titleList.size)
                    Icon(
                        imageVector = Icons.Filled.ArrowForwardIos,
                        contentDescription = "selanjutnya",
                    )
            }
        }

    }

    if (isSubmitted)
        ObserveTambahLaporan(viewModel = viewModel, onResultSuccess = { returnData ->
            if (returnData.tb_per_u.lowercase() == "sangat pendek" || returnData.tb_per_u.lowercase() == "pendek")
                StuntingDialog(
                    showDialog = showSuccessDialog,
                    dialogDesc = "Terima kasih atas laporan anda!, namun perlu diperhatikan sistem kami mendeteksi bahwa anak anda tersuspeksi Stunting !, " +
                            "silahkan hubungi puskesmas terdekat untuk penanganan lebih lanjut!",
                    onDismiss = {
                        showSuccessDialog = it
                        navigator.navigateUp()
                    }
                )
            else
                SuccessDialog(
                    showDialog = showSuccessDialog,
                    dialogDesc = "Terima kasih atas laporan anda!, sistem kami mendeteksi bahwa anak anda TIDAK tersuspek Stunting!",
                    onDismiss = {
                        showSuccessDialog = it
                        navigator.navigateUp()
                    })
        }, onError = { errorMsg ->
            ShowSnackbarWithAction(
                snackbarHostState = snackbarHostState,
                errorMsg = errorMsg,
                onRetryClick = {
                    viewModel.addLaporan(userData.token, laporanRequest)
                },
            )

        }, onUnauthorized = {
            navigator.navigate(LogoutHandlerDestination("Sesi login anda telah berakhir"))
        })
}

@Composable
fun ObserveTambahLaporan(
    viewModel: LaporanViewModel,
    onResultSuccess: @Composable (PengukuranResponse) -> Unit,
    onUnauthorized: @Composable () -> Unit,
    onError: @Composable (String) -> Unit,
) {
    viewModel.addLaporanState.collectAsStateWithLifecycle().value.let { uiState ->
        when (uiState) {

            is UiState.Loading -> {
                Dialog(onDismissRequest = { }) {
                    Loading()
                }
            }

            is UiState.Success -> {
                onResultSuccess(uiState.data)
            }

            is UiState.Unauthorized -> {
                onUnauthorized()
            }

            is UiState.Error -> {
                onError(uiState.errorMessage)
            }
        }
    }
}