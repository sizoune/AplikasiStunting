package com.kominfotabalong.simasganteng.ui.screen.laporan

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.kominfotabalong.simasganteng.ui.component.OutlinedSpinner
import com.kominfotabalong.simasganteng.ui.component.OutlinedTextFieldComp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
@Destination
fun AddLaporanScreen(
    modifier: Modifier = Modifier,
    navigator: DestinationsNavigator
) {
    val scrollState = rememberLazyListState()

    var namaAnak by remember {
        mutableStateOf("")
    }
    var namaAnakError by remember { mutableStateOf(false) }

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

    var tanggalLahirError by remember { mutableStateOf(false) }
    var tanggalLahir by remember {
        mutableStateOf("")
    }

    var alamatError by remember { mutableStateOf(false) }
    var alamat by remember {
        mutableStateOf("")
    }
    var rtAlamat by remember {
        mutableStateOf("")
    }
    var rwAlamat by remember {
        mutableStateOf("")
    }
    var desa by remember {
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

    val coroutineScope = rememberCoroutineScope()
    val dateDialogState = rememberMaterialDialogState()
    var scrollToPosition = 0.0F

    MaterialDialog(
        dialogState = dateDialogState,
        buttons = {
            positiveButton("Ok")
            negativeButton("Cancel")
        }
    ) {
        datepicker { date ->
            // Do stuff with java.time.LocalDate object which is passed in
            tanggalLahir = "${date.dayOfWeek}, ${date.dayOfMonth} ${date.month} ${date.year}"
        }
    }

    fun validateInput() {
        if (namaAnak == "") {
            namaAnakError = true
            coroutineScope.launch {
                scrollState.animateScrollToItem(0)
            }
        } else if (anakKe == "") {
            anakKeError = true
            coroutineScope.launch {
                scrollState.animateScrollToItem(1)
            }
        } else if (nik == "") {
            nikError = true
            coroutineScope.launch {
                scrollState.animateScrollToItem(2)
            }
        } else if (tanggalLahir == "") {
            tanggalLahirError = true
            coroutineScope.launch {
                scrollState.animateScrollToItem(3)
            }
        } else if (berat == "") {
            beratError = true
            coroutineScope.launch {
                scrollState.animateScrollToItem(4)
            }
        } else if (tinggi == "") {
            tinggiError = true
            coroutineScope.launch {
                scrollState.animateScrollToItem(4)
            }
        } else if (namaOrtu == "") {
            namaOrtuError = true
            coroutineScope.launch {
                scrollState.animateScrollToItem(5)
            }
        } else if (noWA == "") {
            noWAError = true
            coroutineScope.launch {
                scrollState.animateScrollToItem(6)
            }
        }
    }

    Card(
        elevation = CardDefaults.cardElevation(10.dp),
        shape = RoundedCornerShape(15.dp),
        modifier = modifier.padding(16.dp)
    ) {
        LazyColumn(
            state = scrollState, modifier = modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            item {
                OutlinedTextFieldComp(
                    placeholderText = "Nama Anak",
                    query = namaAnak,
                    isError = namaAnakError,
                    errorMsg = "Nama Anak tidak boleh kosong!",
                    onQueryChange = { newText ->
                        namaAnak = newText
                        namaAnakError = false
                    },
                    modifier = modifier
                        .fillMaxWidth()
                )
            }
            item {
                OutlinedSpinner(
                    options = listOf("Pria", "Wanita"),
                    label = "Pilih Jenis Kelamin",
                    onOptionSelected = { selectedJK = it }, modifier = modifier
                )
            }
            item {
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
                        .padding(top = 8.dp)
                        .fillMaxWidth()

                )
            }
            item {
                OutlinedTextFieldComp(
                    placeholderText = "NIK",
                    query = nik,
                    isError = nikError,
                    errorMsg = "NIK tidak boleh kosong!",
                    onQueryChange = { newText ->
                        nik = newText
                        nikError = false
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = modifier
                        .fillMaxWidth()

                )
            }
            item {
                OutlinedTextFieldComp(
                    modifier = modifier
                        .fillMaxWidth(),
                    placeholderText = "Tanggal Lahir",
                    isError = tanggalLahirError,
                    errorMsg = "Tanggal Lahir tidak boleh kosong!",
                    query = tanggalLahir,
                    onQueryChange = {
                        tanggalLahirError = false
                    },
                    readOnly = true,
                    interactionSource = remember { MutableInteractionSource() }
                        .also { interactionSource ->
                            LaunchedEffect(interactionSource) {
                                interactionSource.interactions.collect {
                                    if (it is PressInteraction.Release) {
                                        dateDialogState.show()
                                    }
                                }
                            }
                        },
                )
            }
            item {
                OutlinedTextFieldComp(
                    placeholderText = "Alamat",
                    query = alamat,
                    onQueryChange = { alamat = it },
                    modifier = modifier
                        .fillMaxWidth()

                )
            }
            item {
                Row(
                    modifier = modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Max),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedTextFieldComp(
                        placeholderText = "RT",
                        query = rtAlamat,
                        prefix = {
                            Text(text = "RT")
                        },
                        onQueryChange = { rtAlamat = it },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        modifier = modifier.weight(1f)
                    )
                    Spacer(
                        modifier = modifier
                            .size(1.dp)
                            .weight(0.1f)
                    )
                    OutlinedTextFieldComp(
                        placeholderText = "RW",
                        query = rwAlamat,
                        prefix = {
                            Text(text = "RW")
                        },
                        onQueryChange = { rwAlamat = it },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        modifier = modifier.weight(1f)
                    )
                }
            }
            item {
                OutlinedSpinner(
                    options = listOf("Telaga Itar", "Masingai"),
                    label = "Pilih Desa/Kelurahan",
                    onOptionSelected = { desa = it },
                    modifier = modifier
                        .fillMaxWidth()

                )
            }
            item {
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
            item {
                OutlinedTextFieldComp(
                    placeholderText = "Nama Orang Tua",
                    query = namaOrtu,
                    isError = namaOrtuError,
                    errorMsg = "Nama ortu tidak boleh kosong!",
                    onQueryChange = { newText ->
                        namaOrtu = newText
                        namaOrtuError = false
                    },
                    modifier = modifier
                        .fillMaxWidth()

                )
            }
            item {
                OutlinedTextFieldComp(
                    placeholderText = "No.HP Orang Tua / Pelapor",
                    query = noWA,
                    isError = noWAError,
                    errorMsg = "Nomor HP tidak boleh kosong!",
                    onQueryChange = { newText ->
                        noWA = newText
                        noWAError = false
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),

                    )
            }
            item {
                Button(
                    onClick = {
                        validateInput()
                    }, modifier = modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth()
                ) {
                    Text(text = "Submit")
                }
            }
        }
    }
}