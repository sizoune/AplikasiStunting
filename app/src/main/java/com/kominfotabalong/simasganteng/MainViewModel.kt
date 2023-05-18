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
import com.kominfotabalong.simasganteng.data.model.User
import com.kominfotabalong.simasganteng.data.repository.ApiRepository
import com.kominfotabalong.simasganteng.data.repository.UserDataStoreRepository
import com.kominfotabalong.simasganteng.ui.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    private val _kecamatanState: MutableStateFlow<UiState<ResponseListObject<Kecamatan>>> =
        MutableStateFlow(UiState.Loading)
    val kecamatanState: StateFlow<UiState<ResponseListObject<Kecamatan>>>
        get() = _kecamatanState

    private val _pkmState: MutableStateFlow<UiState<ResponseListObject<PuskesmasResponse>>> =
        MutableStateFlow(UiState.Loading)
    val pkmState: StateFlow<UiState<ResponseListObject<PuskesmasResponse>>>
        get() = _pkmState

    private val _uiState: MutableStateFlow<UiState<User>> =
        MutableStateFlow(UiState.Loading)
    val uiState: StateFlow<UiState<User>>
        get() = _uiState

    private val _pkmStaffState: MutableStateFlow<UiState<ResponseListObject<User>>> =
        MutableStateFlow(UiState.Loading)
    val pkmStaffState: StateFlow<UiState<ResponseListObject<User>>>
        get() = _pkmStaffState

    fun getDaftarArtikel() {
        viewModelScope.launch {
            _artikelState.emit(UiState.Loading)
            apiRepository.getDaftarArtikel().catch {
                _artikelState.value = UiState.Error(it.message.toString())

            }.collect { response ->
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
                        _artikelState.value = UiState.Error("Tolong periksa koneksi anda!")
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
        _kecamatanState.value = UiState.Loading
        viewModelScope.launch {
            apiRepository.getTabalongDistricts(token).catch {
                _kecamatanState.value = UiState.Error(it.message.toString())

            }.collect { response ->
                when (response) {
                    is NetworkResponse.Success -> {
                        _kecamatanState.value = UiState.Success(response.body.body)
                    }

                    is NetworkResponse.ServerError -> {
                        _kecamatanState.value = UiState.Error(
                            response.body?.message
                                ?: "Terjadi kesalahan saat memproses data"
                        )
                    }

                    is NetworkResponse.NetworkError -> {
                        _kecamatanState.value = UiState.Error("Tolong periksa koneksi anda!")
                    }

                    is NetworkResponse.UnknownError -> {
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
        viewModelScope.launch {
            _pkmState.value = UiState.Loading
            apiRepository.getDaftarPuskes(token).catch {
                _pkmState.value = UiState.Error(it.message.toString())

            }.collect { response ->
                when (response) {
                    is NetworkResponse.Success -> {
                        _pkmState.value = UiState.Success(response.body.body)
                    }

                    is NetworkResponse.ServerError -> {
                        _pkmState.value = UiState.Error(
                            response.body?.message
                                ?: "Terjadi kesalahan saat memproses data"
                        )
                    }

                    is NetworkResponse.NetworkError -> {
                        _pkmState.value = UiState.Error("Tolong periksa koneksi anda!")
                    }

                    is NetworkResponse.UnknownError -> {
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

    fun getDataKecamatanInLocal() {
        viewModelScope.launch {
            userDataStoreRepository.getDataTabalong().collect {
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

    fun getDataPuskesInLocal() {
        viewModelScope.launch {
            userDataStoreRepository.getDataPuskes().collect {
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

    fun updateProfile(token: String, name: String, username: String, phone: String, email: String) {
        viewModelScope.launch {
            _uiState.emit(UiState.Loading)
            apiRepository.updateProfile(token, name, username, phone, email).catch {
                _uiState.value = UiState.Error(it.message.toString())
            }.collect { response ->
                when (response) {
                    is NetworkResponse.Success -> {
                        _uiState.value = UiState.Success(response.body)
                    }

                    is NetworkResponse.ServerError -> {
                        if (response.code == 401) {
                            _uiState.value = UiState.Unauthorized
                        } else {
                            _uiState.emit(
                                UiState.Error(
                                    response.body?.message
                                        ?: "Terjadi kesalahan saat memproses data"
                                )
                            )
                        }
                    }

                    is NetworkResponse.NetworkError -> {
                        _uiState.emit(UiState.Error("Tolong periksa koneksi anda!"))
                    }

                    is NetworkResponse.UnknownError -> {
                        _uiState.emit(
                            UiState.Error(
                                response.error.localizedMessage
                                    ?: "Unknown Error"
                            )
                        )
                    }
                }
            }
        }
    }

    fun getDaftarPetugasPKM(token: String, pkmID: Int) {
        viewModelScope.launch {
            _pkmStaffState.emit(UiState.Loading)
            apiRepository.getDaftarPetugasPKM(token, pkmID).catch {
                _pkmStaffState.value = UiState.Error(it.message.toString())
            }.collect { response ->
                when (response) {
                    is NetworkResponse.Success -> {
                        _pkmStaffState.value = UiState.Success(response.body)
                    }

                    is NetworkResponse.ServerError -> {
                        if (response.code == 401) {
                            _pkmStaffState.value = UiState.Unauthorized
                        } else {
                            _pkmStaffState.emit(
                                UiState.Error(
                                    response.body?.message
                                        ?: "Terjadi kesalahan saat memproses data"
                                )
                            )
                        }
                    }

                    is NetworkResponse.NetworkError -> {
                        _pkmStaffState.emit(UiState.Error("Tolong periksa koneksi anda!"))
                    }

                    is NetworkResponse.UnknownError -> {
                        _pkmStaffState.emit(
                            UiState.Error(
                                response.error.localizedMessage
                                    ?: "Unknown Error"
                            )
                        )
                    }
                }
            }
        }
    }
}