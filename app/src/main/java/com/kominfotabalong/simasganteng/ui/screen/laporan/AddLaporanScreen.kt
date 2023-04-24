package com.kominfotabalong.simasganteng.ui.screen.laporan

import android.content.pm.PackageManager
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.maps.model.LatLng
import com.kominfotabalong.simasganteng.data.model.AddLaporanRequest
import com.kominfotabalong.simasganteng.data.model.AddressLoc
import com.kominfotabalong.simasganteng.data.model.Kecamatan
import com.kominfotabalong.simasganteng.data.model.LoginResponse
import com.kominfotabalong.simasganteng.data.model.PuskesmasResponse
import com.kominfotabalong.simasganteng.data.model.Village
import com.kominfotabalong.simasganteng.ui.common.UiState
import com.kominfotabalong.simasganteng.ui.component.Loading
import com.kominfotabalong.simasganteng.ui.component.LogoutHandler
import com.kominfotabalong.simasganteng.ui.component.ObserveLoggedUser
import com.kominfotabalong.simasganteng.ui.component.OutlinedSpinner
import com.kominfotabalong.simasganteng.ui.component.OutlinedTextFieldComp
import com.kominfotabalong.simasganteng.ui.component.ShowSnackbarWithAction
import com.kominfotabalong.simasganteng.ui.component.StepperComp
import com.kominfotabalong.simasganteng.ui.component.SuccessDialog
import com.kominfotabalong.simasganteng.ui.component.WarningDialog
import com.kominfotabalong.simasganteng.ui.screen.destinations.AddressChooserDestination
import com.kominfotabalong.simasganteng.util.Constants.ADDRESS_CHOOSE
import com.kominfotabalong.simasganteng.util.Constants.ADDRESS_NEXT
import com.kominfotabalong.simasganteng.util.Constants.ADDRESS_NONE
import com.kominfotabalong.simasganteng.util.showToast
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState

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
    var myCurrentLat by rememberSaveable {
        mutableStateOf((-1).toDouble())
    }
    var myCurrentLng by rememberSaveable {
        mutableStateOf((-1).toDouble())
    }

    var namaAnak by remember {
        mutableStateOf("")
    }
    var namaAnakError by remember { mutableStateOf(false) }

    var jkError by remember { mutableStateOf(false) }
    var selectedJK by remember {
        mutableStateOf("")
    }

    var anakKeError by remember { mutableStateOf(false) }
    var anakKe by remember {
        mutableStateOf("")
    }

    var nikError by remember { mutableStateOf(false) }
    var nik by remember {
        mutableStateOf("")
    }

    var nikOrtuError by remember { mutableStateOf(false) }
    var nikOrtu by remember {
        mutableStateOf("")
    }

    var tempatLahirError by remember { mutableStateOf(false) }
    var tempatLahir by remember {
        mutableStateOf("")
    }

    var tanggalLahirError by remember { mutableStateOf(false) }
    var tanggalLahir by remember {
        mutableStateOf("")
    }

    var alamatError by remember { mutableStateOf(false) }
    var alamat by rememberSaveable {
        mutableStateOf("")
    }

    var beratError by remember { mutableStateOf(false) }
    var berat by remember {
        mutableStateOf("")
    }

    var tinggiError by remember { mutableStateOf(false) }
    var tinggi by remember {
        mutableStateOf("")
    }

    var namaOrtuError by remember { mutableStateOf(false) }
    var namaOrtu by remember {
        mutableStateOf("")
    }

    var noWAError by remember { mutableStateOf(false) }
    var noWA by remember {
        mutableStateOf("")
    }

    val dateDialogState = rememberMaterialDialogState()

    var checkLocPerm by remember {
        mutableStateOf(false)
    }

    var currentAddressStat by remember {
        mutableStateOf(ADDRESS_NONE)
    }
    val context = LocalContext.current
    var isLocAccessGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                context, android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    var showWarningDialog by remember {
        mutableStateOf(false)
    }

    val locationPermissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    val launcherPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.containsValue(false)) {
            permissions.entries.forEach {
                val granted = it.value
                if (!granted) {
                    val neverAskAgain = !locationPermissionsState.shouldShowRationale
                    if (neverAskAgain) {
                        onLocPermissionDeniedForever()
                    } else {
                        showWarningDialog = true
                    }
                }
            }
        } else
            isLocAccessGranted = true
    }

    var userData by remember {
        mutableStateOf(LoginResponse())
    }

    var dataPuskes by remember {
        mutableStateOf(listOf<PuskesmasResponse>())
    }

    var puskesError by remember { mutableStateOf(false) }
    var selectedPuskes by rememberSaveable {
        mutableStateOf("")
    }

    var selectedPuskesID by rememberSaveable {
        mutableStateOf("")
    }

    var dataKecamatan by remember {
        mutableStateOf(listOf<Kecamatan>())
    }

    var kecError by remember { mutableStateOf(false) }
    var selectedKecamatan by rememberSaveable {
        mutableStateOf("")
    }

    var dataDesa = remember {
        mutableStateListOf<Village>()
    }

    var selectedDesaValue by rememberSaveable {
        mutableStateOf("")
    }

    var desaError by remember { mutableStateOf(false) }
    var selectedDesaCode by rememberSaveable {
        mutableStateOf("")
    }

    val (showSnackBar, setShowSnackBar) = remember {
        mutableStateOf(false)
    }

    var isLoading by rememberSaveable {
        mutableStateOf(false)
    }

    var currentStep by rememberSaveable { mutableStateOf(1) }
    val titleList = arrayListOf("Data Alamat", "Data Anak", "Data Orang Tua")

    BackHandler {
        if (currentStep != 1) currentStep--
        else navigator.navigateUp()
    }

    if (isLoading)
        Dialog(onDismissRequest = { isLoading = false }) {
            Loading()
        }
    viewModel.isLoading.collectAsStateWithLifecycle().value.let { loading ->
        isLoading = loading
    }

    if (checkLocPerm) {
        launcherPermission.launch(
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
        checkLocPerm = false
    }

    WarningDialog(showDialog = showWarningDialog,
        dialogDesc = "Izin mengakses lokasi dibutuhkan!",
        onDismiss = { showWarningDialog = it },
        onOkClick = { locationPermissionsState.launchMultiplePermissionRequest() })

    resultRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {
            }

            is NavResult.Value -> {
                alamat = result.value.myAddress
                myCurrentLat = result.value.myPosition.latitude
                myCurrentLng = result.value.myPosition.longitude
            }
        }
    }

    MaterialDialog(dialogState = dateDialogState, buttons = {
        positiveButton("Ok")
        negativeButton("Cancel")
    }) {
        datepicker { date ->
            // Do stuff with java.time.LocalDate object which is passed in
            tanggalLahir = "${date.dayOfWeek}, ${date.dayOfMonth} ${date.month} ${date.year}"
        }
    }


    ObserveLoggedUser(getData = userData.token == "",
        onUserObserved = {
            userData = it
            println("userData = $userData")
            LaunchedEffect(dataKecamatan.isEmpty()) {
                viewModel.getTabalongDistricts(it.token)
                viewModel.getDaftarPuskes(it.token)
            }
        }, onError = {
            context.showToast(it)
        })

    ObserveDataTabalong(viewModel = viewModel,
        onResultSuccess = {
            dataKecamatan = it
        }, onResultError = { errorMsg ->
            ShowSnackbarWithAction(snackbarHostState = snackbarHostState,
                errorMsg = errorMsg,
                showSnackBar = showSnackBar,
                onRetryClick = { viewModel.getTabalongDistricts(userData.token) },
                onDismiss = { setShowSnackBar(it) })
        }, onUnauthorized = {
            LogoutHandler("Sesi login anda telah berakhir")
        })

    ObserveDataPuskes(viewModel = viewModel, onResultSuccess = {
        dataPuskes = it
    }, onResultError = { errorMsg ->
        ShowSnackbarWithAction(snackbarHostState = snackbarHostState,
            errorMsg = errorMsg,
            showSnackBar = showSnackBar,
            onRetryClick = { viewModel.getDaftarPuskes(userData.token) },
            onDismiss = { setShowSnackBar(it) })
    }, onUnauthorized = {
        LogoutHandler("Sesi login anda telah berakhir")
    })

    LaunchedEffect(isLocAccessGranted) {
        println("lat : $myCurrentLat, long:$myCurrentLng")
        viewModel.getUserLocation(context)
    }

    viewModel.myLat.collectAsState().value.let {
        myCurrentLat = it
    }
    viewModel.myLng.collectAsState().value.let {
        myCurrentLng = it
    }

    if (currentAddressStat == ADDRESS_CHOOSE) {
        if (myCurrentLat == (-1).toDouble() && myCurrentLng == (-1).toDouble()) {
            isLoading = true
        } else {
            isLoading = false
            currentAddressStat = ADDRESS_NONE
            navigator.navigate(AddressChooserDestination(LatLng(myCurrentLat, myCurrentLng)))
        }
    } else if (currentAddressStat == ADDRESS_NEXT) {
        if (myCurrentLat == (-1).toDouble() && myCurrentLng == (-1).toDouble()) {
            isLoading = true
        } else {
            isLoading = false
            currentAddressStat = ADDRESS_NONE
            currentStep++
        }
    }

    var showSuccessDialog by remember {
        mutableStateOf(true)
    }

    fun validateStepOne(): Boolean {
        if (selectedKecamatan == "" || selectedKecamatan.lowercase() == "pilih kecamatan") {
            kecError = true
            return false
        } else if (selectedDesaValue == "" || selectedDesaValue.lowercase() == "pilih desa") {
            desaError = true
            return false
        } else if (selectedPuskes == "" || selectedPuskes.lowercase() == "pilih puskesmas") {
            puskesError = true
            return false
        } else if (alamat == "") {
            alamatError = true
            return false
        }
        return true
    }

    fun validateStepTwo(): Boolean {
        if (namaAnak == "") {
            namaAnakError = true
            return false
        } else if (selectedJK == "" || selectedJK.lowercase() == "pilih jenis kelamin anak") {
            jkError = true
            return false
        } else if (anakKe == "") {
            anakKeError = true
            return false
        } else if (nik == "") {
            nikError = true
            return false
        } else if (tanggalLahir == "") {
            tanggalLahirError = true
            return false
        } else if (tempatLahir == "") {
            tempatLahirError = true
            return false
        } else if (berat == "") {
            beratError = true
            return false
        } else if (tinggi == "") {
            tinggiError = true
            return false
        }
        return true
    }

    fun validateStepThree(): Boolean {
        if (namaOrtu == "") {
            namaOrtuError = true
            return false
        } else if (nikOrtu == "") {
            nikOrtuError = true
            return false
        } else if (noWA == "") {
            noWAError = true
            return false
        }
        return true
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
            currentStep == 1,
            enter = slideInHorizontally(),
            exit = slideOutHorizontally(),
            modifier = modifier.constrainAs(contentAlamat) {
                top.linkTo(stepper.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        ) {
            Card(
                elevation = CardDefaults.cardElevation(10.dp),
                shape = RoundedCornerShape(15.dp),
                modifier = modifier.padding(16.dp)
            ) {
                Column(modifier = modifier.padding(12.dp)) {
                    OutlinedSpinner(
                        options = dataKecamatan.map { it.name },
                        label = "Pilih Kecamatan",
                        value = selectedKecamatan,
                        isError = kecError,
                        errorMsg = "Tolong Pilih Kecamatan dulu!",
                        onOptionSelected = { selectedVal ->
                            kecError = false
                            selectedKecamatan = selectedVal
                            dataDesa.clear()
                            selectedDesaValue = "Pilih Desa"
                            selectedDesaCode = ""
                            dataKecamatan.find { it.name == selectedVal }
                                ?.let { selectedKecamatan ->
                                    for (data in selectedKecamatan.villages) {
                                        dataDesa.add(data)
                                    }
                                }
                            dataPuskes.find { it.nama.lowercase() == selectedVal.lowercase() }
                                ?.let {
                                    selectedPuskes = it.nama
                                    selectedPuskesID = it.pkm_id.toString()
                                }
                        },
                        modifier = modifier
                    )
                    OutlinedSpinner(
                        options = dataDesa.map { it.name },
                        value = selectedDesaValue,
                        label = "Pilih Desa",
                        isError = desaError,
                        errorMsg = "Tolong Pilih Desa dulu!",
                        onOptionSelected = { selectedVal ->
                            desaError = false
                            selectedDesaValue = selectedVal
                            dataDesa.find { it.name == selectedVal }?.let { selectedDesa ->
                                selectedDesaCode = selectedDesa.code
                                println(selectedDesaCode)
                            }
                        },
                        modifier = modifier
                    )
                    OutlinedSpinner(
                        options = dataPuskes.map { it.nama },
                        label = "Pilih Puskesmas",
                        value = selectedPuskes,
                        isError = puskesError,
                        errorMsg = "Tolong Pilih Puskesmas dulu!",
                        onOptionSelected = { selectedVal ->
                            puskesError = false
                            selectedPuskes = selectedVal
                            dataPuskes.find { it.nama == selectedVal }
                                ?.let { selectedDesa ->
                                    selectedPuskesID = selectedDesa.pkm_id.toString()
                                }
                        },
                        modifier = modifier
                    )
                    Box(
                        modifier = modifier.fillMaxWidth()
                    ) {
                        OutlinedTextFieldComp(
                            placeholderText = "Alamat",
                            query = alamat,
                            singleLine = false,
                            isError = alamatError,
                            errorMsg = "Tolong isi alamat dulu!",
                            minLines = 4,
                            onQueryChange = {
                                alamatError = false
                                alamat = it
                            },
                            modifier = modifier.fillMaxWidth()

                        )
                        IconButton(onClick = {
                            if (myCurrentLat != (-1).toDouble() && myCurrentLng != (-1).toDouble()) navigator.navigate(
                                AddressChooserDestination(
                                    LatLng(myCurrentLat, myCurrentLng)
                                )
                            ) else {
                                checkLocPerm = true
                                currentAddressStat = ADDRESS_CHOOSE
                            }
                        }, modifier = modifier.align(Alignment.CenterEnd)) {
                            Icon(
                                imageVector = Icons.Filled.LocationOn,
                                contentDescription = "Kembali",
                            )
                        }
                    }
                }
            }
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
            Card(
                elevation = CardDefaults.cardElevation(10.dp),
                shape = RoundedCornerShape(15.dp),
                modifier = modifier.padding(16.dp)
            ) {
                Column(modifier = modifier.padding(12.dp)) {
                    OutlinedTextFieldComp(
                        placeholderText = "Nama Anak",
                        query = namaAnak,
                        isError = namaAnakError,
                        errorMsg = "Nama Anak tidak boleh kosong!",
                        onQueryChange = { newText ->
                            namaAnak = newText
                            namaAnakError = false
                        },
                        modifier = modifier.fillMaxWidth()
                    )
                    OutlinedSpinner(
                        options = listOf("Laki-laki", "Perempuan"),
                        label = "Pilih Jenis Kelamin Anak",
                        value = selectedJK,
                        isError = jkError,
                        errorMsg = "Toloh pilih jenis kelamin anak dahulu!",
                        onOptionSelected = { selectedJK = it },
                        modifier = modifier
                    )
                    OutlinedTextFieldComp(
                        placeholderText = "Anak ke berapa",
                        query = anakKe,
                        isError = anakKeError,
                        errorMsg = "Anak ke berapa tidak boleh kosong!",
                        onQueryChange = { newText ->
                            anakKe = newText
                            anakKeError = false
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        modifier = modifier
                            .fillMaxWidth()

                    )
                    OutlinedTextFieldComp(
                        placeholderText = "NIK Anak",
                        query = nik,
                        isError = nikError,
                        errorMsg = "NIK tidak boleh kosong!",
                        onQueryChange = { newText ->
                            nik = newText
                            nikError = false
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        modifier = modifier.fillMaxWidth()
                    )
                    OutlinedTextFieldComp(
                        placeholderText = "Tempat Lahir",
                        query = tempatLahir,
                        isError = tempatLahirError,
                        errorMsg = "tempat lahir Anak tidak boleh kosong!",
                        onQueryChange = { newText ->
                            tempatLahir = newText
                            tempatLahirError = false
                        },
                        modifier = modifier.fillMaxWidth()
                    )
                    OutlinedTextFieldComp(
                        modifier = modifier.fillMaxWidth(),
                        placeholderText = "Tanggal Lahir Anak",
                        isError = tanggalLahirError,
                        errorMsg = "Tanggal Lahir tidak boleh kosong!",
                        query = tanggalLahir,
                        onQueryChange = {
                            tanggalLahirError = false
                        },
                        readOnly = true,
                        interactionSource = remember { MutableInteractionSource() }.also { interactionSource ->
                            LaunchedEffect(interactionSource) {
                                interactionSource.interactions.collect {
                                    if (it is PressInteraction.Release) {
                                        dateDialogState.show()
                                    }
                                }
                            }
                        },
                    )
                    Row(
                        modifier = modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                            .height(IntrinsicSize.Max),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        OutlinedTextFieldComp(
                            placeholderText = "Berat",
                            query = berat,
                            isError = beratError,
                            errorMsg = "berat anak tidak boleh kosong!",
                            suffix = {
                                Text(text = "Kg")
                            },
                            onQueryChange = { newText ->
                                berat = newText
                                beratError = false
                            },
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                            modifier = modifier
                                .fillMaxWidth()
                                .weight(1f)
                        )
                        Spacer(
                            modifier = modifier
                                .size(1.dp)
                                .weight(0.1f)
                        )
                        OutlinedTextFieldComp(
                            placeholderText = "Tinggi",
                            query = tinggi,
                            isError = tinggiError,
                            errorMsg = "Tinggi AnaK tidak boleh kosong!",
                            suffix = {
                                Text(text = "Cm")
                            },
                            onQueryChange = { newText ->
                                tinggi = newText
                                tinggiError = false
                            },
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                            modifier = modifier
                                .fillMaxWidth()
                                .weight(1f)
                        )
                    }
                }
            }
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
            Card(
                elevation = CardDefaults.cardElevation(10.dp),
                shape = RoundedCornerShape(15.dp),
                modifier = modifier.padding(16.dp)
            ) {
                Column(modifier = modifier.padding(12.dp)) {
                    OutlinedTextFieldComp(
                        placeholderText = "Nama Orang Tua",
                        query = namaOrtu,
                        isError = namaOrtuError,
                        errorMsg = "Nama ortu tidak boleh kosong!",
                        onQueryChange = { newText ->
                            namaOrtu = newText
                            namaOrtuError = false
                        },
                        modifier = modifier.fillMaxWidth()

                    )
                    OutlinedTextFieldComp(
                        placeholderText = "NIK Orang Tua",
                        query = nikOrtu,
                        isError = nikOrtuError,
                        errorMsg = "NIK tidak boleh kosong!",
                        onQueryChange = { newText ->
                            nikOrtu = newText
                            nikOrtuError = false
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        modifier = modifier.fillMaxWidth()
                    )
                    OutlinedTextFieldComp(
                        placeholderText = "No. WhatsApp",
                        query = noWA,
                        isError = noWAError,
                        errorMsg = "Nomor WhatsApp tidak boleh kosong!",
                        onQueryChange = { newText ->
                            noWA = newText
                            noWAError = false
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),

                        )
                }
            }
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
                    if (currentStep == 1) {
                        if (myCurrentLat != (-1).toDouble() && myCurrentLng != (-1).toDouble()) {
                            if (validateStepOne()) {
                                currentStep++
                            }
                        } else {
                            checkLocPerm = true
                            currentAddressStat = ADDRESS_NEXT
                        }

                    } else if (currentStep == 2) {
                        if (validateStepTwo()) {
                            currentStep++
                        }
                    } else if (currentStep == 3) {
                        if (validateStepThree()) {
                            val laporanReq = AddLaporanRequest(
                                pkm_id = selectedPuskesID,
                                village_code = selectedDesaCode,
                                alamat = alamat,
                                anak_ke = anakKe,
                                berat = berat,
                                jenis_kelamin = if (selectedJK.lowercase() == "laki-laki") "male" else "female",
                                lat = myCurrentLat.toString(),
                                lng = myCurrentLng.toString(),
                                nama_anak = namaAnak,
                                nama_ortu = namaOrtu,
                                nik_anak = nik,
                                nik_ortu = nikOrtu,
                                tanggal_lahir = tanggalLahir,
                                tempat_lahir = tempatLahir,
                                tinggi = tinggi,
                                whatsapp = noWA,
                            )
//                            showSuccessDialog = true
                            viewModel.addLaporan(userData.token, laporanReq)
                        }
                    }
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
        LogoutHandler("Sesi login anda telah berakhir")
    })
}

@Composable
fun ObserveDataTabalong(
    viewModel: LaporanViewModel,
    onResultSuccess: (List<Kecamatan>) -> Unit,
    onResultError: @Composable (message: String) -> Unit,
    onUnauthorized: @Composable () -> Unit
) {
    viewModel.kecamatanState.collectAsStateWithLifecycle().value.let { uiState ->
        when (uiState) {

            is UiState.Loading -> {
            }

            is UiState.Success -> {
                uiState.data.data?.let {
                    onResultSuccess(it)
                }
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

@Composable
fun ObserveDataPuskes(
    viewModel: LaporanViewModel,
    onResultSuccess: (List<PuskesmasResponse>) -> Unit,
    onResultError: @Composable (message: String) -> Unit,
    onUnauthorized: @Composable () -> Unit
) {
    viewModel.pkmState.collectAsStateWithLifecycle().value.let { uiState ->
        when (uiState) {

            is UiState.Loading -> {

            }

            is UiState.Success -> {
                uiState.data.data?.let {
                    onResultSuccess(it)
                }
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