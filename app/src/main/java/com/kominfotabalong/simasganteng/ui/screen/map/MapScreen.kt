package com.kominfotabalong.simasganteng.ui.screen.map

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapEffect
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.clustering.Clustering
import com.kominfotabalong.simasganteng.data.model.LoginResponse
import com.kominfotabalong.simasganteng.data.model.SebaranResponse
import com.kominfotabalong.simasganteng.ui.common.UiState
import com.kominfotabalong.simasganteng.ui.component.Loading
import com.kominfotabalong.simasganteng.ui.component.OutlinedSpinner
import com.kominfotabalong.simasganteng.ui.component.OutlinedTextFieldComp
import com.kominfotabalong.simasganteng.ui.component.ShowSnackbarWithAction
import com.kominfotabalong.simasganteng.ui.destinations.LogoutHandlerDestination
import com.kominfotabalong.simasganteng.util.getListOfMonth
import com.kominfotabalong.simasganteng.util.getMonthIndex
import com.kominfotabalong.simasganteng.util.getStatusColor
import com.kominfotabalong.simasganteng.util.openMap
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@OptIn(MapsComposeExperimentalApi::class)
@Composable
@Destination
fun MapScreen(
    modifier: Modifier = Modifier,
    viewModel: MapViewModel = hiltViewModel(),
    navigator: DestinationsNavigator,
    userData: LoginResponse,
    snackbarHostState: SnackbarHostState,
) {
    val properties by remember {
        mutableStateOf(MapProperties(mapType = MapType.TERRAIN))
    }

    var monthError by remember { mutableStateOf(false) }
    var selectedMonth by remember {
        mutableStateOf("")
    }

    var yearError by remember { mutableStateOf(false) }
    var year by remember {
        mutableStateOf("")
    }

    var isSubmitted by remember {
        mutableStateOf(false)
    }
    var showDetailClickedItemCluster by remember {
        mutableStateOf(false)
    }
    val clickedSebaran by viewModel.clickedItemCluster.collectAsStateWithLifecycle()
    var changeMapEffect by remember {
        mutableStateOf(false)
    }
    val filteredData = remember {
        mutableStateMapOf<String, List<SebaranResponse>>()
    }
    var filteredText by remember {
        mutableStateOf("")
    }
    val dataSebaran = remember { mutableStateListOf<SebaranResponse>() }
    val dataCluster = remember { mutableStateListOf<MyItem>() }
    val originClusterData = remember { mutableStateListOf<MyItem>() }

    fun validateInput(): Boolean {
        if (year == "") {
            yearError = true
            return false
        }
        return true
    }

    var isExpanded by remember {
        mutableStateOf(true)
    }

    var showFilterDialog by remember {
        mutableStateOf(false)
    }
    val context = LocalContext.current

    FilterDialog(showFilter = showFilterDialog, onFilterChoose = { selectedFilter ->
        if (filteredData.isNotEmpty()) filteredData.clear()
        filteredData.putAll(if (selectedFilter.lowercase() == "berat badan per usia") {
            filteredText = "Sebaran Berat Badan per Usia"
            dataSebaran.groupBy { it.bb_per_u }
        } else if (selectedFilter.lowercase() == "tinggi / panjang badan per usia") {
            filteredText = "Tinggi / Panjang Badan per Usia"
            dataSebaran.groupBy { it.tb_per_u }
        } else {
            filteredText = "Berat Badan per Tinggi / Panjang Badan"
            dataSebaran.groupBy { it.bb_per_tb }
        })

        println("filtered data = ${filteredData.keys}")
    }, onDismiss = { showFilterDialog = false })

    DetailItemCluster(showDialog = showDetailClickedItemCluster,
        data = clickedSebaran,
        onDirectionClick = { latLng ->
            context.openMap(latLng.latitude, latLng.longitude)
        },
        onWhatsApp = { waNumber ->
            context.startActivity(Intent().apply {
                action = Intent.ACTION_VIEW
                data = Uri.parse("https://api.whatsapp.com/send?phone=+62$waNumber")
            })
        },
        onDismiss = {
            showDetailClickedItemCluster = false
        })

    ConstraintLayout(
        modifier = modifier
            .fillMaxSize()
            .navigationBarsPadding()
    ) {
        val (search, map, filter, filteredContent, detailContent) = createRefs()
        Card(elevation = CardDefaults.cardElevation(10.dp),
            shape = RoundedCornerShape(20.dp),
            modifier = modifier
                .constrainAs(search) {
                    top.linkTo(parent.top)
                    width = Dimension.fillToConstraints
                }
                .zIndex(5f)
                .fillMaxWidth()
                .padding(8.dp)

        ) {
            val focusManager = LocalFocusManager.current
            Column(modifier = modifier.padding(16.dp)) {
                Row(
                    modifier = modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Pencarian Data Sebaran",
                        style = MaterialTheme.typography.titleMedium,
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
                                .height(IntrinsicSize.Max),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            OutlinedTextFieldComp(
                                placeholderText = "Tahun",
                                query = year,
                                isError = yearError,
                                errorMsg = "Tolong isi tahun pengukuran dahulu",
                                onQueryChange = { newText ->
                                    yearError = false
                                    if (year.length < 4) year = newText
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
                                value = selectedMonth,
                                onOptionSelected = { selectedVal ->
                                    monthError = false
                                    selectedMonth = selectedVal
                                },
                                modifier = modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            )

                        }

                        Button(
                            onClick = {
                                if (validateInput()) {
                                    if (filteredData.isNotEmpty()) filteredData.clear()
                                    viewModel.getDataSebaran(
                                        userData.token,
                                        year,
                                        if (selectedMonth != "") getMonthIndex(selectedMonth) else null
                                    )
                                    isSubmitted = true
                                }
                            }, modifier = modifier
                                .padding(top = 16.dp)
                                .align(Alignment.End)
                        ) {
                            Text(
                                text = "Lihat Data",
                                color = if (isSystemInDarkTheme()) Color.White else Color.Black
                            )
                        }
                    }
                }
            }
        }

        if (originClusterData.isNotEmpty()) Card(elevation = CardDefaults.cardElevation(10.dp),
            shape = RoundedCornerShape(20.dp),
            modifier = modifier
                .constrainAs(filter) {
                    top.linkTo(search.bottom)
                    end.linkTo(parent.end)
                }
                .zIndex(5f)
                .padding(end = 8.dp)

        ) {
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = modifier
                    .padding(end = 16.dp)
                    .clickable {
                        showFilterDialog = true
                    }) {
                IconButton(onClick = { showFilterDialog = true }) {
                    Icon(
                        imageVector = Icons.Filled.FilterList, contentDescription = "Filter Data"
                    )
                }
                Text(text = "Filter Sebaran", style = MaterialTheme.typography.labelLarge)
            }

        }

        if (filteredData.isNotEmpty()) Card(elevation = CardDefaults.cardElevation(10.dp),
            shape = RoundedCornerShape(20.dp),
            modifier = modifier
                .constrainAs(filteredContent) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
                .zIndex(5f)
                .padding(8.dp)

        ) {
            Text(
                text = filteredText,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                modifier = modifier.padding(start = 16.dp, top = 16.dp)
            )
            LazyRow(modifier = modifier.padding(start = 16.dp, end = 16.dp)) {
                filteredData.forEach { (key, data) ->
                    item {
                        TextButton(
                            onClick = {
                                if (dataCluster.isNotEmpty()) dataCluster.clear()
                                dataCluster.addAll(data.map {
                                    MyItem(
                                        LatLng(
                                            it.balita.lat, it.balita.lng
                                        ),
                                        it.balita.nama_anak + " (klik disini untuk melihat detail!)",
                                        it.toString()
                                    )
                                })
                                changeMapEffect = !changeMapEffect
                            }, colors = ButtonDefaults.textButtonColors(
                                contentColor = if (isSystemInDarkTheme()) Color.White else Color.Black
                            )
                        ) {
                            Text(text = "$key : ${data.size} anak", color = getStatusColor(key))
                        }
                    }
                }
            }
        }

        GoogleMap(
            modifier = modifier.constrainAs(map) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            },
            properties = properties,
        ) {
            if (originClusterData.isNotEmpty())
                Clustering(
                    items = if (dataCluster.isNotEmpty()) dataCluster.toList() else originClusterData.toList(),
                    // Optional: Handle clicks on clusters, cluster items, and cluster item info windows
                    onClusterClick = {
                        if (dataCluster.isNotEmpty()) dataCluster.clear()
                        dataCluster.addAll(
                            it.items
                        )
                        changeMapEffect = !changeMapEffect
                        false
                    }, onClusterItemClick = { myItem ->
                        dataSebaran.find {
                            it.balita.nama_anak == myItem.title.replace(
                                " (klik disini untuk melihat detail!)", ""
                            )
                        }?.let {
                            println("clickedSebaran = $it")
                            viewModel.setClickedItemCluster(it)
                            if (dataCluster.isNotEmpty()) dataCluster.clear()
                            dataCluster.add(myItem)
                            changeMapEffect = !changeMapEffect
                        }
                        println(myItem.itemTitle)
                        false
                    }, onClusterItemInfoWindowClick = {
                        println("Cluster item info window clicked! $it")
                        showDetailClickedItemCluster = true
                    }, clusterItemContent = null
                )

            MapEffect(changeMapEffect) { map ->
                val builder = LatLngBounds.Builder()
                if (dataCluster.isNotEmpty())
                    dataCluster.map { builder.include(it.position) }
                else
                    dataSebaran.map { builder.include(LatLng(it.balita.lat, it.balita.lng)) }
                if (dataCluster.isNotEmpty() || dataSebaran.isNotEmpty())
                    map.animateCamera(
                        CameraUpdateFactory.newLatLngBounds(
                            builder.build(), 0
                        )
                    )
            }
        }
    }

    if (isSubmitted) viewModel.sebaranState.collectAsStateWithLifecycle().value.let { uiState ->
        when (uiState) {
            is UiState.Loading -> {
                Dialog(onDismissRequest = { }) {
                    Loading()
                }
            }

            is UiState.Unauthorized -> {
                navigator.navigate(LogoutHandlerDestination("Sesi login anda telah berakhir"))
            }

            is UiState.Success -> {
                uiState.data.data?.let {
                    LaunchedEffect(key1 = Unit, block = {
                        isExpanded = false
                        if (filteredData.isNotEmpty()) filteredData.clear()
                        if (originClusterData.isNotEmpty()) originClusterData.clear()
                        if (dataCluster.isNotEmpty()) dataCluster.clear()
                        originClusterData.addAll(it.map {
                            MyItem(
                                LatLng(it.balita.lat, it.balita.lng),
                                it.balita.nama_anak + " (klik disini untuk melihat detail!)",
                                it.toString()
                            )
                        })
                        if (dataSebaran.isNotEmpty()) dataSebaran.clear()
                        dataSebaran.addAll(it)
                        changeMapEffect = !changeMapEffect
                    })
                }
            }

            is UiState.Error -> {
                ShowSnackbarWithAction(
                    snackbarHostState = snackbarHostState,
                    errorMsg = uiState.errorMessage,
                    onRetryClick = {
                        viewModel.getDataSebaran(
                            userData.token,
                            year,
                            if (selectedMonth != "") getMonthIndex(selectedMonth) else null
                        )
                    },
                )
            }
        }
    }
}

data class MyItem(
    val itemPosition: LatLng = LatLng(1.0, 1.0),
    val itemTitle: String = "",
    val itemSnippet: String = "",
) : ClusterItem {
    override fun getPosition(): LatLng = itemPosition

    override fun getTitle(): String = itemTitle

    override fun getSnippet(): String = itemSnippet
}
