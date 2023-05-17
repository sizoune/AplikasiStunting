package com.kominfotabalong.simasganteng.data.local

import com.google.gson.Gson
import com.kominfotabalong.simasganteng.data.model.PuskesmasResponse
import kotlinx.coroutines.flow.Flow

interface PuskesmasDataStoreInterface {
    suspend fun saveDataPuskes(data: List<PuskesmasResponse>?, gson: Gson)
    fun getDataPuskes(): Flow<String>
}