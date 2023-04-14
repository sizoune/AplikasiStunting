package com.kominfotabalong.simasganteng.data.repository

import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.google.firebase.auth.AuthCredential
import com.kominfotabalong.simasganteng.data.model.GoogleAuthResponse

typealias OneTapSignInResponse = GoogleAuthResponse<BeginSignInResult>
typealias SignInWithGoogleResponse = GoogleAuthResponse<Boolean>
typealias SignOutResponse = GoogleAuthResponse<Boolean>

interface GoogleAuthInterface {

    val isUserAuthenticatedInFirebase: Boolean

    suspend fun oneTapSignInWithGoogle(): OneTapSignInResponse

    suspend fun firebaseSignInWithGoogle(googleCredential: AuthCredential): SignInWithGoogleResponse

    suspend fun signOut(): SignOutResponse
}