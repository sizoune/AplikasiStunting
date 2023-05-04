package com.kominfotabalong.simasganteng.ui.screen.pengukuran

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haroldadmin.cnradapter.NetworkResponse
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
}