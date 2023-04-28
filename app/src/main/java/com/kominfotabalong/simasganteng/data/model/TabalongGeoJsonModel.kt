package com.kominfotabalong.simasganteng.data.model

data class TabalongGeoJsonModel(
    val features: List<Feature> = listOf(),
    val name: String = "",
    val type: String = ""
)