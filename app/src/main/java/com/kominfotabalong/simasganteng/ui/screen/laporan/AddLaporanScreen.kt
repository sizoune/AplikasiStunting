package com.kominfotabalong.simasganteng.ui.screen.laporan

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.kominfotabalong.simasganteng.data.model.AddLaporanRequest
import com.kominfotabalong.simasganteng.data.model.AddressLoc
import com.kominfotabalong.simasganteng.data.model.LoginResponse
import com.kominfotabalong.simasganteng.ui.common.UiState
import com.kominfotabalong.simasganteng.ui.component.Loading
import com.kominfotabalong.simasganteng.ui.component.ObserveLoggedUser
import com.kominfotabalong.simasganteng.ui.component.ShowSnackbarWithAction
import com.kominfotabalong.simasganteng.ui.component.StepperComp
import com.kominfotabalong.simasganteng.ui.component.SuccessDialog
import com.kominfotabalong.simasganteng.ui.destinations.AddressChooserDestination
import com.kominfotabalong.simasganteng.ui.destinations.LogoutHandlerDestination
import com.kominfotabalong.simasganteng.util.showToast
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultRecipient

@OptIn(ExperimentalPermissionsApi::class)
@Composable
@Destination
fun AddLaporanScreen(
    modifier: Modifier = Modifier,
    viewModel: LaporanViewModel = hiltViewModel(),
    navigator: DestinationsNavigator,
    snackbarHostState: SnackbarHostState,
    onLocPermissionDeniedForever: () -> Unit,
    resultRecipient: ResultRecipient<AddressChooserDestination, AddressLoc>
) {
    val context = LocalContext.current
    var userData by remember {
        mutableStateOf(LoginResponse())
    }
    var laporanRequest by remember {
        mutableStateOf(AddLaporanRequest())
    }

    val (showSnackBar, setShowSnackBar) = remember {
        mutableStateOf(false)
    }

    var currentStep by rememberSaveable { mutableStateOf(1) }
    val titleList = arrayListOf("Data Alamat", "Data Anak", "Data Orang Tua")

    BackHandler {
        if (currentStep != 1) currentStep--
        else navigator.navigateUp()
    }

    ObserveLoggedUser(getData = userData.token == "",
        onUserObserved = {
            userData = it
        }, onError = {
            context.showToast(it)
        })

    var isLoading by rememberSaveable {
        mutableStateOf(false)
    }
    if (isLoading)
        Dialog(onDismissRequest = { isLoading = false }) {
            Loading()
        }
    viewModel.isLoading.collectAsStateWithLifecycle().value.let { loadingState ->
        isLoading = loadingState
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

    ConstraintLayout(
        modifier = modifier
            .fillMaxSize()

    ) {

        val (stepper, contentAlamat, contentAnak, contentOrtu, prevButton, nextButton) = createRefs()
        val stepperUnselectedColor = if (isSystemInDarkTheme()) Color.DarkGray else Color.LightGray

        StepperComp(
            numberOfSteps = titleList.size,
            currentStep = currentStep,
            stepDescriptionList = titleList,
            selectedColor = MaterialTheme.colorScheme.primary,
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
                userToken = userData.token,
                viewModel = viewModel,
                currentRequest = laporanRequest,
                snackbarHostState = snackbarHostState,
                onLocPermissionDeniedForever = onLocPermissionDeniedForever,
                resultRecipient = resultRecipient,
                navigator = navigator,
                onNextClick = {
                    laporanRequest = it
                    collectDataAlamat = false
                    currentStep = 2
                },
                onUnauthorized = {
                    navigator.navigate(LogoutHandlerDestination("Sesi login anda telah berakhir"))
                }
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
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
            }
            .padding(16.dp)) {
            Button(
                onClick = {
                    if (currentStep != 1) currentStep--
                },
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBackIos,
                    contentDescription = "selanjutnya",
                    modifier.padding()
                )
            }
        }

        AnimatedVisibility(visible = true, modifier = modifier
            .constrainAs(nextButton) {
                bottom.linkTo(parent.bottom)
                end.linkTo(parent.end)
            }
            .padding(16.dp)) {
            Button(
                onClick = {
                    collectDataAlamat = currentStep == 1
                    collectDataAnak = currentStep == 2
                    collectDataOrtu = currentStep == 3
                    println("collectDataAlamat ? $collectDataAlamat")
                    println("collectDataAnak ? $collectDataAnak")
                    println("collctDataAlamat ? $collectDataAlamat")
                    viewModel.collectData(currentStep)
                    if (currentStep >= titleList.size)
                        viewModel.addLaporan(userData.token, laporanRequest)
                },
            ) {
                if (currentStep == titleList.size)
                    Text(text = "Submit")
                if (currentStep < titleList.size)
                    Icon(
                        imageVector = Icons.Filled.ArrowForwardIos,
                        contentDescription = "selanjutnya",
                        modifier.padding()
                    )
            }
        }

    }

    ObserveTambahLaporan(viewModel = viewModel, onResultSuccess = { successMsg ->
        SuccessDialog(
            showDialog = showSuccessDialog,
            dialogDesc = successMsg,
            onDismiss = {
                showSuccessDialog = it
                navigator.navigateUp()
            },
        )
    }, onResultError = { errorMsg ->
        ShowSnackbarWithAction(snackbarHostState = snackbarHostState,
            errorMsg = errorMsg,
            showSnackBar = showSnackBar,
            onRetryClick = { viewModel.getDaftarPuskes(userData.token) },
            onDismiss = { setShowSnackBar(it) })
    }, onUnauthorized = {
        navigator.navigate(LogoutHandlerDestination("Sesi login anda telah berakhir"))
    })
}

@Composable
fun ObserveTambahLaporan(
    viewModel: LaporanViewModel,
    onResultSuccess: @Composable (String) -> Unit,
    onResultError: @Composable (message: String) -> Unit,
    onUnauthorized: @Composable () -> Unit
) {
    viewModel.addLaporanState.collectAsState().value.let { uiState ->
        when (uiState) {

            is UiState.Loading -> {

            }

            is UiState.Success -> {
                onResultSuccess(uiState.data.message ?: "Laporan berhasil disubmit!")
            }

            is UiState.Error -> {
                println("error = ${uiState.errorMessage}")
                onResultError(uiState.errorMessage)
            }

            is UiState.Unauthorized -> {
                onUnauthorized()
            }
        }
    }
}