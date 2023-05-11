package com.kominfotabalong.simasganteng.data.repository

import com.haroldadmin.cnradapter.NetworkResponse
import com.kominfotabalong.simasganteng.data.model.AddLaporanRequest
import com.kominfotabalong.simasganteng.data.model.PengukuranRequest
import com.kominfotabalong.simasganteng.data.remote.ApiService
import com.kominfotabalong.simasganteng.util.Constants.DEVICE_NAME
import com.kominfotabalong.simasganteng.util.createJsonRequestBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Named

class ApiRepository @Inject constructor(
    private val apiService: ApiService,
    @Named(DEVICE_NAME)
    private val userDevice: String
) {
    suspend fun doLogin(
        username: String, pass: String
    ) = flow {
        when (val data = apiService.doLogin(
            createJsonRequestBody(
                "username" to username, "password" to pass, "device_name" to userDevice
            )
        )) {
            is NetworkResponse.Success -> {
                emit(
                    NetworkResponse.Success(
                        data, null, 200
                    )
                )
            }

            is NetworkResponse.ServerError -> {
                emit(NetworkResponse.ServerError(data.body, data.code))
            }

            is NetworkResponse.NetworkError -> {
                emit(NetworkResponse.NetworkError(data.error))
            }

            is NetworkResponse.UnknownError -> {
                emit(NetworkResponse.UnknownError(data.error))
            }
        }
    }.flowOn(Dispatchers.IO)

    suspend fun doLoginWithGoogle(
        email: String, name: String, firebaseToken: String
    ) = flow {
        when (val data = apiService.doLoginWithGoogle(
            createJsonRequestBody(
                "email" to email,
                "name" to name,
                "device_name" to userDevice,
                "firebase_token" to firebaseToken
            )
        )) {
            is NetworkResponse.Success -> {
                emit(
                    NetworkResponse.Success(
                        data, null, 200
                    )
                )
            }

            is NetworkResponse.ServerError -> {
                emit(NetworkResponse.ServerError(data.body, data.code))
            }

            is NetworkResponse.NetworkError -> {
                emit(NetworkResponse.NetworkError(data.error))
            }

            is NetworkResponse.UnknownError -> {
                emit(NetworkResponse.UnknownError(data.error))
            }
        }
    }.flowOn(Dispatchers.IO)

    suspend fun getUserData(
        token: String,
    ) = flow {
        when (val data = apiService.getUserData(
            "Bearer $token"
        )) {
            is NetworkResponse.Success -> {
                emit(
                    NetworkResponse.Success(
                        data, null, 200
                    )
                )
            }

            is NetworkResponse.ServerError -> {
                emit(NetworkResponse.ServerError(data.body, data.code))
            }

            is NetworkResponse.NetworkError -> {
                emit(NetworkResponse.NetworkError(data.error))
            }

            is NetworkResponse.UnknownError -> {
                emit(NetworkResponse.UnknownError(data.error))
            }
        }
    }.flowOn(Dispatchers.IO)

    suspend fun postFCMToken(
        token: String, fcmToken: String?
    ) = flow {
        when (val data = apiService.postFCMToken(
            "Bearer $token", createJsonRequestBody(
                "token" to fcmToken,
            )
        )) {
            is NetworkResponse.Success -> {
                emit(
                    NetworkResponse.Success(
                        data, null, 200
                    )
                )
            }

            is NetworkResponse.ServerError -> {
                emit(NetworkResponse.ServerError(data.body, data.code))
            }

            is NetworkResponse.NetworkError -> {
                emit(NetworkResponse.NetworkError(data.error))
            }

            is NetworkResponse.UnknownError -> {
                emit(NetworkResponse.UnknownError(data.error))
            }
        }
    }.flowOn(Dispatchers.IO)

    suspend fun getTabalongDistricts(
        token: String,
    ) = flow {
        when (val data = apiService.getTabalongDistricts("Bearer $token")) {
            is NetworkResponse.Success -> {
                emit(
                    NetworkResponse.Success(
                        data, null, 200
                    )
                )
            }

            is NetworkResponse.ServerError -> {
                emit(NetworkResponse.ServerError(data.body, data.code))
            }

            is NetworkResponse.NetworkError -> {
                emit(NetworkResponse.NetworkError(data.error))
            }

            is NetworkResponse.UnknownError -> {
                emit(NetworkResponse.UnknownError(data.error))
            }
        }
    }.flowOn(Dispatchers.IO)

    suspend fun getDaftarPuskes(
        token: String,
    ) = flow {
        when (val data = apiService.getDaftarPuskes("Bearer $token")) {
            is NetworkResponse.Success -> {
                emit(
                    NetworkResponse.Success(
                        data, null, 200
                    )
                )
            }

            is NetworkResponse.ServerError -> {
                emit(NetworkResponse.ServerError(data.body, data.code))
            }

            is NetworkResponse.NetworkError -> {
                emit(NetworkResponse.NetworkError(data.error))
            }

            is NetworkResponse.UnknownError -> {
                emit(NetworkResponse.UnknownError(data.error))
            }
        }
    }.flowOn(Dispatchers.IO)

    suspend fun getDaftarArtikel(
    ) = flow {
        when (val data = apiService.getDaftarArtikel()) {
            is NetworkResponse.Success -> {
                emit(
                    NetworkResponse.Success(
                        data, null, 200
                    )
                )
            }

            is NetworkResponse.ServerError -> {
                emit(NetworkResponse.ServerError(data.body, data.code))
            }

            is NetworkResponse.NetworkError -> {
                emit(NetworkResponse.NetworkError(data.error))
            }

            is NetworkResponse.UnknownError -> {
                emit(NetworkResponse.UnknownError(data.error))
            }
        }
    }.flowOn(Dispatchers.IO)

    suspend fun cekNikBalita(
        token: String, nikAnak: String,
    ) = flow {
        when (val data = apiService.cekNikBalita(
            "Bearer $token", nikAnak
        )) {
            is NetworkResponse.Success -> {
                emit(
                    NetworkResponse.Success(
                        data, null, 200
                    )
                )
            }

            is NetworkResponse.ServerError -> {
                emit(NetworkResponse.ServerError(data.body, data.code))
            }

            is NetworkResponse.NetworkError -> {
                emit(NetworkResponse.NetworkError(data.error))
            }

            is NetworkResponse.UnknownError -> {
                emit(NetworkResponse.UnknownError(data.error))
            }
        }
    }.flowOn(Dispatchers.IO)

    suspend fun tambahLaporan(
        token: String, dataLaporan: AddLaporanRequest
    ) = flow {
        when (val data = apiService.tambahLaporan(
            "Bearer $token", createJsonRequestBody(
                "pkm_id" to dataLaporan.pkm_id,
                "village_code" to dataLaporan.village_code,
                "nama_anak" to dataLaporan.nama_anak,
                "jenis_kelamin" to dataLaporan.jenis_kelamin,
                "anak_ke" to dataLaporan.anak_ke,
                "nomor_kk" to dataLaporan.nomor_kk,
                "nik_anak" to dataLaporan.nik_anak,
                "tempat_lahir" to dataLaporan.tempat_lahir,
                "tanggal_lahir" to dataLaporan.tanggal_lahir,
                "alamat" to dataLaporan.alamat,
                "rt" to dataLaporan.rt,
                "rw" to dataLaporan.rw,
                "berat" to dataLaporan.berat,
                "tinggi" to dataLaporan.tinggi,
                "lila" to dataLaporan.lila,
                "lingkar_kepala" to dataLaporan.lingkar_kepala,
                "nama_ortu" to dataLaporan.nama_ortu,
                "nik_ortu" to dataLaporan.nik_ortu,
                "whatsapp" to dataLaporan.whatsapp,
                "lat" to dataLaporan.lat,
                "lng" to dataLaporan.lng,
                "tanggal" to dataLaporan.tanggal,
            )
        )) {
            is NetworkResponse.Success -> {
                emit(
                    NetworkResponse.Success(
                        data, null, 200
                    )
                )
            }

            is NetworkResponse.ServerError -> {
                emit(NetworkResponse.ServerError(data.body, data.code))
            }

            is NetworkResponse.NetworkError -> {
                emit(NetworkResponse.NetworkError(data.error))
            }

            is NetworkResponse.UnknownError -> {
                emit(NetworkResponse.UnknownError(data.error))
            }
        }
    }.flowOn(Dispatchers.IO)

    suspend fun updateStatusLaporan(
        token: String, laporanID: Int, status: String
    ) = flow {
        when (val data = apiService.updateStatusLaporan(
            "Bearer $token", laporanID, createJsonRequestBody(
                "status" to status,
            )
        )) {
            is NetworkResponse.Success -> {
                emit(
                    NetworkResponse.Success(
                        data, null, 200
                    )
                )
            }

            is NetworkResponse.ServerError -> {
                emit(NetworkResponse.ServerError(data.body, data.code))
            }

            is NetworkResponse.NetworkError -> {
                emit(NetworkResponse.NetworkError(data.error))
            }

            is NetworkResponse.UnknownError -> {
                emit(NetworkResponse.UnknownError(data.error))
            }
        }
    }.flowOn(Dispatchers.IO)

    suspend fun getDaftarPengukuran(
        token: String, balitaID: Int,
    ) = flow {
        when (val data = apiService.getDaftarPengukuran(
            "Bearer $token", balitaID
        )) {
            is NetworkResponse.Success -> {
                emit(
                    NetworkResponse.Success(
                        data, null, 200
                    )
                )
            }

            is NetworkResponse.ServerError -> {
                emit(NetworkResponse.ServerError(data.body, data.code))
            }

            is NetworkResponse.NetworkError -> {
                emit(NetworkResponse.NetworkError(data.error))
            }

            is NetworkResponse.UnknownError -> {
                emit(NetworkResponse.UnknownError(data.error))
            }
        }
    }.flowOn(Dispatchers.IO)

    suspend fun getDataLaporan(userToken: String, url: String) =
        apiService.getLaporanData("Bearer $userToken", url)

    suspend fun getDaftarBalita(userToken: String, url: String) =
        apiService.getDaftarBalita("Bearer $userToken", url)

    suspend fun tambahPengukuran(
        token: String, balitaID: Int, request: PengukuranRequest
    ) = flow {
        when (val data = apiService.tambahPengukuran(
            "Bearer $token", balitaID, createJsonRequestBody(
                "tanggal" to request.tanggal_pengisian,
                "lila" to request.lila,
                "lingkar_kepala" to request.lingkar_kepala,
                "berat_anak" to request.berat_anak,
                "tinggi_anak" to request.tinggi_anak,
                "status_laporan" to request.status_laporan,
                "cara_ukur" to request.cara_ukur,
            )
        )) {
            is NetworkResponse.Success -> {
                emit(
                    NetworkResponse.Success(
                        data, null, 200
                    )
                )
            }

            is NetworkResponse.ServerError -> {
                emit(NetworkResponse.ServerError(data.body, data.code))
            }

            is NetworkResponse.NetworkError -> {
                emit(NetworkResponse.NetworkError(data.error))
            }

            is NetworkResponse.UnknownError -> {
                emit(NetworkResponse.UnknownError(data.error))
            }
        }
    }.flowOn(Dispatchers.IO)

    suspend fun updatePengukuran(
        token: String, pengukuranID: Int, request: PengukuranRequest
    ) = flow {
        when (val data = apiService.updatePengukuran(
            "Bearer $token", pengukuranID, createJsonRequestBody(
                "tanggal" to request.tanggal_pengisian,
                "lila" to request.lila,
                "lingkar_kepala" to request.lingkar_kepala,
                "berat_anak" to request.berat_anak,
                "tinggi_anak" to request.tinggi_anak,
                "status_laporan" to request.status_laporan,
                "cara_ukur" to request.cara_ukur,
            )
        )) {
            is NetworkResponse.Success -> {
                emit(
                    NetworkResponse.Success(
                        data, null, 200
                    )
                )
            }

            is NetworkResponse.ServerError -> {
                emit(NetworkResponse.ServerError(data.body, data.code))
            }

            is NetworkResponse.NetworkError -> {
                emit(NetworkResponse.NetworkError(data.error))
            }

            is NetworkResponse.UnknownError -> {
                emit(NetworkResponse.UnknownError(data.error))
            }
        }
    }.flowOn(Dispatchers.IO)

    suspend fun deletePengukuran(
        token: String, pengukuranID: Int
    ) = flow {
        when (val data = apiService.deletePengukuran(
            "Bearer $token", pengukuranID
        )) {
            is NetworkResponse.Success -> {
                emit(
                    NetworkResponse.Success(
                        data, null, 200
                    )
                )
            }

            is NetworkResponse.ServerError -> {
                emit(NetworkResponse.ServerError(data.body, data.code))
            }

            is NetworkResponse.NetworkError -> {
                emit(NetworkResponse.NetworkError(data.error))
            }

            is NetworkResponse.UnknownError -> {
                emit(NetworkResponse.UnknownError(data.error))
            }
        }
    }.flowOn(Dispatchers.IO)

    suspend fun getDataSebaran(
        token: String, tahun: String, bulan: String?, status: String?
    ) = flow {
        val params = hashMapOf(
            "year" to tahun,
        )
        if (bulan != null) params["month"] = bulan
        if (status != null) params["status"] = status
        when (val data = apiService.getDataSebaran(
            "Bearer $token", params
        )) {
            is NetworkResponse.Success -> {
                emit(
                    NetworkResponse.Success(
                        data, null, 200
                    )
                )
            }

            is NetworkResponse.ServerError -> {
                emit(NetworkResponse.ServerError(data.body, data.code))
            }

            is NetworkResponse.NetworkError -> {
                emit(NetworkResponse.NetworkError(data.error))
            }

            is NetworkResponse.UnknownError -> {
                emit(NetworkResponse.UnknownError(data.error))
            }
        }
    }.flowOn(Dispatchers.IO)

    suspend fun getStatistikGizi(
        token: String, tahun: String, bulan: String?
    ) = flow {
        val params = hashMapOf(
            "year" to tahun,
        )
        if (bulan != null) params["month"] = bulan
        when (val data = apiService.getStatistikGizi(
            "Bearer $token", params
        )) {
            is NetworkResponse.Success -> {
                emit(
                    NetworkResponse.Success(
                        data, null, 200
                    )
                )
            }

            is NetworkResponse.ServerError -> {
                emit(NetworkResponse.ServerError(data.body, data.code))
            }

            is NetworkResponse.NetworkError -> {
                emit(NetworkResponse.NetworkError(data.error))
            }

            is NetworkResponse.UnknownError -> {
                emit(NetworkResponse.UnknownError(data.error))
            }
        }
    }.flowOn(Dispatchers.IO)
}