package com.kominfotabalong.simasganteng.data.repository

import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.kominfotabalong.simasganteng.data.model.GoogleAuthResponse
import com.kominfotabalong.simasganteng.data.remote.FCMTokenResponse
import com.kominfotabalong.simasganteng.data.remote.GoogleAuthInterface
import com.kominfotabalong.simasganteng.data.remote.OneTapSignInResponse
import com.kominfotabalong.simasganteng.data.remote.SignInWithGoogleResponse
import com.kominfotabalong.simasganteng.data.remote.SignOutResponse
import com.kominfotabalong.simasganteng.util.Constants.SIGN_IN_REQUEST
import com.kominfotabalong.simasganteng.util.Constants.SIGN_UP_REQUEST
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class GoogleAuthRepo @Inject constructor(
    private val auth: FirebaseAuth,
    private var oneTapClient: SignInClient,
    @Named(SIGN_IN_REQUEST)
    private var signInRequest: BeginSignInRequest,
    @Named(SIGN_UP_REQUEST)
    private var signUpRequest: BeginSignInRequest,
) : GoogleAuthInterface {

    override val isUserAuthenticatedInFirebase = auth.currentUser != null

    override suspend fun oneTapSignInWithGoogle(): OneTapSignInResponse {
        return try {
            val signInResult = oneTapClient.beginSignIn(signInRequest).await()
            GoogleAuthResponse.Success(signInResult)
        } catch (e: Exception) {
            try {
                val signUpResult = oneTapClient.beginSignIn(signUpRequest).await()
                GoogleAuthResponse.Success(signUpResult)
            } catch (e: Exception) {
                println("login failed : ${e.localizedMessage} / Google Account Not Found!")
                GoogleAuthResponse.Failure(e)
            }
        }
    }

    override suspend fun firebaseSignInWithGoogle(googleCredential: AuthCredential): SignInWithGoogleResponse {
        return try {
            val authResult = auth.signInWithCredential(googleCredential).await()
            val isNewUser = authResult.additionalUserInfo?.isNewUser ?: false
            GoogleAuthResponse.Success(auth.currentUser)
        } catch (e: Exception) {
            GoogleAuthResponse.Failure(e)
        }
    }

    override suspend fun getFCMToken(): FCMTokenResponse {
        return try {
            val fcmReq = FirebaseMessaging.getInstance().token.await()
            println("fcmReq = $fcmReq")
            GoogleAuthResponse.Success(fcmReq)
        } catch (e: Exception) {
            GoogleAuthResponse.Failure(e)
        }
    }

    override suspend fun signOut(): SignOutResponse {
        return try {
            oneTapClient.signOut().await()
            auth.signOut()
            GoogleAuthResponse.Success(true)
        } catch (e: Exception) {
            GoogleAuthResponse.Failure(e)
        }
    }
}