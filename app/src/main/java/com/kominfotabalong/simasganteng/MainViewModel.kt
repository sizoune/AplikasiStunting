package com.kominfotabalong.simasganteng

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.haroldadmin.cnradapter.NetworkResponse
import com.kominfotabalong.simasganteng.data.model.ArtikelResponse
import com.kominfotabalong.simasganteng.data.model.LoginResponse
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
    private val userDataStoreRepository: UserDataStoreRepository,
    private val apiRepository: ApiRepository,
    private val gson: Gson,
) : ViewModel() {
    private val _uiState: MutableStateFlow<UiState<LoginResponse>> =
        MutableStateFlow(UiState.Loading)
    val uiState: StateFlow<UiState<LoginResponse>>
        get() = _uiState


    fun getLoggedUserData(): Flow<String> = userDataStoreRepository.getLoggedUser()

    fun getUserData() {
        viewModelScope.launch {
            getLoggedUserData().catch {
                _uiState.value = UiState.Error(it.message.toString())
            }.collect {
                if (it != "")
                    _uiState.value = UiState.Success(gson.fromJson(it, LoginResponse::class.java))

            }
        }
    }

    private val _artikelState: MutableStateFlow<UiState<ResponseListObject<ArtikelResponse>>> =
        MutableStateFlow(UiState.Loading)
    val artikelState: StateFlow<UiState<ResponseListObject<ArtikelResponse>>>
        get() = _artikelState

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean>
        get() = _isRefreshing.asStateFlow()

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
}