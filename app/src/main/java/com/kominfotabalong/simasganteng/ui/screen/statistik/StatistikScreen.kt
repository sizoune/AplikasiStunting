package com.kominfotabalong.simasganteng.ui.screen.statistik

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
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
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kominfotabalong.simasganteng.data.model.LoginResponse
import com.kominfotabalong.simasganteng.data.model.StatistikResponse
import com.kominfotabalong.simasganteng.ui.common.UiState
import com.kominfotabalong.simasganteng.ui.component.ItemStatistik
import com.kominfotabalong.simasganteng.ui.component.Loading
import com.kominfotabalong.simasganteng.ui.component.NoData
import com.kominfotabalong.simasganteng.ui.component.OutlinedSpinner
import com.kominfotabalong.simasganteng.ui.component.OutlinedTextFieldComp
import com.kominfotabalong.simasganteng.ui.component.ShowSnackbarWithAction
import com.kominfotabalong.simasganteng.ui.destinations.LogoutHandlerDestination
import com.kominfotabalong.simasganteng.util.getListOfGiziStatus
import com.kominfotabalong.simasganteng.util.getListOfMonth
import com.kominfotabalong.simasganteng.util.getMonthIndex
import com.kominfotabalong.simasganteng.util.getSelectedStatistik
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@OptIn(ExperimentalLayoutApi::class)
@Destination
@Composable
fun StatistikScreen(
    modifier: Modifier = Modifier,
    viewModel: StatistikViewModel = hiltViewModel(),
    navigator: DestinationsNavigator,
    userData: LoginResponse,
    snackbarHostState: SnackbarHostState,
) {
    val focusManager = LocalFocusManager.current
    var isExpanded by remember {
        mutableStateOf(true)
    }
    var isSubmitted by remember {
        mutableStateOf(false)
    }
    var isLoading by remember {
        mutableStateOf(false)
    }
    val dataStatistik = remember {
        mutableStateListOf(StatistikResponse())
    }

    var yearError by remember { mutableStateOf(false) }
    var year by remember {
        mutableStateOf("")
    }
    var month by remember {
        mutableStateOf("")
    }
    var tipeError by remember { mutableStateOf(false) }
    var tipe by remember {
        mutableStateOf("")
    }

    fun validateInput(): Boolean {
        if (year == "") {
            yearError = true
            return false
        } else if (tipe == "") {
            tipeError = true
            return false
        }
        return true
    }

    Column(
        modifier = modifier
            .padding(16.dp)
    ) {
        Card(
            elevation = CardDefaults.cardElevation(10.dp),
            shape = RoundedCornerShape(20.dp),
            modifier = modifier
                .fillMaxWidth()
        ) {
            Column(modifier = modifier.padding(10.dp)) {
                Row(
                    modifier = modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = tipe,
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.ExtraBold),
                        modifier = modifier.weight(1f)
                    )
                    IconButton(
                        onClick = { isExpanded = !isExpanded },
                    ) {
                        Icon(
                            imageVector = if (!isExpanded) Icons.Filled.KeyboardArrowDown else Icons.Filled.KeyboardArrowUp,
                            contentDescription = "Detail"
                        )
                    }
                }

                AnimatedVisibility(visible = isExpanded) {
                    Column {
                        Row(
                            modifier = modifier
                                .fillMaxWidth()
                                .height(IntrinsicSize.Min),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            OutlinedTextFieldComp(
                                placeholderText = "Tahun",
                                query = year,
                                isError = yearError,
                                errorMsg = "Tolong isi tahun pengukuran dahulu",
                                onQueryChange = { newText ->
                                    yearError = false
                                    if (newText.length <= 4) year = newText
                                },
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    keyboardType = KeyboardType.Number, imeAction = ImeAction.Next
                                ),
                                keyboardActions = KeyboardActions(onNext = {
                                    focusManager.moveFocus(FocusDirection.Right)
                                }),
                                modifier = modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            )
                            Spacer(
                                modifier = modifier
                                    .size(1.dp)
                                    .weight(0.1f)
                            )
                            OutlinedSpinner(
                                options = getListOfMonth(),
                                label = "Bulan",
                                value = month,
                                onOptionSelected = { selectedVal ->
                                    month = selectedVal
                                },
                                modifier = modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            )

                        }

                        OutlinedSpinner(
                            options = getListOfGiziStatus(),
                            label = "Pilih Tipe Statistik",
                            value = tipe,
                            isError = tipeError,
                            errorMsg = "Tolong Pilih Tipe Statistik dahulu",
                            onOptionSelected = { selectedVal ->
                                tipeError = false
                                tipe = selectedVal
                            },
                            modifier = modifier
                                .fillMaxWidth()
                        )
                        if (!isLoading)
                            Button(
                                onClick = {
                                    if (validateInput()) {
                                        viewModel.getDataStatistik(
                                            userData.token, year, if (month == "") null else getMonthIndex(month),
                                            getSelectedStatistik(tipe)
                                        )
                                        isSubmitted = true
                                    }
                                }, modifier = modifier
                                    .padding(top = 16.dp)
                                    .fillMaxWidth()
                            ) {
                                Text(
                                    text = "Lihat Data Statistik",
                                    color = if (isSystemInDarkTheme()) Color.White else Color.Black
                                )
                            }
                        else
                            Loading()
                    }
                }
            }
        }
        if (isSubmitted)
            if (!isLoading && dataStatistik.isNotEmpty())
                FlowRow(
                    modifier = modifier.padding(top = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    maxItemsInEachRow = 2
                ) {
                    dataStatistik.forEach {
                        ItemStatistik(dataStatistik = it)
                    }
                }
            else {
                if (!isLoading)
                    NoData(emptyDesc = "Data Statistik Kosong!")
            }
    }

    if (isSubmitted)
        viewModel.statistikState.collectAsStateWithLifecycle().value.let { uiState ->
            when (uiState) {
                is UiState.Loading -> {
                    LaunchedEffect(Unit) {
                        isLoading = true
                    }
                }

                is UiState.Success -> {
                    uiState.data.data?.let {
                        LaunchedEffect(Unit) {
                            isExpanded = !isExpanded
                            isLoading = false
                            if (dataStatistik.isNotEmpty()) dataStatistik.clear()
                            dataStatistik.addAll(uiState.data.data)
                        }
                    }
                }

                is UiState.Error -> {
                    isLoading = false
                    ShowSnackbarWithAction(
                        snackbarHostState = snackbarHostState,
                        errorMsg = uiState.errorMessage,
                        onRetryClick = {
                            viewModel.getDataStatistik(
                                userData.token, year, if (month == "") null else getMonthIndex(month),
                                getSelectedStatistik(tipe)
                            )
                        },
                    )
                }

                is UiState.Unauthorized -> {
                    navigator.navigate(LogoutHandlerDestination("Sesi login anda telah berakhir"))
                }
            }
        }
}