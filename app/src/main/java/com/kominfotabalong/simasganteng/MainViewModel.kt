package com.kominfotabalong.simasganteng

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.kominfotabalong.simasganteng.data.model.LoginResponse
import com.kominfotabalong.simasganteng.data.repository.UserDataStoreRepository
import com.kominfotabalong.simasganteng.ui.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userDataStoreRepository: UserDataStoreRepository,
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
}