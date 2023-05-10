package com.kominfotabalong.simasganteng.ui.screen.laporan.tambah

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kominfotabalong.simasganteng.data.model.AddLaporanRequest
import com.kominfotabalong.simasganteng.data.model.LoginResponse
import com.kominfotabalong.simasganteng.ui.common.UiState
import com.kominfotabalong.simasganteng.ui.component.Loading
import com.kominfotabalong.simasganteng.ui.component.OutlinedTextFieldComp
import com.kominfotabalong.simasganteng.ui.component.ShowSnackbarWithAction
import com.kominfotabalong.simasganteng.ui.screen.laporan.LaporanViewModel

@Composable
fun NikSearchDialog(
    modifier: Modifier = Modifier,
    viewModel: LaporanViewModel,
    snackbarHostState: SnackbarHostState,
    userData: LoginResponse,
    onCloseClick: () -> Unit,
    onSearchResponse: (AddLaporanRequest) -> Unit,
    onUnauthorized: () -> Unit,
) {
    var showDialog by remember { mutableStateOf(true) }
    var nikError by remember { mutableStateOf(false) }
    var nikErrorMsg by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var nik by remember { mutableStateOf("") }
    var isSubmitted by remember { mutableStateOf(false) }

    fun validateInput(): Boolean {
        if (nik == "") {
            nikError = true
            nikErrorMsg = "Tolong Isi NIK Anak terlebih dahulu!"
            return false
        } else if (nik.length < 16) {
            nikError = true
            nikErrorMsg = "panjang minimal NIK anak adalah 16 digit!"
            return false
        }
        return true
    }

    if (isSubmitted)
        viewModel.nikState.collectAsStateWithLifecycle().value.let { uiState ->
            when (uiState) {
                is UiState.Unauthorized -> {
                    onUnauthorized()
                }

                is UiState.Error -> {
                    LaunchedEffect(key1 = Unit, block = { isLoading = false })
                    ShowSnackbarWithAction(
                        snackbarHostState = snackbarHostState,
                        errorMsg = uiState.errorMessage,
                        onRetryClick = {
                            viewModel.cariDataAnak(userData.token, nik)
                        },
                    )
                }

                is UiState.Success -> {
                    LaunchedEffect(key1 = Unit, block = {
                        isLoading = false
                        with(uiState.data) {
                            onSearchResponse(
                                AddLaporanRequest(
                                    alamat = alamat,
                                    anak_ke = anak_ke.toString(),
                                    berat = berat_lahir.toString(),
                                    jenis_kelamin = jenis_kelamin,
                                    lat = lat.toString(),
                                    lila = lila_lahir.toString(),
                                    lingkar_kepala = lingkar_kepala_lahir.toString(),
                                    lng = lng.toString(),
                                    nama_anak = nama_anak,
                                    nama_ortu = nama_ortu,
                                    nik_anak = nik_anak,
                                    nik_ortu = nik_ortu ?: "",
                                    pkm_id = pkm_id.toString(),
                                    rt = rt,
                                    rw = rw,
                                    kecamatanCode = code,
                                    tanggal_lahir = tanggal_lahir,
                                    tempat_lahir = tempat_lahir,
                                    tinggi = tinggi_lahir.toString(),
                                    village_code = village_code,
                                    whatsapp = whatsapp
                                )
                            )
                            showDialog = false
                        }
                    })
                }

                is UiState.Loading -> {
                    LaunchedEffect(key1 = Unit, block = { isLoading = true })
                }
            }
        }

    if (showDialog)
        Dialog(onDismissRequest = { }) {
            Card(
                elevation = CardDefaults.cardElevation(10.dp),
                shape = RoundedCornerShape(15.dp),
                modifier = modifier.navigationBarsPadding()
            ) {
                IconButton(
                    onClick = {
                        showDialog = false
                        onCloseClick()
                    },
                    modifier = modifier.align(Alignment.End)
                ) {
                    Icon(imageVector = Icons.Filled.Close, contentDescription = "tutup")
                }
                Column(modifier = modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) {
                    Text(
                        text = "Pencarian Data Anak",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )

                    OutlinedTextFieldComp(
                        placeholderText = "Masukkan NIK Anak",
                        query = nik,
                        isError = nikError,
                        errorMsg = nikErrorMsg,
                        onQueryChange = { newText ->
                            if (newText.length <= 16) {
                                nik = newText
                            }
                            nikError = false
                        },
                        supportingText = {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = "${nik.length}/16",
                                textAlign = TextAlign.End,
                            )
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                if (validateInput()) {
                                    viewModel.cariDataAnak(userData.token, nik)
                                    isSubmitted = true
                                }
                            }
                        ),
                        modifier = modifier.fillMaxWidth()
                    )

                    if (!isLoading)
                        Button(
                            onClick = {
                                if (validateInput()) {
                                    viewModel.cariDataAnak(userData.token, nik)
                                    isSubmitted = true
                                }
                            }, modifier = modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        ) {
                            Text(
                                text = "Cari",
                                color = if (isSystemInDarkTheme()) Color.White else Color.Black
                            )
                        }
                    else
                        Loading()
                }
            }
        }
}