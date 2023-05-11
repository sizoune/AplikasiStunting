package com.kominfotabalong.simasganteng.ui.screen.laporan.tambah

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.maps.model.LatLng
import com.kominfotabalong.simasganteng.data.model.AddLaporanRequest
import com.kominfotabalong.simasganteng.data.model.Kecamatan
import com.kominfotabalong.simasganteng.data.model.PuskesmasResponse
import com.kominfotabalong.simasganteng.data.model.Village
import com.kominfotabalong.simasganteng.ui.component.Loading
import com.kominfotabalong.simasganteng.ui.component.OutlinedSpinner
import com.kominfotabalong.simasganteng.ui.component.OutlinedTextFieldComp
import com.kominfotabalong.simasganteng.ui.component.WarningDialog
import com.kominfotabalong.simasganteng.ui.screen.laporan.LaporanViewModel
import com.kominfotabalong.simasganteng.util.Constants
import com.kominfotabalong.simasganteng.util.showToast

@OptIn(ExperimentalPermissionsApi::class, ExperimentalComposeUiApi::class)
@Composable
fun LaporanAlamatContent(
    modifier: Modifier = Modifier,
    getData: Boolean,
    currentRequest: AddLaporanRequest,
    viewModel: LaporanViewModel,
    dataKecamatan: List<Kecamatan>,
    dataPuskesmas: List<PuskesmasResponse>,
    onNextClick: (AddLaporanRequest) -> Unit,
) {
    val isFinishSearching by viewModel.isFinishSearching.collectAsStateWithLifecycle()
    val myCurrentLat by viewModel.myLat.collectAsStateWithLifecycle()
    val myCurrentLng by viewModel.myLng.collectAsStateWithLifecycle()


    var alamatError by remember { mutableStateOf(false) }
    var alamat by remember {
        mutableStateOf(currentRequest.alamat)
    }

    var rtError by remember { mutableStateOf(false) }
    var rt by remember {
        mutableStateOf(currentRequest.rt)
    }

    var rwError by remember { mutableStateOf(false) }
    var rw by remember {
        mutableStateOf(currentRequest.rw)
    }

    var puskesError by remember { mutableStateOf(false) }
    var selectedPuskes by rememberSaveable {
        mutableStateOf("")
    }

    var selectedPuskesID by rememberSaveable {
        mutableStateOf(currentRequest.pkm_id)
    }

    var kecError by remember { mutableStateOf(false) }
    var selectedKecamatan by rememberSaveable {
        mutableStateOf("")
    }
    var selectedKecCode by rememberSaveable {
        mutableStateOf(currentRequest.kecamatanCode)
    }


    val dataDesa = remember {
        mutableStateListOf<Village>()
    }

    var selectedDesaValue by rememberSaveable {
        mutableStateOf("")
    }

    var desaError by remember { mutableStateOf(false) }
    var selectedDesaCode by rememberSaveable {
        mutableStateOf(currentRequest.village_code)
    }


    var checkLocPerm by remember {
        mutableStateOf(false)
    }

    var currentAddressStat by remember {
        mutableStateOf(Constants.ADDRESS_NONE)
    }

    val context = LocalContext.current
    var isLocAccessGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    var showWarningDialog by remember {
        mutableStateOf(false)
    }

    val locationPermissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    val isLocLoading by viewModel.locLoading.collectAsStateWithLifecycle()
    println("isLocLoading = $isLocLoading")
    if (isLocLoading)
        Dialog(onDismissRequest = { }) {
            Loading()
        }

    val launcherPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.containsValue(false)) {
            permissions.entries.forEach {
                val granted = it.value
                if (!granted) {
                    val neverAskAgain = !locationPermissionsState.shouldShowRationale
                    if (neverAskAgain) {
                        context.showToast("Izin Akses Lokasi dibutuhkan !")
                        context.startActivity(Intent().apply {
                            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            data = Uri.fromParts("package", context.packageName, null)
                        })
                    } else {
                        showWarningDialog = true
                    }
                }
            }
        } else
            isLocAccessGranted = true
    }

    var showChooser by remember {
        mutableStateOf(false)
    }

    if (checkLocPerm) {
        LaunchedEffect(key1 = Unit) {
            launcherPermission.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
        checkLocPerm = false
    }

    if (currentRequest.village_code != "")
        LaunchedEffect(key1 = Unit, block = {
            alamat = currentRequest.alamat
            rt = currentRequest.rt
            rw = currentRequest.rw
            selectedKecCode = currentRequest.kecamatanCode
            selectedDesaCode = currentRequest.village_code
            dataKecamatan.find { kecamatan ->
                kecamatan.code == selectedKecCode
            }?.let { kecamatan ->
                dataDesa.addAll(kecamatan.villages)
                selectedKecamatan = kecamatan.name
                kecamatan.villages.find { it.code == currentRequest.village_code }
                    ?.let { village -> selectedDesaValue = village.name }
                dataPuskesmas.find { it.nama.lowercase() == selectedKecamatan.lowercase() }
                    ?.let {
                        selectedPuskes = it.nama
                        selectedPuskesID = it.pkm_id.toString()
                    }
            }
        })

    WarningDialog(showDialog = showWarningDialog,
        dialogDesc = "Izin mengakses lokasi dibutuhkan!",
        onDismiss = { showWarningDialog = it },
        onOkClick = { locationPermissionsState.launchMultiplePermissionRequest() })

    AddressChooser(
        showChooser = showChooser,
        viewModel = viewModel,
        userLatLng = LatLng(myCurrentLat, myCurrentLng),
        onDoneClick = {
            alamat = it.myAddress
            viewModel.setLatLng(it.myPosition)
        }) {
        showChooser = false
    }

    println("lat : $myCurrentLat, lng : $myCurrentLng")

    if (isLocAccessGranted && isFinishSearching)
        LaunchedEffect(Unit) {
            if (myCurrentLat == (-1).toDouble() && myCurrentLng == (-1).toDouble())
                viewModel.getUserLocation(context)
        }


    if (currentAddressStat == Constants.ADDRESS_CHOOSE) {
        if (myCurrentLat != (-1).toDouble() && myCurrentLng != (-1).toDouble()) {
            currentAddressStat = Constants.ADDRESS_NONE
            showChooser = true
        }
    } else if (currentAddressStat == Constants.ADDRESS_NEXT) {
        if (myCurrentLat != (-1).toDouble() && myCurrentLng != (-1).toDouble()) {
            currentAddressStat = Constants.ADDRESS_NONE
            viewModel.collectData(1)
        }
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
        } else if (rt == "") {
            rtError = true
            return false
        } else if (rw == "") {
            rwError = true
            return false
        } else if (myCurrentLat == (-1).toDouble() && myCurrentLng == (-1).toDouble()) {
            checkLocPerm = true
            currentAddressStat = Constants.ADDRESS_NEXT
            return false
        }
        return true
    }


    Card(
        elevation = CardDefaults.cardElevation(10.dp),
        shape = RoundedCornerShape(15.dp),
        modifier = modifier.padding(16.dp)
    ) {
        val focusManager = LocalFocusManager.current
        val keyboardController = LocalSoftwareKeyboardController.current

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
                            selectedKecCode = selectedKecamatan.code
                            for (data in selectedKecamatan.villages) {
                                dataDesa.add(data)
                            }
                        }
                    dataPuskesmas.find { it.nama.lowercase() == selectedVal.lowercase() }
                        ?.let {
                            selectedPuskes = it.nama
                            currentRequest.pkm_id = it.pkm_id.toString()
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
                        currentRequest.village_code = selectedDesaCode
                        println(selectedDesaCode)
                    }
                },
                modifier = modifier
            )
            OutlinedSpinner(
                options = dataPuskesmas.map { it.nama },
                label = "Pilih Puskesmas",
                value = selectedPuskes,
                isError = puskesError,
                errorMsg = "Tolong Pilih Puskesmas dulu!",
                onOptionSelected = { selectedVal ->
                    puskesError = false
                    selectedPuskes = selectedVal
                    dataPuskesmas.find { it.nama == selectedVal }
                        ?.let { selectedDesa ->
                            selectedPuskesID = selectedDesa.pkm_id.toString()
                            currentRequest.pkm_id = selectedPuskesID
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
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        }
                    ),
                    onQueryChange = {
                        alamatError = false
                        alamat = it
                        currentRequest.alamat = alamat
                    },
                    modifier = modifier.fillMaxWidth()

                )
                IconButton(onClick = {
                    if (myCurrentLat != (-1).toDouble() && myCurrentLng != (-1).toDouble())
                        showChooser = true
                    else {
                        checkLocPerm = true
                        currentAddressStat = Constants.ADDRESS_CHOOSE
                    }
                }, modifier = modifier.align(Alignment.CenterEnd)) {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = "Kembali",
                    )
                }

            }
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
                    .height(IntrinsicSize.Max),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedTextFieldComp(
                    placeholderText = "RT",
                    query = rt,
                    isError = rtError,
                    errorMsg = "tolong isi RT dahulu!",
                    prefix = {
                        Text(text = "RT")
                    },
                    onQueryChange = { newText ->
                        rt = newText
                        currentRequest.rt = rt
                        rtError = false
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusManager.moveFocus(FocusDirection.Right)
                        }
                    ),
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
                    placeholderText = "RW",
                    query = rw,
                    isError = rwError,
                    errorMsg = "tolong isi RT dahulu!",
                    prefix = {
                        Text(text = "RW")
                    },
                    onQueryChange = { newText ->
                        rw = newText
                        currentRequest.rw = rw
                        rwError = false
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (validateStepOne()) {
                                currentRequest.village_code = selectedDesaCode
                                currentRequest.pkm_id = selectedPuskesID
                                currentRequest.kecamatanCode = selectedKecCode
                                currentRequest.alamat = alamat
                                currentRequest.rt = rt
                                currentRequest.rw = rw
                                keyboardController?.hide()
                                onNextClick(currentRequest)
                            }
                        }
                    ),
                    modifier = modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            }
        }
    }


    if (getData)
        viewModel.dataCollect.value.let { index ->
            if (index == 1) {
                if (validateStepOne()) {
                    currentRequest.village_code = selectedDesaCode
                    currentRequest.kecamatanCode = selectedKecCode
                    currentRequest.pkm_id = selectedPuskesID
                    currentRequest.alamat = alamat
                    currentRequest.rt = rt
                    currentRequest.rw = rw
                    currentRequest.lat = myCurrentLat.toString()
                    currentRequest.lng = myCurrentLng.toString()
                    onNextClick(currentRequest)
                } else
                    viewModel.collectData(0)
            }
        }
}
