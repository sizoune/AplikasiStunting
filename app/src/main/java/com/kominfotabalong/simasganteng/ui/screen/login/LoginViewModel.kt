package com.kominfotabalong.simasganteng.ui.screen.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.AuthCredential
import com.haroldadmin.cnradapter.NetworkResponse
import com.kominfotabalong.simasganteng.data.model.GoogleAuthResponse
import com.kominfotabalong.simasganteng.data.model.LoginResponse
import com.kominfotabalong.simasganteng.data.model.ResponseObject
import com.kominfotabalong.simasganteng.data.remote.OneTapSignInResponse
import com.kominfotabalong.simasganteng.data.remote.SignInWithGoogleResponse
import com.kominfotabalong.simasganteng.data.remote.SignOutResponse
import com.kominfotabalong.simasganteng.data.repository.ApiRepository
import com.kominfotabalong.simasganteng.data.repository.GoogleAuthRepo
import com.kominfotabalong.simasganteng.data.repository.UserDataStoreRepository
import com.kominfotabalong.simasganteng.ui.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repo: GoogleAuthRepo,
    private val apiRepository: ApiRepository,
    private val userDataStoreRepository: UserDataStoreRepository,
    val oneTapClient: SignInClient,
) : ViewModel() {
    var signOutResponse by mutableStateOf<SignOutResponse>(GoogleAuthResponse.Success(false))
        private set

    val isUserAuthenticated get() = repo.isUserAuthenticatedInFirebase

    var oneTapSignInResponse by mutableStateOf<OneTapSignInResponse>(GoogleAuthResponse.Success(null))
        private set
    var signInWithGoogleResponse by mutableStateOf<SignInWithGoogleResponse>(
        GoogleAuthResponse.Success(
            null
        )
    )
        private set

    fun oneTapSignIn() = viewModelScope.launch {
        oneTapSignInResponse = GoogleAuthResponse.Loading
        oneTapSignInResponse = repo.oneTapSignInWithGoogle()
    }

    fun signInWithGoogle(googleCredential: AuthCredential) = viewModelScope.launch {
        oneTapSignInResponse = GoogleAuthResponse.Loading
        signInWithGoogleResponse = repo.firebaseSignInWithGoogle(googleCredential)
    }

    fun logOut() = viewModelScope.launch {
        signOutResponse = GoogleAuthResponse.Loading
        signOutResponse = repo.signOut()
    }

    fun saveUserData(data: LoginResponse?) {
        viewModelScope.launch {
            userDataStoreRepository.saveUserData(data)
        }
    }

    private val _uiState: MutableStateFlow<UiState<ResponseObject<LoginResponse>>> =
        MutableStateFlow(UiState.Loading)
    val uiState: StateFlow<UiState<ResponseObject<LoginResponse>>>
        get() = _uiState

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean>
        get() = _isRefreshing.asStateFlow()

    fun doLogin(username: String, pass: String) {
        viewModelScope.launch {
            _isRefreshing.emit(true)
            apiRepository.doLogin(username, pass).catch {
                _isRefreshing.emit(false)
                _uiState.value = UiState.Error(it.message.toString())

            }.collect { response ->
                when (response) {
                    is NetworkResponse.Success -> {
                        _isRefreshing.emit(false)
                        _uiState.value = UiState.Success(response.body.body)
                    }

                    is NetworkResponse.ServerError -> {
                        _isRefreshing.emit(false)
                        _uiState.value = UiState.Error(
                            response.body?.message
                                ?: "Terjadi kesalahan saat memproses data"
                        )
                    }

                    is NetworkResponse.NetworkError -> {
                        _isRefreshing.emit(false)
                        _uiState.value = UiState.Error(
                            "Tolong periksa koneksi anda!"
                        )
                    }

                    is NetworkResponse.UnknownError -> {
                        _isRefreshing.emit(false)
                        _uiState.value = UiState.Error(
                            response.error.localizedMessage
                                ?: "Unknown Error"
                        )
                    }
                }
            }
        }
    }

    fun doLoginWithGoogle(email: String, name: String, firebaseToken: String) {
        viewModelScope.launch {
            _isRefreshing.emit(true)
            apiRepository.doLoginWithGoogle(email, name, firebaseToken).catch {
                _isRefreshing.emit(false)
                _uiState.value = UiState.Error(it.message.toString())

            }.collect { response ->
                when (response) {
                    is NetworkResponse.Success -> {
                        _isRefreshing.emit(false)
                        _uiState.value = UiState.Success(response.body.body)
                    }

                    is NetworkResponse.ServerError -> {
                        _isRefreshing.emit(false)
                        _uiState.value = UiState.Error(
                            response.body?.message
                                ?: "Terjadi kesalahan saat memproses data"
                        )
                    }

                    is NetworkResponse.NetworkError -> {
                        _isRefreshing.emit(false)
                        _uiState.value = UiState.Error(
                            "Tolong periksa koneksi anda!"
                        )
                    }

                    is NetworkResponse.UnknownError -> {
                        _isRefreshing.emit(false)
                        _uiState.value = UiState.Error(
                            response.error.localizedMessage
                                ?: "Unknown Error"
                        )
                    }
                }
            }
        }
    }
}