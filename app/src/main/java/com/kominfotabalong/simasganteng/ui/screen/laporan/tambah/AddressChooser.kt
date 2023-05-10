package com.kominfotabalong.simasganteng.ui.screen.laporan.tambah

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.DragState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.kominfotabalong.simasganteng.R
import com.kominfotabalong.simasganteng.data.model.AddressLoc
import com.kominfotabalong.simasganteng.ui.component.Loading
import com.kominfotabalong.simasganteng.ui.screen.laporan.LaporanViewModel

@Composable
fun AddressChooser(
    modifier: Modifier = Modifier,
    showChooser: Boolean,
    viewModel: LaporanViewModel,
    userLatLng: LatLng,
    onDoneClick: (AddressLoc) -> Unit,
    onClose: () -> Unit
) {
    if (showChooser)
        Dialog(
            properties = DialogProperties(usePlatformDefaultWidth = false),
            onDismissRequest = { onClose() }) {
            val context = LocalContext.current

            val dragState = rememberMarkerState(
                position = userLatLng
            )
            var uiSettings by remember { mutableStateOf(MapUiSettings()) }
            var properties by remember {
                mutableStateOf(MapProperties(mapType = MapType.TERRAIN))
            }
            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(dragState.position, 17f)
            }

            var currentAddress by remember {
                mutableStateOf("")
            }

            viewModel.myAddr.collectAsState().value.let {
                currentAddress = it
            }

            val backBtnBG = colorResource(id = R.color.overlay_dark_30)


            ConstraintLayout(
                modifier = modifier
                    .fillMaxSize()
                    .navigationBarsPadding()
            ) {
                val (map, alamat, submit, backBtn) = createRefs()

                IconButton(onClick = { onClose() }, modifier = modifier
                    .constrainAs(backBtn) {
                        top.linkTo(parent.top)
                        end.linkTo(parent.end)
                    }
                    .padding(8.dp)
                    .zIndex(5f)) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        tint = Color.White,
                        contentDescription = "kembali",
                        modifier = Modifier
                            .drawBehind {
                                drawCircle(color = backBtnBG, radius = 50f)
                            })
                }

                GoogleMap(
                    modifier = modifier
                        .constrainAs(map) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            bottom.linkTo(parent.bottom)
                            width = Dimension.fillToConstraints
                            height = Dimension.fillToConstraints
                        }
                        .zIndex(3f),
                    properties = properties,
                    uiSettings = uiSettings,
                    cameraPositionState = cameraPositionState
                ) {
                    Marker(
                        state = dragState,
                        title = "Posisi Saya",
                        draggable = true,
                    )
                }

                Card(
                    modifier = modifier
                        .constrainAs(alamat) {
                            bottom.linkTo(parent.bottom)
                            start.linkTo(parent.start)
                            width = Dimension.fillToConstraints
                        }
                        .zIndex(4f),
                    elevation = CardDefaults.cardElevation(10.dp),
                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                ) {
                    Column(
                        modifier = modifier.padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        dragState.dragState.let {
                            if (it == DragState.END || it == DragState.START) {
                                cameraPositionState.position =
                                    CameraPosition.fromLatLngZoom(dragState.position, 17f)
                                MapInfoContent(
                                    currentAddress = currentAddress,
                                    currentLatLng = dragState.position
                                )
                            } else if (it == DragState.DRAG) {
                                Loading(modifier = modifier.wrapContentSize())
                            }
                        }
                    }
                }

                IconButton(onClick = {
                    onDoneClick(
                        AddressLoc(
                            myPosition = dragState.position,
                            myAddress = currentAddress,
                        )
                    )
                    onClose()
                }, modifier = modifier
                    .constrainAs(submit) {
                        end.linkTo(parent.end)
                        top.linkTo(alamat.top)
                        bottom.linkTo(alamat.top)
                    }
                    .padding(end = 24.dp)
                    .zIndex(5f)) {
                    Image(
                        painter = painterResource(id = R.drawable.done),
                        contentDescription = "pilih lokasi",
                        modifier = modifier.size(48.dp)
                    )
                }

                LaunchedEffect(dragState.dragState == DragState.END || dragState.dragState == DragState.START) {
                    viewModel.getAddressFromLocation(context, dragState.position)
                }
            }
        }


}

@Composable
fun MapInfoContent(
    modifier: Modifier = Modifier.fillMaxWidth(),
    currentAddress: String,
    currentLatLng: LatLng
) {
    Column {
        Text(
            text = currentAddress,
            style = MaterialTheme.typography.labelMedium,
            modifier = modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = modifier.padding(top = 16.dp)
        ) {
            Column() {
                Text(
                    text = "Latitude",
                    style = MaterialTheme.typography.labelSmall,
                )
                Text(
                    text = currentLatLng.latitude.toString(),
                    style = MaterialTheme.typography.labelMedium,
                )
            }
            Column() {
                Text(
                    text = "Longitude",
                    style = MaterialTheme.typography.labelSmall,
                )
                Text(
                    text = currentLatLng.longitude.toString(),
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }
    }
}