package com.kominfotabalong.simasganteng.ui.screen.pengukuran

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kominfotabalong.simasganteng.data.model.PengukuranRequest
import com.kominfotabalong.simasganteng.ui.common.UiState
import com.kominfotabalong.simasganteng.ui.component.Loading
import com.kominfotabalong.simasganteng.ui.component.OutlinedSpinner
import com.kominfotabalong.simasganteng.ui.component.OutlinedTextFieldComp
import com.kominfotabalong.simasganteng.ui.component.ShowSnackbarWithAction
import com.kominfotabalong.simasganteng.ui.component.StuntingDialog
import com.kominfotabalong.simasganteng.ui.component.SuccessDialog
import com.kominfotabalong.simasganteng.ui.component.WarningDialog
import com.kominfotabalong.simasganteng.ui.destinations.LogoutHandlerDestination
import com.kominfotabalong.simasganteng.ui.destinations.PetugasScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.spec.DestinationStyle
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.LocalDate

@Composable
@Destination(style = DestinationStyle.Dialog::class)
fun PengukuranInput(
    modifier: Modifier = Modifier,
    viewModel: PengukuranViewModel,
    isUpdate: Boolean = false,
    pengukuranID: Int = -1,
    pkmID: Int = -1,
    snackbarHostState: SnackbarHostState,
    userToken: String,
    currentRequest: PengukuranRequest,
    balitaID: Int,
    resultNavigator: ResultBackNavigator<Boolean>,
    navigator: DestinationsNavigator
) {
    var tanggalPengisianError by remember { mutableStateOf(false) }
    var tanggalPengisian by remember {
        mutableStateOf(currentRequest.tanggal_pengisian)
    }

    var lingkarKepalaError by remember { mutableStateOf(false) }
    var lingkarKepala by remember {
        mutableStateOf(currentRequest.lingkar_kepala)
    }

    var lilaError by remember { mutableStateOf(false) }
    var lila by remember {
        mutableStateOf(currentRequest.lila)
    }

    var beratError by remember { mutableStateOf(false) }
    var berat by remember {
        mutableStateOf(currentRequest.berat_anak)
    }

    var tinggiError by remember { mutableStateOf(false) }
    var tinggi by remember {
        mutableStateOf(currentRequest.tinggi_anak)
    }

    var caraUkurError by remember { mutableStateOf(false) }
    var caraUkur by remember {
        mutableStateOf(currentRequest.cara_ukur)
    }

    var isSubmitted by remember {
        mutableStateOf(false)
    }
    var showWarning by remember {
        mutableStateOf(false)
    }
    var isDelete by remember {
        mutableStateOf(false)
    }

    fun validateInput(): Boolean {
        if (tanggalPengisian == "") {
            tanggalPengisianError = true
            return false
        } else if (lingkarKepala == "") {
            lingkarKepalaError = true
            return false
        } else if (lila == "") {
            lilaError = true
            return false
        } else if (berat == "") {
            beratError = true
            return false
        } else if (tinggi == "") {
            tinggiError = true
            return false
        } else if (caraUkur == "") {
            caraUkurError = true
            return false
        }
        return true
    }


    val dateDialogState = rememberMaterialDialogState()

    MaterialDialog(dialogState = dateDialogState, buttons = {
        positiveButton("Ok")
        negativeButton("Cancel")
    }) {
        datepicker(allowedDateValidator = { dialogDate ->
            dialogDate < LocalDate.now()
        }) { date ->
            // Do stuff with java.time.LocalDate object which is passed in
            tanggalPengisian = "${date.year}-${date.monthValue}-${date.dayOfMonth}"
            currentRequest.tanggal_pengisian = tanggalPengisian
        }
    }

    Card(
        elevation = CardDefaults.cardElevation(10.dp),
        shape = RoundedCornerShape(15.dp),
        modifier = modifier.navigationBarsPadding()
    ) {
        ConstraintLayout {
            val (title, closeBtn, inputContent) = createRefs()

            Text(
                text = if (!isUpdate) "Tambah Pengukuran" else "Update Pengukuran",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                modifier = modifier
                    .constrainAs(title) {
                        top.linkTo(closeBtn.top)
                        bottom.linkTo(closeBtn.bottom)
                        start.linkTo(parent.start)
                    }
                    .padding(start = 16.dp)
            )

            IconButton(
                onClick = { resultNavigator.navigateBack(result = false) },
                modifier = modifier.constrainAs(closeBtn) {
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                }
            ) {
                Icon(imageVector = Icons.Filled.Close, contentDescription = "tutup")
            }

            Column(
                modifier = modifier
                    .constrainAs(inputContent) {
                        top.linkTo(closeBtn.bottom)
                        width = Dimension.fillToConstraints
                    }
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val focusManager = LocalFocusManager.current

                OutlinedTextFieldComp(
                    modifier = modifier.fillMaxWidth(),
                    placeholderText = "Tanggal Pengisian",
                    isError = tanggalPengisianError,
                    errorMsg = "Tanggal Pengisian tidak boleh kosong!",
                    query = tanggalPengisian,
                    onQueryChange = {
                        tanggalPengisianError = false
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
                OutlinedTextFieldComp(
                    placeholderText = "Lingkar Kepala",
                    query = lingkarKepala,
                    isError = lingkarKepalaError,
                    errorMsg = "lingkar kepala anak tidak boleh kosong!",
                    suffix = {
                        Text(text = "Cm")
                    },
                    onQueryChange = { newText ->
                        lingkarKepala = newText
                        currentRequest.lingkar_kepala = lingkarKepala
                        lingkarKepalaError = false
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        }
                    ),
                    modifier = modifier
                        .fillMaxWidth()

                )
                OutlinedTextFieldComp(
                    placeholderText = "Lingkar Lengan",
                    query = lila,
                    isError = lilaError,
                    errorMsg = "Lingkar lengan AnaK tidak boleh kosong!",
                    suffix = {
                        Text(text = "Cm")
                    },
                    onQueryChange = { newText ->
                        lila = newText
                        currentRequest.lila = lila
                        lilaError = false
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        }
                    ),
                    modifier = modifier
                        .fillMaxWidth()

                )
                Row(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
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
                            currentRequest.berat_anak = berat
                            beratError = false
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
                        placeholderText = "Tinggi",
                        query = tinggi,

                        isError = tinggiError,
                        errorMsg = "Tinggi AnaK tidak boleh kosong!",
                        suffix = {
                            Text(text = "Cm")
                        },
                        onQueryChange = { newText ->
                            tinggi = newText
                            currentRequest.tinggi_anak = tinggi
                            tinggiError = false
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = {
                                focusManager.moveFocus(FocusDirection.Down)
                            }
                        ),
                        modifier = modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )


                }

                OutlinedSpinner(
                    options = listOf("terlentang", "berdiri"),
                    label = "Pilih Cara Ukur",
                    value = caraUkur,
                    isError = caraUkurError,
                    errorMsg = "Toloh pilih cara ukur dahulu!",
                    onOptionSelected = {
                        caraUkur = it
                        currentRequest.cara_ukur = caraUkur
                    },
                    modifier = modifier
                )

                Button(
                    onClick = {
                        if (validateInput()) {
                            currentRequest.status_laporan = "terverifikasi"
                            if (!isUpdate)
                                viewModel.tambahPengukuran(userToken, balitaID, currentRequest)
                            else
                                viewModel.updatePengukuran(userToken, pengukuranID, currentRequest)
                            isSubmitted = true
                        }
                    }, modifier = modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp)
                ) {
                    Text(
                        text = "Submit",
                        color = if (isSystemInDarkTheme()) Color.White else Color.Black
                    )
                }

                if (isUpdate)
                    TextButton(onClick = {
                        showWarning = true
                    }) {
                        Text(
                            text = "Hapus Data Pengukuran",
                            color = Color.Red
                        )
                    }

            }
        }
    }

    WarningDialog(showDialog = showWarning,
        onDismiss = { dismiss ->
            showWarning = dismiss
        },
        dialogDesc = "Apakah anda yakin ingin menghapus data pengukuran ini ?",
        onOkClick = {
            isDelete = true
            viewModel.deletePengukuran(userToken, pengukuranID)
            isSubmitted = true
        })

    if (isSubmitted) {
        viewModel.ukurOperationState.collectAsStateWithLifecycle().value.let { uiState ->
            when (uiState) {
                is UiState.Loading -> {
                    Dialog(onDismissRequest = {}) {
                        Loading()
                    }
                }

                is UiState.Success -> {
                    if (uiState.data.tb_per_u.lowercase() == "sangat pendek" || uiState.data.tb_per_u.lowercase() == "pendek")
                        StuntingDialog(
                            showDialog = true,
                            dialogDesc = "Terima kasih atas laporan anda!, namun perlu diperhatikan sistem kami mendeteksi bahwa anak anda tersuspeksi Stunting !, " +
                                    "silahkan hubungi puskesmas terdekat untuk penanganan lebih lanjut!",
                            onDismiss = {
                                resultNavigator.navigateBack(result = true)
                            },
                            onPhoneClick = {
                                navigator.navigate(
                                    PetugasScreenDestination(
                                        pkmID
                                    )
                                )
                            }
                        )
                    else
                        SuccessDialog(
                            showDialog = true,
                            dialogDesc = "Terima kasih atas laporan anda!, sistem kami mendeteksi bahwa anak anda TIDAK tersuspek Stunting!",
                            onDismiss = {
                                resultNavigator.navigateBack(result = true)
                            },
                        )

                }

                is UiState.Error -> {
                    ShowSnackbarWithAction(
                        snackbarHostState = snackbarHostState,
                        errorMsg = uiState.errorMessage,
                        onRetryClick = {
                            if (!isUpdate)
                                viewModel.tambahPengukuran(userToken, balitaID, currentRequest)
                            else {
                                if (!isDelete)
                                    viewModel.updatePengukuran(
                                        userToken,
                                        pengukuranID,
                                        currentRequest
                                    )
                                else
                                    viewModel.deletePengukuran(userToken, pengukuranID)
                            }
                        },
                    )
                }

                is UiState.Unauthorized -> {
                    navigator.navigate(LogoutHandlerDestination("Sesi login anda telah berakhir"))
                }
            }
        }

        viewModel.delOperationState.collectAsStateWithLifecycle().value.let { uiState ->
            when (uiState) {
                is UiState.Loading -> {
                    Dialog(onDismissRequest = {}) {
                        Loading()
                    }
                }

                is UiState.Success -> {
                    SuccessDialog(
                        showDialog = true,
                        dialogDesc = uiState.data.message ?: "Data berhasil dihapus!",
                        onDismiss = {
                            resultNavigator.navigateBack(result = true)
                        },
                    )
                }

                is UiState.Error -> {
                    ShowSnackbarWithAction(
                        snackbarHostState = snackbarHostState,
                        errorMsg = uiState.errorMessage,
                        onRetryClick = {
                            viewModel.deletePengukuran(userToken, pengukuranID)
                        },
                    )
                }

                is UiState.Unauthorized -> {
                    navigator.navigate(LogoutHandlerDestination("Sesi login anda telah berakhir"))
                }
            }

        }
    }

}