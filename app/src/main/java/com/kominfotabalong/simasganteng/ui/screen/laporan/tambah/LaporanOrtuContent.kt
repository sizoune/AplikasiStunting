package com.kominfotabalong.simasganteng.ui.screen.laporan

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kominfotabalong.simasganteng.data.model.AddLaporanRequest
import com.kominfotabalong.simasganteng.ui.component.OutlinedTextFieldComp
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.LocalDate

@Composable
fun LaporanOrtuContent(
    modifier: Modifier = Modifier,
    getData: Boolean,
    currentRequest: AddLaporanRequest,
    viewModel: LaporanViewModel,
    onNextClick: (AddLaporanRequest) -> Unit,
) {
    var noKKError by remember { mutableStateOf(false) }
    var noKK by remember {
        mutableStateOf(currentRequest.nomor_kk)
    }
    var noKKErrorMsg by remember {
        mutableStateOf("")
    }

    var tanggalPengisianError by remember { mutableStateOf(false) }
    var tanggalPengisian by remember {
        mutableStateOf(currentRequest.tanggal)
    }

    var namaOrtuError by remember { mutableStateOf(false) }
    var namaOrtu by remember {
        mutableStateOf(currentRequest.nama_ortu)
    }

    var noWAError by remember { mutableStateOf(false) }
    var noWA by remember {
        mutableStateOf(currentRequest.whatsapp)
    }

    var nikOrtuError by remember { mutableStateOf(false) }
    var nikErrorMsg by remember {
        mutableStateOf("")
    }
    var nikOrtu by remember {
        mutableStateOf(currentRequest.nik_ortu)
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
            val selectedDate = "${date.year}-${date.monthValue}-${date.dayOfMonth}"
            tanggalPengisian = selectedDate
            currentRequest.tanggal = tanggalPengisian
        }
    }

    fun validateStepThree(): Boolean {
        if (namaOrtu == "") {
            namaOrtuError = true
            return false
        } else if (nikOrtu == "") {
            nikOrtuError = true
            return false
        } else if (nikOrtu.length < 16) {
            nikOrtuError = true
            nikErrorMsg = "panjang minimal NIK adalah 16 digit!"
            return false
        } else if (noKK.length < 16) {
            noKKError = true
            noKKErrorMsg = "panjang minimal No KK adalah 16 digit!"
            return false
        } else if (noWA == "") {
            noWAError = true
            return false
        } else if (tanggalPengisian == "") {
            tanggalPengisianError = true
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
        Column(modifier = modifier.padding(12.dp)) {
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
                placeholderText = "Nomor Kartu Keluarga",
                query = noKK,
                isError = noKKError,
                errorMsg = noKKErrorMsg,
                onQueryChange = { newText ->
                    if (noKK.length < 16)
                        noKK = newText
                    currentRequest.nomor_kk = noKK
                    noKKError = false
                },
                supportingText = {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "${noKK.length}/16",
                        textAlign = TextAlign.End,
                    )
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
                modifier = modifier.fillMaxWidth()
            )
            OutlinedTextFieldComp(
                placeholderText = "Nama Orang Tua",
                query = namaOrtu,
                isError = namaOrtuError,
                errorMsg = "Nama ortu tidak boleh kosong!",
                onQueryChange = { newText ->
                    namaOrtu = newText
                    currentRequest.nama_ortu = namaOrtu
                    namaOrtuError = false
                },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                    }
                ),
                modifier = modifier.fillMaxWidth()

            )
            OutlinedTextFieldComp(
                placeholderText = "NIK Orang Tua",
                query = nikOrtu,
                isError = nikOrtuError,
                errorMsg = nikErrorMsg,
                onQueryChange = { newText ->
                    if (nikOrtu.length < 16)
                        nikOrtu = newText
                    currentRequest.nik_ortu = nikOrtu
                    nikOrtuError = false
                },
                supportingText = {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "${nikOrtu.length}/16",
                        textAlign = TextAlign.End,
                    )
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
                modifier = modifier.fillMaxWidth()
            )
            OutlinedTextFieldComp(
                placeholderText = "No. WhatsApp",
                query = noWA,
                isError = noWAError,
                errorMsg = "Nomor WhatsApp tidak boleh kosong!",
                onQueryChange = { newText ->
                    noWA = newText
                    currentRequest.whatsapp = noWA
                    noWAError = false
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (validateStepThree()) {
                            onNextClick(currentRequest)
                        }
                    }
                ),
            )

        }
    }

    if (getData)
        viewModel.dataCollect.value.let {
            if (it == 3) {
                if (validateStepThree()) {
                    onNextClick(currentRequest)
                } else
                    viewModel.collectData(0)
            }
        }
}