package com.kominfotabalong.simasganteng

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.haroldadmin.cnradapter.NetworkResponse
import com.kominfotabalong.simasganteng.data.model.ArtikelResponse
import com.kominfotabalong.simasganteng.data.model.Kecamatan
import com.kominfotabalong.simasganteng.data.model.PuskesmasResponse
import com.kominfotabalong.simasganteng.data.model.ResponseListObject
import com.kominfotabalong.simasganteng.data.repository.ApiRepository
import com.kominfotabalong.simasganteng.data.repository.UserDataStoreRepository
import com.kominfotabalong.simasganteng.ui.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val apiRepository: ApiRepository,
    private val userDataStoreRepository: UserDataStoreRepository,
    private val gson: Gson,
) : ViewModel() {
    private val _artikelState: MutableStateFlow<UiState<ResponseListObject<ArtikelResponse>>> =
        MutableStateFlow(UiState.Loading)
    val artikelState: StateFlow<UiState<ResponseListObject<ArtikelResponse>>>
        get() = _artikelState

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean>
        get() = _isRefreshing.asStateFlow()

    private val _kecamatanState: MutableStateFlow<UiState<ResponseListObject<Kecamatan>>> =
        MutableStateFlow(UiState.Loading)
    val kecamatanState: StateFlow<UiState<ResponseListObject<Kecamatan>>>
        get() = _kecamatanState

    private val _pkmState: MutableStateFlow<UiState<ResponseListObject<PuskesmasResponse>>> =
        MutableStateFlow(UiState.Loading)
    val pkmState: StateFlow<UiState<ResponseListObject<PuskesmasResponse>>>
        get() = _pkmState

    fun getDaftarArtikel() {
        viewModelScope.launch {
            _isRefreshing.emit(true)
            apiRepository.getDaftarArtikel().catch {
                _isRefreshing.emit(false)
                _artikelState.value = UiState.Error(it.message.toString())

            }.collect { response ->
                _isRefreshing.emit(false)
                when (response) {
                    is NetworkResponse.Success -> {
                        _artikelState.value = UiState.Success(response.body.body)
                    }

                    is NetworkResponse.ServerError -> {
                        _artikelState.value = UiState.Error(
                            response.body?.message
                                ?: "Terjadi kesalahan saat memproses data"
                        )
                    }

                    is NetworkResponse.NetworkError -> {
                        _artikelState.value = UiState.Error(
                            "Tolong periksa koneksi anda!"
                        )
                    }

                    is NetworkResponse.UnknownError -> {
                        _artikelState.value = UiState.Error(
                            response.error.localizedMessage
                                ?: "Unknown Error"
                        )
                    }
                }
            }
        }
    }

    fun getTabalongDistricts(token: String) {
        _isRefreshing.value = true
        viewModelScope.launch {
            apiRepository.getTabalongDistricts(token).catch {
                _isRefreshing.value = false
                _kecamatanState.value = UiState.Error(it.message.toString())

            }.collect { response ->
                when (response) {
                    is NetworkResponse.Success -> {
                        _isRefreshing.value = false
                        _kecamatanState.value = UiState.Success(response.body.body)
                    }

                    is NetworkResponse.ServerError -> {
                        _isRefreshing.value = false
                        if (response.code == 401) {
                            _kecamatanState.value = UiState.Unauthorized
                        } else {
                            _kecamatanState.value = UiState.Error(
                                response.body?.message
                                    ?: "Terjadi kesalahan saat memproses data"
                            )
                        }
                    }

                    is NetworkResponse.NetworkError -> {
                        _isRefreshing.value = false
                        _kecamatanState.value = UiState.Error(
                            response.error.localizedMessage ?: "Tolong periksa koneksi anda!"
                        )
                    }

                    is NetworkResponse.UnknownError -> {
                        _isRefreshing.value = false
                        _kecamatanState.value = UiState.Error(
                            response.error.localizedMessage
                                ?: "Unknown Error"
                        )
                    }
                }
            }
        }
    }

    fun getDaftarPuskes(token: String) {
        _isRefreshing.value = true
        viewModelScope.launch {
            apiRepository.getDaftarPuskes(token).catch {
                _isRefreshing.value = false
                _pkmState.value = UiState.Error(it.message.toString())

            }.collect { response ->
                when (response) {
                    is NetworkResponse.Success -> {
                        _isRefreshing.value = false
                        _pkmState.value = UiState.Success(response.body.body)
                    }

                    is NetworkResponse.ServerError -> {
                        _isRefreshing.value = false
                        if (response.code == 401) {
                            _kecamatanState.value = UiState.Unauthorized
                        } else {
                            _kecamatanState.value = UiState.Error(
                                response.body?.message
                                    ?: "Terjadi kesalahan saat memproses data"
                            )
                        }
                    }

                    is NetworkResponse.NetworkError -> {
                        _isRefreshing.value = false
                        _pkmState.value = UiState.Error(
                            response.error.localizedMessage ?: "Tolong periksa koneksi anda!"
                        )
                    }

                    is NetworkResponse.UnknownError -> {
                        _isRefreshing.value = false
                        _pkmState.value = UiState.Error(
                            response.error.localizedMessage
                                ?: "Unknown Error"
                        )
                    }
                }
            }
        }
    }


    // local tabalong data
    private val _kecState: MutableStateFlow<List<Kecamatan>> =
        MutableStateFlow(listOf())
    val kecState: StateFlow<List<Kecamatan>>
        get() = _kecState

    private fun getKecamatanLocally(): Flow<String> = userDataStoreRepository.getDataTabalong()
    fun getDataKecamatanInLocal() {
        viewModelScope.launch {
            getKecamatanLocally().collect {
                if (it != "") {
                    val itemType = object : TypeToken<List<Kecamatan>>() {}.type
                    _kecState.value = gson.fromJson(it, itemType)
                }
            }
        }
    }

    fun saveDataToLocal(data: List<Kecamatan>) = viewModelScope.launch {
        userDataStoreRepository.saveDataTabalong(data)
    }

    //local puskes data
    private val _puskesState: MutableStateFlow<List<PuskesmasResponse>> =
        MutableStateFlow(listOf())
    val puskesState: StateFlow<List<PuskesmasResponse>>
        get() = _puskesState

    private fun getPuskesLocally(): Flow<String> = userDataStoreRepository.getDataPuskes()
    fun getDataPuskesInLocal() {
        viewModelScope.launch {
            getPuskesLocally().collect {
                if (it != "") {
                    val itemType = object : TypeToken<List<PuskesmasResponse>>() {}.type
                    _puskesState.value = gson.fromJson(it, itemType)
                }
            }
        }
    }

    fun saveDataPuskesToLocal(data: List<PuskesmasResponse>) = viewModelScope.launch {
        userDataStoreRepository.saveDataPuskes(data)
    }
}