package com.kominfotabalong.simasganteng.ui.screen.petugas

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kominfotabalong.simasganteng.MainViewModel
import com.kominfotabalong.simasganteng.data.model.LoginResponse
import com.kominfotabalong.simasganteng.data.model.PuskesmasResponse
import com.kominfotabalong.simasganteng.data.model.User
import com.kominfotabalong.simasganteng.ui.common.UiState
import com.kominfotabalong.simasganteng.ui.component.Loading
import com.kominfotabalong.simasganteng.ui.component.NoData
import com.kominfotabalong.simasganteng.ui.component.OutlinedSpinner
import com.kominfotabalong.simasganteng.ui.component.ShowSnackbarWithAction
import com.kominfotabalong.simasganteng.ui.destinations.LogoutHandlerDestination
import com.kominfotabalong.simasganteng.ui.screen.laporan.detail.CreateTextForm
import com.kominfotabalong.simasganteng.util.getMonthIndex
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination
@Composable
fun PetugasScreen(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel = hiltViewModel(),
    dataPuskesmas: List<PuskesmasResponse>,
    userData: LoginResponse,
    selectedPKMID: Int = -1,
    snackbarHostState: SnackbarHostState,
    navigator: DestinationsNavigator
) {
    var selectedPuskes by remember {
        mutableStateOf("")
    }
    var pkmID by remember {
        mutableStateOf(selectedPKMID)
    }
    var isSelected by remember {
        mutableStateOf(false)
    }
    var isLoading by remember {
        mutableStateOf(false)
    }
    val daftarPetugas = remember {
        mutableStateListOf(User())
    }

    if (isSelected)
        mainViewModel.pkmStaffState.collectAsStateWithLifecycle().value.let { uiState ->
            when (uiState) {
                is UiState.Loading -> {
                    LaunchedEffect(Unit) {
                        isLoading = true
                    }
                }

                is UiState.Error -> {
                    isLoading = false
                    ShowSnackbarWithAction(
                        snackbarHostState = snackbarHostState,
                        errorMsg = uiState.errorMessage,
                        onRetryClick = {
                            mainViewModel.getDaftarPetugasPKM(userData.token, pkmID)
                        })
                }

                is UiState.Success -> {
                    LaunchedEffect(key1 = Unit) {
                        isLoading = false
                        uiState.data.data?.let { petugas ->
                            if (daftarPetugas.isNotEmpty()) daftarPetugas.clear()
                            daftarPetugas.addAll(petugas)
                        }
                    }
                }

                is UiState.Unauthorized -> {
                    navigator.navigate(LogoutHandlerDestination("Sesi login anda telah berakhir"))
                }
            }
        }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        val context = LocalContext.current
        OutlinedSpinner(
            options = dataPuskesmas.map { it.nama },
            label = "Pilih Puskesmas",
            value = selectedPuskes,
            onOptionSelected = { selectedVal ->
                selectedPuskes = selectedVal
                dataPuskesmas.find { it.nama == selectedVal }
                    ?.let { selectedPKM ->
                        pkmID = selectedPKM.pkm_id
                    }
            },
            modifier = modifier
        )
        if (isSelected)
            if (isLoading)
                Loading()
            else {
                if (daftarPetugas.isNotEmpty())
                    LazyColumn(modifier = modifier.padding(top = 20.dp)) {
                        items(daftarPetugas) { petugas ->
                            ItemPetugas(dataPetugas = petugas, onPhoneClick = { phone ->
                                context.startActivity(Intent().apply {
                                    action = Intent.ACTION_VIEW
                                    data =
                                        Uri.parse("tel:$phone")
                                })
                            })
                        }
                        item {
                            Spacer(modifier = modifier.size(16.dp))
                        }
                    }
                else
                    NoData(emptyDesc = "tidak ada data petugas puskesmas!")
            }

    }

    if (pkmID != -1)
        LaunchedEffect(pkmID) {
            println("pkmID = $pkmID")
            if (selectedPuskes == "")
                dataPuskesmas.find { it.pkm_id == pkmID }?.let {
                    selectedPuskes = it.nama
                }
            mainViewModel.getDaftarPetugasPKM(userData.token, pkmID)
            isSelected = true
        }
}

@Composable
fun ItemPetugas(
    modifier: Modifier = Modifier,
    dataPetugas: User,
    onPhoneClick: (String) -> Unit,
) {
    Card(
        elevation = CardDefaults.cardElevation(10.dp),
        shape = RoundedCornerShape(20.dp),
    ) {
        Column(modifier = modifier.padding(16.dp)) {
            FormText(label = "Nama Petugas", value = dataPetugas.name)
            Divider(
                modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 8.dp),
                thickness = 1.dp,
                color = Color.LightGray,
            )
            FormText(label = "Email Petugas", value = dataPetugas.email)
            Divider(
                modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 8.dp),
                thickness = 1.dp,
                color = Color.LightGray,
            )
            FormText(
                label = "Nomor Telpon",
                value = if (dataPetugas.phone.isNullOrEmpty()) "-" else dataPetugas.phone
            )
            if (dataPetugas.phone != null && dataPetugas.phone != "")
                Button(
                    onClick = {
                        onPhoneClick(dataPetugas.phone)
                    }, modifier = modifier
                        .padding(top = 10.dp)
                        .align(Alignment.End)
                ) {
                    Text(
                        text = "Hubungi Petugas",
                        color = if (isSystemInDarkTheme()) Color.White else Color.Black
                    )
                }

        }
    }
}

@Composable
fun FormText(
    modifier: Modifier = Modifier,
    label: String,
    value: String
) {
    Column {
        Text(text = label, style = MaterialTheme.typography.labelSmall)
        Text(
            text = value,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.ExtraBold),
            modifier = modifier.padding(top = 8.dp)
        )
    }
}