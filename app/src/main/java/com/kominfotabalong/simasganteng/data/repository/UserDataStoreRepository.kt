package com.kominfotabalong.simasganteng.data.repository

import com.google.gson.Gson
import com.kominfotabalong.simasganteng.data.local.UserDataStore
import com.kominfotabalong.simasganteng.data.model.LoginResponse
import javax.inject.Inject

class UserDataStoreRepository @Inject constructor(
    private val userDataStore: UserDataStore,
    private val gson: Gson
) {
    suspend fun saveUserData(data: LoginResponse?) = userDataStore.saveUserData(data, gson)
    fun getLoggedUser() = userDataStore.getLoggedUserData()
}