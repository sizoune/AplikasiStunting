package com.kominfotabalong.simasganteng.data.model

import android.graphics.Color


data class Feature(
    val geometry: Geometry,
    val properties: Properties,
    val type: String = "",
    var fillColor: Int = Color.BLACK
)