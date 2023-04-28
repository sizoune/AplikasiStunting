package com.kominfotabalong.simasganteng.ui.screen.login

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.AuthCredential
import com.google.gson.Gson
import com.haroldadmin.cnradapter.NetworkResponse
import com.kominfotabalong.simasganteng.data.model.ApiBaseResponse
import com.kominfotabalong.simasganteng.data.model.GoogleAuthResponse
import com.kominfotabalong.simasganteng.data.model.LoginResponse
import com.kominfotabalong.simasganteng.data.model.ResponseObject
import com.kominfotabalong.simasganteng.data.remote.FCMTokenResponse
import com.kominfotabalong.simasganteng.data.remote.OneTapSignInResponse
import com.kominfotabalong.simasganteng.data.remote.SignInWithGoogleResponse
import com.kominfotabalong.simasganteng.data.remote.SignOutResponse
import com.kominfotabalong.simasganteng.data.repository.ApiRepository
import com.kominfotabalong.simasganteng.data.repository.GoogleAuthRepo
import com.kominfotabalong.simasganteng.data.repository.UserDataStoreRepository
import com.kominfotabalong.simasganteng.service.LogoutWorker
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
class LoginViewModel @Inject constructor(
    private val repo: GoogleAuthRepo,
    private val apiRepository: ApiRepository,
    private val userDataStoreRepository: UserDataStoreRepository,
    private val application: Application,
    private val gson: Gson,
    val oneTapClient: SignInClient,
) : ViewModel() {

    var signOutResponse by mutableStateOf<SignOutResponse>(GoogleAuthResponse.Success(false))
        private set

    var fcmResponse by mutableStateOf<FCMTokenResponse>(GoogleAuthResponse.Loading)
        private set

    fun getLoggedUserData(): Flow<String> = userDataStoreRepository.getLoggedUser()

    private val _userState: MutableStateFlow<LoginResponse> =
        MutableStateFlow(LoginResponse())
    val userState: StateFlow<LoginResponse>
        get() = _userState

    fun getUserDataV2() {
        viewModelScope.launch {
            getLoggedUserData().collect {
                if (it != "")
                    _userState.value = gson.fromJson(it, LoginResponse::class.java)
            }
        }
    }

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
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val logoutWorkRequest: WorkRequest =
            OneTimeWorkRequestBuilder<LogoutWorker>()
                .setConstraints(constraints)
                .addTag("FCMWorker")
                .build()

        WorkManager
            .getInstance(application)
            .enqueue(logoutWorkRequest)

        signOutResponse = GoogleAuthResponse.Loading
        signOutResponse = repo.signOut()
    }

    fun saveUserData(data: LoginResponse?) {
        viewModelScope.launch {
            userDataStoreRepository.saveUserData(data)
        }
    }

    fun getFCMToken() = viewModelScope.launch {
        fcmResponse = repo.getFCMToken()
    }

    private val _uiState: MutableStateFlow<UiState<ResponseObject<LoginResponse>>> =
        MutableStateFlow(UiState.Loading)
    val uiState: StateFlow<UiState<ResponseObject<LoginResponse>>>
        get() = _uiState

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean>
        get() = _isRefreshing.asStateFlow()

    private val _isFinishLogin = MutableStateFlow(false)
    val isFinishLogin: StateFlow<Boolean>
        get() = _isFinishLogin.asStateFlow()

    fun setLoginStatus(isFinish: Boolean) {
        _isFinishLogin.value = isFinish
    }

    private val _fcmState: MutableStateFlow<UiState<ApiBaseResponse>> =
        MutableStateFlow(UiState.Loading)
    val fcmState: StateFlow<UiState<ApiBaseResponse>>
        get() = _fcmState

    private val _isError: MutableStateFlow<String> =
        MutableStateFlow("")
    val isError: StateFlow<String>
        get() = _isError

    fun doLogin(username: String, pass: String) {
        viewModelScope.launch {
            _isError.value = ""
            _isRefreshing.emit(true)
            apiRepository.doLogin(username, pass).catch {
                _isRefreshing.emit(false)
                _isError.value = it.message.toString()
            }.collect { response ->
                when (response) {
                    is NetworkResponse.Success -> {
                        _isRefreshing.emit(false)
                        _uiState.value = UiState.Success(response.body.body)
                    }

                    is NetworkResponse.ServerError -> {
                        _isRefreshing.emit(false)
                        _isError.value = response.body?.message
                            ?: "Terjadi kesalahan saat memproses data"
                    }

                    is NetworkResponse.NetworkError -> {
                        _isRefreshing.emit(false)
                        _isError.value = "Tolong periksa koneksi anda!"
                    }

                    is NetworkResponse.UnknownError -> {
                        _isRefreshing.emit(false)
                        _isError.value = response.error.localizedMessage
                            ?: "Unknown Error"
                    }
                }
            }
        }
    }

    fun doLoginWithGoogle(email: String, name: String, firebaseToken: String) {
        viewModelScope.launch {
            _isError.value = ""
            _isRefreshing.emit(true)
            apiRepository.doLoginWithGoogle(email, name, firebaseToken).catch {
                _isRefreshing.emit(false)
                _isError.value = it.message.toString()
            }.collect { response ->
                when (response) {
                    is NetworkResponse.Success -> {
                        _isRefreshing.emit(false)
                        _uiState.value = UiState.Success(response.body.body)
                    }

                    is NetworkResponse.ServerError -> {
                        _isRefreshing.emit(false)
                        _isError.value = response.body?.message
                            ?: "Terjadi kesalahan saat memproses data"
                    }

                    is NetworkResponse.NetworkError -> {
                        _isRefreshing.emit(false)
                        _isError.value = "Tolong periksa koneksi anda!"
                    }

                    is NetworkResponse.UnknownError -> {
                        _isRefreshing.emit(false)
                        _isError.value = response.error.localizedMessage
                            ?: "Unknown Error"
                    }
                }
            }
        }
    }

    fun postFCMToken(userToken: String, fcmToken: String?) {
        viewModelScope.launch {
            _isError.value = ""
            _isRefreshing.emit(true)
            apiRepository.postFCMToken(userToken, fcmToken).catch {
                _isRefreshing.emit(false)
                _isError.value = it.message.toString()
            }.collect { response ->
                when (response) {
                    is NetworkResponse.Success -> {
                        _isRefreshing.emit(false)
                        _fcmState.value = UiState.Success(response.body.body)
                    }

                    is NetworkResponse.ServerError -> {
                        _isRefreshing.emit(false)
                        _isError.value = response.body?.message
                            ?: "Terjadi kesalahan saat memproses data"
                    }

                    is NetworkResponse.NetworkError -> {
                        _isRefreshing.emit(false)
                        _isError.value = "Tolong periksa koneksi anda!"
                    }

                    is NetworkResponse.UnknownError -> {
                        _isRefreshing.emit(false)
                        _isError.value = response.error.localizedMessage
                            ?: "Unknown Error"
                    }
                }
            }
        }
    }
}