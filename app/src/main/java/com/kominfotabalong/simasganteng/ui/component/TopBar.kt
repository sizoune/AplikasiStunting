package com.kominfotabalong.simasganteng.ui.component

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kominfotabalong.simasganteng.ui.destinations.AddLaporanScreenDestination
import com.kominfotabalong.simasganteng.ui.destinations.Destination
import com.kominfotabalong.simasganteng.ui.destinations.DetailLaporanScreenDestination
import com.kominfotabalong.simasganteng.ui.destinations.ListLaporanMasukScreenDestination
import com.kominfotabalong.simasganteng.ui.destinations.ListLaporanRejectedScreenDestination
import com.kominfotabalong.simasganteng.ui.destinations.ListLaporanVerifiedScreenDestination
import com.kominfotabalong.simasganteng.ui.destinations.MapScreenDestination
import com.kominfotabalong.simasganteng.ui.destinations.PengukuranScreenDestination
import com.kominfotabalong.simasganteng.ui.destinations.PetugasScreenDestination
import com.kominfotabalong.simasganteng.ui.destinations.StatistikScreenDestination
import com.kominfotabalong.simasganteng.ui.destinations.TutorialScreenDestination

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun TopBar(
    destination: Destination,
    onBackClick: () -> Unit,
    onSearchClick: (String) -> Unit,
) {
    val titleColor = if (isSystemInDarkTheme()) Color.White else Color.Black
    var doSearch by remember {
        mutableStateOf(false)
    }
    var searchText by remember {
        mutableStateOf("")
    }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    TopAppBar(
        title = {
            if (!doSearch)
                Text(
                    text = destination.topBarTitle(),
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.ExtraBold),
                    textAlign = TextAlign.Center,
                    color = titleColor,
                )
            else {
                if (destination == ListLaporanVerifiedScreenDestination) {
                    TextField(
                        placeholder = {
                            Text(
                                text = "Masukkan NIK/Nama Anak",
                                style = MaterialTheme.typography.labelMedium
                            )
                        },
                        value = searchText, onValueChange = { searchText = it },
                        textStyle = MaterialTheme.typography.labelMedium,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Search
                        ),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                onSearchClick(searchText)
                                keyboardController?.hide()
                            }
                        ),
                        colors = TextFieldDefaults.colors(
                            cursorColor = if (isSystemInDarkTheme()) Color.White else Color.Black,
                            focusedLabelColor = if (isSystemInDarkTheme()) Color.White else Color.Black,
                            focusedIndicatorColor = if (isSystemInDarkTheme()) Color.White else Color.Black,
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .focusRequester(focusRequester)
                    )
                    LaunchedEffect(key1 = Unit, block = { focusRequester.requestFocus() })
                } else
                    doSearch = false
            }

        },
        navigationIcon = {
            IconButton(
                onClick = {
                    if (!doSearch)
                        onBackClick()
                    else {
                        doSearch = false
                        keyboardController?.hide()
                    }
                },
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBackIos,
                    contentDescription = "Kembali",
                    tint = titleColor
                )
            }
        },
        actions = {
            if (destination == ListLaporanVerifiedScreenDestination)
                IconButton(
                    onClick = { doSearch = !doSearch },
                ) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Cari Balita",
                        tint = titleColor
                    )
                }
        },
        colors = TopAppBarDefaults.topAppBarColors(MaterialTheme.colorScheme.primary)
    )
}

@Composable
fun Destination.topBarTitle(): String {
    return when (this) {
        AddLaporanScreenDestination -> {
            // Here you can also call another Composable on another file like TaskScreenTopBar
            // 👇 access the same viewmodel instance the screen is using, by passing the back stack entry
            "Tambah Laporan"
        }

        ListLaporanMasukScreenDestination -> {
            "Laporan Masuk"
        }

        ListLaporanVerifiedScreenDestination -> {
            "Balita Terverifikasi"
        }

        ListLaporanRejectedScreenDestination -> {
            "Laporan Ditolak"
        }

        DetailLaporanScreenDestination -> {
            "Periksa Laporan"
        }

        MapScreenDestination -> {
            "Peta Sebaran Stunting"
        }

        PengukuranScreenDestination -> {
            "Detail Balita"
        }

        StatistikScreenDestination -> {
            "Statistik Gizi"
        }

        PetugasScreenDestination -> {
            "Daftar Petugas Puskesmas"
        }

        TutorialScreenDestination -> {
            "Video Tutorial"
        }

        else -> javaClass.simpleName.removeSuffix("Destination")
    }
}