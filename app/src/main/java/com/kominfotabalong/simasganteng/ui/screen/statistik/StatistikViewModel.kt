package com.kominfotabalong.simasganteng.ui.screen.statistik

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.haroldadmin.cnradapter.NetworkResponse
import com.kominfotabalong.simasganteng.data.model.ResponseListObject
import com.kominfotabalong.simasganteng.data.model.StatistikResponse
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
class StatistikViewModel @Inject constructor(
    private val apiRepository: ApiRepository,
) : ViewModel() {

    private val _statistikState: MutableStateFlow<UiState<ResponseListObject<StatistikResponse>>> =
        MutableStateFlow(UiState.Loading)
    val statistikState: StateFlow<UiState<ResponseListObject<StatistikResponse>>>
        get() = _statistikState

    fun getDataStatistik(userToken: String, tahun: String, bulan: String? = null, tipe: String) {
        viewModelScope.launch {
            _statistikState.emit(UiState.Loading)
            apiRepository.getStatistikGizi(userToken, tahun, bulan, tipe).catch {
                _statistikState.value = UiState.Error(it.message.toString())

            }.collect { response ->
                when (response) {
                    is NetworkResponse.Success -> {
                        _statistikState.value = UiState.Success(response.body.body)
                    }

                    is NetworkResponse.ServerError -> {
                        if (response.code == 401) {
                            _statistikState.value = UiState.Unauthorized
                        } else {
                            _statistikState.value = UiState.Error(
                                response.body?.message
                                    ?: "Terjadi kesalahan saat memproses data"
                            )
                        }

                    }

                    is NetworkResponse.NetworkError -> {
                        _statistikState.value = UiState.Error("Tolong periksa koneksi anda!")
                    }

                    is NetworkResponse.UnknownError -> {
                        _statistikState.value = UiState.Error(
                            response.error.localizedMessage
                                ?: "Unknown Error"
                        )
                    }
                }
            }
        }
    }
}