package com.kominfotabalong.simasganteng.data.local

import com.google.gson.Gson
import com.kominfotabalong.simasganteng.data.model.LoginResponse
import kotlinx.coroutines.flow.Flow

interface UserDataStoreInterface {
    suspend fun saveUserData(user: LoginResponse?, gson: Gson)
    fun getLoggedUserData(): Flow<String>
}