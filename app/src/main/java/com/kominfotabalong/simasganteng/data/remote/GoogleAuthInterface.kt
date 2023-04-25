package com.kominfotabalong.simasganteng.data.remote

import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import com.kominfotabalong.simasganteng.data.model.GoogleAuthResponse


typealias OneTapSignInResponse = GoogleAuthResponse<BeginSignInResult>
typealias SignInWithGoogleResponse = GoogleAuthResponse<FirebaseUser>
typealias SignOutResponse = GoogleAuthResponse<Boolean>
typealias FCMTokenResponse = GoogleAuthResponse<String>

interface GoogleAuthInterface {

    val isUserAuthenticatedInFirebase: Boolean

    suspend fun oneTapSignInWithGoogle(): OneTapSignInResponse

    suspend fun firebaseSignInWithGoogle(googleCredential: AuthCredential): SignInWithGoogleResponse

    suspend fun getFCMToken(): FCMTokenResponse

    suspend fun signOut(): SignOutResponse
}