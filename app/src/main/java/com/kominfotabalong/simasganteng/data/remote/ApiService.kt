package com.kominfotabalong.simasganteng.data.remote

import com.haroldadmin.cnradapter.NetworkResponse
import com.kominfotabalong.simasganteng.data.model.ApiBaseResponse
import com.kominfotabalong.simasganteng.data.model.Kecamatan
import com.kominfotabalong.simasganteng.data.model.LoginResponse
import com.kominfotabalong.simasganteng.data.model.PuskesmasResponse
import com.kominfotabalong.simasganteng.data.model.ResponseListObject
import com.kominfotabalong.simasganteng.data.model.ResponseObject
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiService {
    @POST("login")
    @Headers("Accept: application/json")
    suspend fun doLogin(
        @Body params: RequestBody
    ): NetworkResponse<ResponseObject<LoginResponse>, ApiBaseResponse>

    @POST("login-firebase")
    @Headers("Accept: application/json")
    suspend fun doLoginWithGoogle(
        @Body params: RequestBody
    ): NetworkResponse<ResponseObject<LoginResponse>, ApiBaseResponse>

    @GET("indonesia/districts-tabalong")
    suspend fun getTabalongDistricts(
        @Header("Authorization") token: String
    ): NetworkResponse<ResponseListObject<Kecamatan>, ApiBaseResponse>

    @GET("pkm")
    suspend fun getDaftarPuskes(
        @Header("Authorization") token: String
    ): NetworkResponse<ResponseListObject<PuskesmasResponse>, ApiBaseResponse>

    @POST("laporan")
    suspend fun tambahLaporan(
        @Header("Authorization") token: String,
        @Body params: RequestBody,
    ): NetworkResponse<ApiBaseResponse, ApiBaseResponse>
}