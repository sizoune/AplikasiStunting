package com.kominfotabalong.simasganteng.ui.screen.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.AuthCredential
import com.kominfotabalong.simasganteng.data.model.GoogleAuthResponse
import com.kominfotabalong.simasganteng.data.repository.GoogleAuthRepo
import com.kominfotabalong.simasganteng.data.repository.OneTapSignInResponse
import com.kominfotabalong.simasganteng.data.repository.SignInWithGoogleResponse
import com.kominfotabalong.simasganteng.data.repository.SignOutResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repo: GoogleAuthRepo,
    val oneTapClient: SignInClient
) : ViewModel() {
    var signOutResponse by mutableStateOf<SignOutResponse>(GoogleAuthResponse.Success(false))
        private set

    val isUserAuthenticated get() = repo.isUserAuthenticatedInFirebase

    var oneTapSignInResponse by mutableStateOf<OneTapSignInResponse>(GoogleAuthResponse.Success(null))
        private set
    var signInWithGoogleResponse by mutableStateOf<SignInWithGoogleResponse>(
        GoogleAuthResponse.Success(
            false
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
}