package com.kominfotabalong.simasganteng.ui.screen.map

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapEffect
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.Polygon
import com.kominfotabalong.simasganteng.data.model.Feature
import com.kominfotabalong.simasganteng.data.model.Kecamatan
import com.kominfotabalong.simasganteng.data.model.TabalongGeoJsonModel
import com.kominfotabalong.simasganteng.ui.common.UiState
import com.kominfotabalong.simasganteng.ui.component.OutlinedSpinner
import com.kominfotabalong.simasganteng.util.showToast
import com.ramcosta.composedestinations.annotation.Destination

@Composable
@Destination
fun MapScreen(
    modifier: Modifier = Modifier,
    dataKecamatan: List<Kecamatan>,
    viewModel: MapViewModel = hiltViewModel(),
    snackbarHostState: SnackbarHostState,
) {
    val context = LocalContext.current
    var properties by remember {
        mutableStateOf(MapProperties(mapType = MapType.TERRAIN))
    }
//    val singapore = LatLng(1.35, 103.87)

    var selectedKecamatan by rememberSaveable {
        mutableStateOf("")
    }
    var dataTabalong by remember {
        mutableStateOf(TabalongGeoJsonModel())
    }
    val dataPoly = remember {
        mutableStateListOf(listOf<LatLng>())
    }
    var filteredVillage by remember {
        mutableStateOf(listOf<Feature>())
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.getTabalongGeoData()
    }

    viewModel.tabalongState.collectAsStateWithLifecycle().value.let { tabalongState ->
        when (tabalongState) {
            is UiState.Loading -> {}
            is UiState.Unauthorized -> {}
            is UiState.Success -> {
                dataTabalong = tabalongState.data
//                println(dataTabalong)
            }

            is UiState.Error -> {
                println("error get tabalong data : ${tabalongState.errorMessage}")
                context.showToast(tabalongState.errorMessage)
            }
        }
    }

    LaunchedEffect(selectedKecamatan) {
        if (dataPoly.isNotEmpty()) dataPoly.clear()
        dataKecamatan.find { it.name == selectedKecamatan }?.let { kecamatan ->
            filteredVillage =
                dataTabalong.features.filter { it.properties.Kecamatan.lowercase() == kecamatan.name.lowercase() }
            println("${filteredVillage.map { it.properties.Name }}, Kec = ${filteredVillage.map { it.properties.Kecamatan }}")
            for (desa in filteredVillage) {
                dataPoly.add(
                    desa.geometry.coordinates[0].map {
                        LatLng(it[1], it[0])
                    }
                )
            }
        }
        println("dataTabalong feature Size = ${dataTabalong.features.size}, data poly size = ${dataPoly.size}")
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .navigationBarsPadding()
    ) {

        Card(
            elevation = CardDefaults.cardElevation(10.dp),
            shape = RoundedCornerShape(20.dp),
            modifier = modifier
                .zIndex(5f)
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.TopStart)
        ) {
            Column(modifier = modifier.padding(12.dp)) {
                OutlinedSpinner(
                    options = dataKecamatan.map { it.name },
                    label = "Pilih Kecamatan",
                    value = selectedKecamatan,
                    onOptionSelected = { selectedVal ->
                        selectedKecamatan = selectedVal
                    },
                    modifier = modifier
                )
            }
        }

        GoogleMap(
            modifier = modifier,
            properties = properties,
        ) {
            dataPoly.forEachIndexed { index, data ->
                Polygon(
                    points = data,
                    tag = filteredVillage[index].properties.FID,
                    fillColor = Color(filteredVillage[index].fillColor),
                    clickable = true,
                    onClick = { clickedPoly ->
                        context.showToast(filteredVillage[index].properties.Name)
                    }
                )
            }
            if (selectedKecamatan != "")
                MapEffect(selectedKecamatan) { map ->
                    val builder = LatLngBounds.Builder()
                    for (data in dataPoly) {
                        data.map { builder.include(it) }
                    }
                    map.animateCamera(
                        CameraUpdateFactory.newLatLngBounds(
                            builder.build(),
                            0
                        )
                    )
                }
        }

        AnimatedVisibility(
            selectedKecamatan != "",
            modifier = modifier
                .align(Alignment.BottomStart)
                .zIndex(5f)
        ) {
            Card(
                elevation = CardDefaults.cardElevation(10.dp),
                shape = RoundedCornerShape(20.dp),
                modifier = modifier
                    .padding(8.dp)
            ) {
                Column(modifier = modifier.padding(16.dp)) {
                    filteredVillage.forEach { data ->
                        DrawLegend(boxColor = Color(data.fillColor), desc = data.properties.Name)
                    }
                }
            }
        }
    }

}

@Composable
fun DrawLegend(
    modifier: Modifier = Modifier,
    boxColor: Color,
    desc: String
) {
    Row(
        modifier.padding(4.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = modifier
                .size(18.dp)
                .background(boxColor)
        ) {

        }
        Text(
            modifier = modifier.padding(start = 8.dp),
            text = desc,
            fontSize = 11.sp
        )
    }
}
