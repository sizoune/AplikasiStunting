package com.kominfotabalong.simasganteng.ui.screen.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolygonOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapEffect
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.data.kml.KmlLayer
import com.google.maps.android.data.kml.KmlPolygon
import com.kominfotabalong.simasganteng.R
import com.ramcosta.composedestinations.annotation.Destination

@Composable
@Destination
fun MapScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var uiSettings by remember { mutableStateOf(MapUiSettings()) }
    var properties by remember {
        mutableStateOf(MapProperties(mapType = MapType.TERRAIN))
    }
//    val singapore = LatLng(1.35, 103.87)
//    val cameraPositionState = rememberCameraPositionState {
//        position = CameraPosition.fromLatLngZoom(singapore, 10f)
//    }
    Box(modifier = modifier.fillMaxSize()) {
        GoogleMap(
            modifier = modifier,
            properties = properties,
            uiSettings = uiSettings
        ) {
            val config = LocalConfiguration.current
            MapEffect { gMap ->
                val kmlLayer = KmlLayer(gMap, R.raw.kecamatan, context)
                kmlLayer.addLayerToMap()
                gMap.addPolygon(getPolyData("tanjung", kmlLayer))
                kmlLayer.removeLayerFromMap()
                moveCameraToKml(kmlLayer, gMap, config.screenWidthDp, config.screenHeightDp)
            }
        }
    }
}

private fun getPolyData(polyKey: String, kmlLayer: KmlLayer): PolygonOptions {
    var container = kmlLayer.containers.iterator().next()
    //Retrieve a nested container within the first container
    container = container.containers.iterator().next()
    val findedData =
        container.placemarks.find { it.getProperty("name").lowercase().contains(polyKey) }
    val polygon = findedData?.geometry as KmlPolygon
    val option = PolygonOptions().clickable(true).fillColor(0x4D000000)
    for (latLng in polygon.outerBoundaryCoordinates) {
        option.add(latLng)
    }
    return option
}

private fun moveCameraToKml(kmlLayer: KmlLayer, mMap: GoogleMap, currWidth: Int, currHeight: Int) {
    //Retrieve the first container in the KML layer
    var container = kmlLayer.containers.iterator().next()
    //Retrieve a nested container within the first container
    container = container.containers.iterator().next()
    //Retrieve the first placemark in the nested container
    val placemark = container.placemarks.iterator().next()
    val kelua = container.placemarks.find { it.getProperty("name").lowercase().contains("kelua") }
    println("kelua = ${kelua?.properties}")
    for (placemark in container.placemarks) {
        println("place = ${placemark.geometry}")
    }
    //Retrieve a polygon object in a placemark
    val polygon = placemark.geometry as KmlPolygon
    //Create LatLngBounds of the outer coordinates of the polygon
    val builder = LatLngBounds.Builder()
    for (latLng in polygon.outerBoundaryCoordinates) {
        builder.include(latLng)
    }
    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), currWidth, currHeight, 1))
}