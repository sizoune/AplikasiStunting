package com.kominfotabalong.simasganteng.data.local

import com.google.gson.Gson
import com.kominfotabalong.simasganteng.data.model.Kecamatan
import kotlinx.coroutines.flow.Flow

interface TabalongDataStoreInterface {
    suspend fun saveData(data: List<Kecamatan>?, gson: Gson)
    fun getData(): Flow<String>
}