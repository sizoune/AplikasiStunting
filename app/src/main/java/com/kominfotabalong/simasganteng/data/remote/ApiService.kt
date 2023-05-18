package com.kominfotabalong.simasganteng.data.remote

import com.haroldadmin.cnradapter.NetworkResponse
import com.kominfotabalong.simasganteng.data.model.ApiBaseResponse
import com.kominfotabalong.simasganteng.data.model.ArtikelResponse
import com.kominfotabalong.simasganteng.data.model.BalitaResponse
import com.kominfotabalong.simasganteng.data.model.Kecamatan
import com.kominfotabalong.simasganteng.data.model.LaporanResponse
import com.kominfotabalong.simasganteng.data.model.LoginResponse
import com.kominfotabalong.simasganteng.data.model.PaginationObject
import com.kominfotabalong.simasganteng.data.model.PengukuranResponse
import com.kominfotabalong.simasganteng.data.model.PuskesmasResponse
import com.kominfotabalong.simasganteng.data.model.ResponseListObject
import com.kominfotabalong.simasganteng.data.model.ResponseObject
import com.kominfotabalong.simasganteng.data.model.SebaranResponse
import com.kominfotabalong.simasganteng.data.model.StatistikResponse
import com.kominfotabalong.simasganteng.data.model.User
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap
import retrofit2.http.Url

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

    @Headers("Accept: application/json")
    @POST("user/update-profile")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Body params: RequestBody,
    ): NetworkResponse<ResponseObject<User>, ApiBaseResponse>

    @GET("user")
    suspend fun getUserData(
        @Header("Authorization") token: String,
    ): NetworkResponse<ResponseObject<LoginResponse>, ApiBaseResponse>

    @POST("store-token")
    suspend fun postFCMToken(
        @Header("Authorization") token: String,
        @Body params: RequestBody
    ): NetworkResponse<ApiBaseResponse, ApiBaseResponse>

    @GET("indonesia/districts-tabalong")
    suspend fun getTabalongDistricts(
        @Header("Authorization") token: String
    ): NetworkResponse<ResponseListObject<Kecamatan>, ApiBaseResponse>

    @GET("pkm")
    suspend fun getDaftarPuskes(
        @Header("Authorization") token: String
    ): NetworkResponse<ResponseListObject<PuskesmasResponse>, ApiBaseResponse>

    @GET("artikel")
    suspend fun getDaftarArtikel(
    ): NetworkResponse<ResponseListObject<ArtikelResponse>, ApiBaseResponse>

    @GET("balita/cek-nik/{nik}")
    suspend fun cekNikBalita(
        @Header("Authorization") token: String,
        @Path("nik") nikAnak: String,
    ): NetworkResponse<ResponseObject<BalitaResponse>, ApiBaseResponse>

    @Headers("Accept: application/json")
    @POST("laporan")
    suspend fun tambahLaporan(
        @Header("Authorization") token: String,
        @Body params: RequestBody,
    ): NetworkResponse<ResponseObject<PengukuranResponse>, ApiBaseResponse>

    @Headers("Accept: application/json")
    @PUT("laporan/update-status/{id}")
    suspend fun updateStatusLaporan(
        @Header("Authorization") token: String,
        @Path("id") laporanID: Int,
        @Body params: RequestBody,
    ): NetworkResponse<ApiBaseResponse, ApiBaseResponse>

    @GET
    suspend fun getLaporanData(
        @Header("Authorization") token: String,
        @Url page: String?,
    ): NetworkResponse<ResponseObject<PaginationObject<LaporanResponse>>, ApiBaseResponse>

    @GET
    suspend fun getDaftarBalita(
        @Header("Authorization") token: String,
        @Url page: String?,
    ): NetworkResponse<ResponseObject<PaginationObject<BalitaResponse>>, ApiBaseResponse>

    @GET("pengukuran/list/{balita_id}")
    suspend fun getDaftarPengukuran(
        @Header("Authorization") token: String,
        @Path("balita_id") balitaID: Int,
    ): NetworkResponse<ResponseListObject<PengukuranResponse>, ApiBaseResponse>

    @Headers("Accept: application/json")
    @POST("pengukuran/store/{balita_id}")
    suspend fun tambahPengukuran(
        @Header("Authorization") token: String,
        @Path("balita_id") balitaID: Int,
        @Body params: RequestBody,
    ): NetworkResponse<ResponseObject<PengukuranResponse>, ApiBaseResponse>

    @Headers("Accept: application/json")
    @PUT("pengukuran/update/{pengukuran_id}")
    suspend fun updatePengukuran(
        @Header("Authorization") token: String,
        @Path("pengukuran_id") pengukuranID: Int,
        @Body params: RequestBody,
    ): NetworkResponse<ResponseObject<PengukuranResponse>, ApiBaseResponse>

    @Headers("Accept: application/json")
    @GET("report/peta-sebaran")
    suspend fun getDataSebaran(
        @Header("Authorization") token: String,
        @QueryMap params: Map<String, String>,
    ): NetworkResponse<ResponseListObject<SebaranResponse>, ApiBaseResponse>

    @Headers("Accept: application/json")
    @GET("report/statistik-gizi")
    suspend fun getStatistikGizi(
        @Header("Authorization") token: String,
        @QueryMap params: Map<String, String>,
    ): NetworkResponse<ResponseListObject<StatistikResponse>, ApiBaseResponse>

    @Headers("Accept: application/json")
    @DELETE("pengukuran/destroy/{pengukuran_id}")
    suspend fun deletePengukuran(
        @Header("Authorization") token: String,
        @Path("pengukuran_id") pengukuranID: Int,
    ): NetworkResponse<ApiBaseResponse, ApiBaseResponse>

    @Headers("Accept: application/json")
    @GET("pkm/petugas/{pkm_id}")
    suspend fun getDaftarPetugasPKM(
        @Header("Authorization") token: String,
        @Path("pkm_id") pkm_id: Int,
    ): NetworkResponse<ResponseListObject<User>, ApiBaseResponse>
}