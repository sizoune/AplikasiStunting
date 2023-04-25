package com.kominfotabalong.simasganteng.ui.screen.laporan

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kominfotabalong.simasganteng.data.model.AddLaporanRequest
import com.kominfotabalong.simasganteng.ui.component.OutlinedSpinner
import com.kominfotabalong.simasganteng.ui.component.OutlinedTextFieldComp
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState

@Composable
fun LaporanAnakContent(
    modifier: Modifier = Modifier,
    getData: Boolean,
    currentRequest: AddLaporanRequest,
    viewModel: LaporanViewModel,
    onNextClick: (AddLaporanRequest) -> Unit,
) {
    var namaAnak by remember {
        mutableStateOf(currentRequest.nama_anak)
    }
    var namaAnakError by remember { mutableStateOf(false) }

    var jkError by remember { mutableStateOf(false) }
    var selectedJK by remember {
        mutableStateOf(if (currentRequest.jenis_kelamin == "L") "Laki-laki" else "Perempuan")
    }

    var anakKeError by remember { mutableStateOf(false) }
    var anakKe by remember {
        mutableStateOf(currentRequest.anak_ke)
    }

    var nikError by remember { mutableStateOf(false) }
    var nikErrorMsg by remember {
        mutableStateOf(currentRequest.nik_anak)
    }
    var nik by remember {
        mutableStateOf(currentRequest.nik_anak)
    }

    var tempatLahirError by remember { mutableStateOf(false) }
    var tempatLahir by remember {
        mutableStateOf(currentRequest.tempat_lahir)
    }

    var tanggalLahirError by remember { mutableStateOf(false) }
    var tanggalLahir by remember {
        mutableStateOf(currentRequest.tanggal_lahir)
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
        mutableStateOf(currentRequest.berat)
    }

    var tinggiError by remember { mutableStateOf(false) }
    var tinggi by remember {
        mutableStateOf(currentRequest.tinggi)
    }

    val dateDialogState = rememberMaterialDialogState()

    MaterialDialog(dialogState = dateDialogState, buttons = {
        positiveButton("Ok")
        negativeButton("Cancel")
    }) {
        datepicker { date ->
            // Do stuff with java.time.LocalDate object which is passed in
            tanggalLahir = "${date.year}-${date.monthValue}-${date.dayOfMonth}"
            currentRequest.tanggal_lahir = tanggalLahir
        }
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
            nikErrorMsg = "Tolong Isi NIK Anak terlebih dahulu!"
            return false
        } else if (nik.length < 16) {
            nikError = true
            nikErrorMsg = "panjang minimal NIK anak adalah 16 digit!"
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
        } else if (lingkarKepala == "") {
            lingkarKepalaError = true
            return false
        } else if (lila == "") {
            lilaError = true
            return false
        }
        return true
    }

    Card(
        elevation = CardDefaults.cardElevation(10.dp),
        shape = RoundedCornerShape(15.dp),
        modifier = modifier.padding(16.dp)
    ) {
        Column(
            modifier = modifier
                .padding(12.dp)
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextFieldComp(
                placeholderText = "Nama Anak",
                query = namaAnak,
                isError = namaAnakError,
                errorMsg = "Nama Anak tidak boleh kosong!",
                onQueryChange = { newText ->
                    namaAnak = newText
                    currentRequest.nama_anak = namaAnak
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
                onOptionSelected = {
                    selectedJK = it
                    currentRequest.jenis_kelamin = if (selectedJK == "Laki-laki") "L" else "P"
                },
                modifier = modifier
            )
            OutlinedTextFieldComp(
                placeholderText = "Anak ke berapa",
                query = anakKe,
                isError = anakKeError,
                errorMsg = "Anak ke berapa tidak boleh kosong!",
                onQueryChange = { newText ->
                    anakKe = newText
                    currentRequest.anak_ke = anakKe
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
                errorMsg = nikErrorMsg,
                onQueryChange = { newText ->
                    nik = newText
                    currentRequest.nik_anak = nik
                    nikError = false
                },
                supportingText = {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "${nik.length}/16",
                        textAlign = TextAlign.End,
                    )
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
                    currentRequest.tempat_lahir = tempatLahir
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
                        currentRequest.berat = berat
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
                        currentRequest.tinggi = tinggi
                        tinggiError = false
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            }

            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
                    .height(IntrinsicSize.Max),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
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
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            }
        }
    }

    if (getData)
        viewModel.dataCollect.value.let { index ->
            if (index == 2) {
                if (validateStepTwo())
                    onNextClick(currentRequest)
                else
                    viewModel.collectData(0)
            }
        }
}