package com.kominfotabalong.simasganteng.ui.screen.pengukuran

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haroldadmin.cnradapter.NetworkResponse
import com.kominfotabalong.simasganteng.data.model.ApiBaseResponse
import com.kominfotabalong.simasganteng.data.model.PengukuranRequest
import com.kominfotabalong.simasganteng.data.model.PengukuranResponse
import com.kominfotabalong.simasganteng.data.model.ResponseListObject
import com.kominfotabalong.simasganteng.data.repository.ApiRepository
import com.kominfotabalong.simasganteng.ui.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PengukuranViewModel @Inject constructor(
    private val apiRepository: ApiRepository
) : ViewModel() {

    private val _ukurState: MutableStateFlow<UiState<ResponseListObject<PengukuranResponse>>> =
        MutableStateFlow(UiState.Loading)
    val ukurState: StateFlow<UiState<ResponseListObject<PengukuranResponse>>>
        get() = _ukurState

    private val _ukurOperationState: MutableStateFlow<UiState<PengukuranResponse>> =
        MutableStateFlow(UiState.Loading)
    val ukurOperationState: StateFlow<UiState<PengukuranResponse>>
        get() = _ukurOperationState

    private val _delOperationState: MutableStateFlow<UiState<ApiBaseResponse>> =
        MutableStateFlow(UiState.Loading)
    val delOperationState: StateFlow<UiState<ApiBaseResponse>>
        get() = _delOperationState

    fun getDaftarPengukuran(token: String, balitaID: Int) {
        viewModelScope.launch {
            _ukurState.emit(UiState.Loading)
            apiRepository.getDaftarPengukuran(token, balitaID).catch {
                _ukurState.emit(UiState.Error(it.message.toString()))
            }.collect { response ->
                when (response) {
                    is NetworkResponse.Success -> {
                        _ukurState.value = UiState.Success(response.body.body)
                    }

                    is NetworkResponse.ServerError -> {
                        if (response.code == 401) {
                            _ukurState.value = UiState.Unauthorized
                        } else {
                            _ukurState.emit(
                                UiState.Error(
                                    response.body?.message
                                        ?: "Terjadi kesalahan saat memproses data"
                                )
                            )
                        }
                    }

                    is NetworkResponse.NetworkError -> {
                        _ukurState.emit(
                            UiState.Error(
                                response.error.localizedMessage
                                    ?: "Terjadi kesalahan saat memproses data"
                            )
                        )
                    }

                    is NetworkResponse.UnknownError -> {
                        _ukurState.emit(
                            UiState.Error(
                                response.error.localizedMessage ?: "Unknown Error"
                            )
                        )
                    }
                }
            }
        }
    }

    fun tambahPengukuran(token: String, balitaID: Int, pengukuranRequest: PengukuranRequest) {
        viewModelScope.launch {
            _ukurOperationState.emit(UiState.Loading)
            apiRepository.tambahPengukuran(token, balitaID, pengukuranRequest).catch {
                _ukurOperationState.emit(UiState.Error(it.message.toString()))
            }.collect { response ->
                when (response) {
                    is NetworkResponse.Success -> {
                        _ukurOperationState.value = UiState.Success(response.body.data)
                    }

                    is NetworkResponse.ServerError -> {
                        if (response.code == 401) {
                            _ukurOperationState.value = UiState.Unauthorized
                        } else {
                            _ukurOperationState.emit(
                                UiState.Error(
                                    response.body?.message
                                        ?: "Terjadi kesalahan saat memproses data"
                                )
                            )
                        }
                    }

                    is NetworkResponse.NetworkError -> {
                        _ukurOperationState.emit(
                            UiState.Error(
                                response.error.localizedMessage
                                    ?: "Terjadi kesalahan saat memproses data"
                            )
                        )
                    }

                    is NetworkResponse.UnknownError -> {
                        _ukurOperationState.emit(
                            UiState.Error(
                                response.error.localizedMessage ?: "Unknown Error"
                            )
                        )
                    }
                }
            }
        }
    }

    fun updatePengukuran(token: String, pengukuranID: Int, pengukuranRequest: PengukuranRequest) {
        viewModelScope.launch {
            _ukurOperationState.emit(UiState.Loading)
            apiRepository.updatePengukuran(token, pengukuranID, pengukuranRequest).catch {
                _ukurOperationState.emit(UiState.Error(it.message.toString()))
            }.collect { response ->
                when (response) {
                    is NetworkResponse.Success -> {
                        _ukurOperationState.value = UiState.Success(response.body.data)
                    }

                    is NetworkResponse.ServerError -> {
                        if (response.code == 401) {
                            _ukurOperationState.value = UiState.Unauthorized
                        } else {
                            _ukurOperationState.emit(
                                UiState.Error(
                                    response.body?.message
                                        ?: "Terjadi kesalahan saat memproses data"
                                )
                            )
                        }
                    }

                    is NetworkResponse.NetworkError -> {
                        _ukurOperationState.emit(
                            UiState.Error(
                                response.error.localizedMessage
                                    ?: "Terjadi kesalahan saat memproses data"
                            )
                        )
                    }

                    is NetworkResponse.UnknownError -> {
                        _ukurOperationState.emit(
                            UiState.Error(
                                response.error.localizedMessage ?: "Unknown Error"
                            )
                        )
                    }
                }
            }
        }
    }

    fun deletePengukuran(token: String, pengukuranID: Int) {
        viewModelScope.launch {
            _delOperationState.emit(UiState.Loading)
            apiRepository.deletePengukuran(token, pengukuranID).catch {
                _delOperationState.emit(UiState.Error(it.message.toString()))
            }.collect { response ->
                when (response) {
                    is NetworkResponse.Success -> {
                        _delOperationState.value = UiState.Success(response.body.body)
                    }

                    is NetworkResponse.ServerError -> {
                        if (response.code == 401) {
                            _delOperationState.value = UiState.Unauthorized
                        } else {
                            _delOperationState.emit(
                                UiState.Error(
                                    response.body?.message
                                        ?: "Terjadi kesalahan saat memproses data"
                                )
                            )
                        }
                    }

                    is NetworkResponse.NetworkError -> {
                        _delOperationState.emit(
                            UiState.Error(
                                response.error.localizedMessage
                                    ?: "Terjadi kesalahan saat memproses data"
                            )
                        )
                    }

                    is NetworkResponse.UnknownError -> {
                        _delOperationState.emit(
                            UiState.Error(
                                response.error.localizedMessage ?: "Unknown Error"
                            )
                        )
                    }
                }
            }
        }
    }
}