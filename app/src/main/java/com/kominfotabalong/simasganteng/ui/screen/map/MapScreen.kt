package com.kominfotabalong.simasganteng.ui.screen.map

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.google.maps.android.data.kml.KmlLayer
import com.google.maps.android.data.kml.KmlPolygon
import com.kominfotabalong.simasganteng.R
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

@OptIn(MapsComposeExperimentalApi::class, ExperimentalLayoutApi::class)
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
    var selectedFilterData by remember { mutableStateOf("") }
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
        val (search, map, filteredContent) = createRefs()
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
                                    if (originClusterData.isNotEmpty()) originClusterData.clear()
                                    if (dataCluster.isNotEmpty()) dataCluster.clear()
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
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            modifier = modifier
                .constrainAs(filteredContent) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
                .zIndex(5f)
        ) {
            val textBg = if (isSystemInDarkTheme()) Color.DarkGray else Color.LightGray
            var currentOptPos by remember {
                mutableStateOf(0)
            }
            val options = listOf(
                "Berat Badan per Usia",
                "Tinggi / Panjang Badan per Usia",
                "Berat Badan per Tinggi / Panjang Badan"
            )

            LaunchedEffect(currentOptPos) {
                if (filteredData.isNotEmpty()) filteredData.clear()
                filteredData.putAll(if (options[currentOptPos].lowercase() == "berat badan per usia") {
                    filteredText = "Sebaran Berat Badan per Usia"
                    dataSebaran.groupBy { it.bb_per_u }
                } else if (options[currentOptPos].lowercase() == "tinggi / panjang badan per usia") {
                    filteredText = "Tinggi / Panjang Badan per Usia"
                    dataSebaran.groupBy { it.tb_per_u }
                } else {
                    filteredText = "Berat Badan per Tinggi / Panjang Badan"
                    dataSebaran.groupBy { it.bb_per_tb }
                })
            }

            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (currentOptPos != 0)
                    IconButton(onClick = { currentOptPos-- }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBackIos,
                            contentDescription = "sebelumnya"
                        )
                    }
                Text(
                    text = options[currentOptPos],
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold)
                )
                if (currentOptPos + 1 < options.size)
                    IconButton(onClick = { currentOptPos++ }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowForwardIos,
                            contentDescription = "selanjutnya"
                        )
                    }
            }

            FlowRow(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = modifier.padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
            ) {
                filteredData.forEach { (key, data) ->
                    Text(
                        text = "$key : ${data.size} anak",
                        fontSize = 11.sp,
                        modifier = modifier
                            .drawBehind {
                                drawRoundRect(
                                    color = textBg, cornerRadius = CornerRadius(20f, 20f)
                                )
                            }
                            .padding(8.dp)
                            .clickable {
                                selectedFilterData = key
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
                            }
                    )
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
            val config = LocalConfiguration.current
            MapEffect(key1 = Unit) { map ->
                val kmlLayer = KmlLayer(map, R.raw.kecamatan, context)
                kmlLayer.addLayerToMap()

                var container = kmlLayer.containers.iterator().next()
                //Retrieve a nested container within the first container
                container = container.containers.iterator().next()
                //Retrieve the first placemark in the nested container
                val placemark = container.placemarks.iterator().next()
                //Retrieve a polygon object in a placemark
                val polygon = placemark.geometry as KmlPolygon
                //Create LatLngBounds of the outer coordinates of the polygon
                val builder = LatLngBounds.Builder()
                for (latLng in polygon.outerBoundaryCoordinates) {
                    builder.include(latLng)
                }

                map.moveCamera(
                    CameraUpdateFactory.newLatLngBounds(
                        builder.build(),
                        config.screenWidthDp,
                        config.screenHeightDp,
                        0
                    )
                )
            }

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
                        selectedFilterData = ""
                        isExpanded = false
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
