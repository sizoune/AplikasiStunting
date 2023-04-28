package com.kominfotabalong.simasganteng.data.repository

import android.content.Context
import com.google.gson.Gson
import com.kominfotabalong.simasganteng.data.local.PuskesmasDataStore
import com.kominfotabalong.simasganteng.data.local.TabalongDataStore
import com.kominfotabalong.simasganteng.data.local.UserDataStore
import com.kominfotabalong.simasganteng.data.model.Kecamatan
import com.kominfotabalong.simasganteng.data.model.LoginResponse
import com.kominfotabalong.simasganteng.data.model.PuskesmasResponse
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UserDataStoreRepository @Inject constructor(
    private val userDataStore: UserDataStore,
    private val tabalongDataStore: TabalongDataStore,
    private val puskesmasDataStore: PuskesmasDataStore,
    private val gson: Gson
) {
    suspend fun saveUserData(data: LoginResponse?) = userDataStore.saveUserData(data, gson)
    fun getLoggedUser() = userDataStore.getLoggedUserData()

    suspend fun saveDataTabalong(data: List<Kecamatan>?) = tabalongDataStore.saveData(data, gson)

    fun getDataTabalong() = tabalongDataStore.getData()

    suspend fun saveDataPuskes(data: List<PuskesmasResponse>?) =
        puskesmasDataStore.saveDataPuskes(data, gson)

    fun getDataPuskes() = puskesmasDataStore.getDataPuskes()

}