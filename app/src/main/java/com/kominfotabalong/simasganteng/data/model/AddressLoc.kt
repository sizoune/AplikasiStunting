package com.kominfotabalong.simasganteng.data.model

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import kotlinx.parcelize.Parcelize

@Parcelize
data class AddressLoc(
    val myPosition: LatLng = LatLng((-1).toDouble(), (-1).toDouble()),
    val myAddress: String = "",
) : Parcelable