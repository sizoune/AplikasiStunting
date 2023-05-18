package com.kominfotabalong.simasganteng.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Dialog
import com.google.firebase.auth.FirebaseUser
import com.kominfotabalong.simasganteng.data.model.GoogleAuthResponse
import com.kominfotabalong.simasganteng.ui.screen.login.LoginViewModel
import com.kominfotabalong.simasganteng.util.showToast

@Composable
fun SignInWithGoogle(
    viewModel: LoginViewModel,
    navigateToHomeScreen: (currentUser: FirebaseUser) -> Unit
) {
    val context = LocalContext.current
    when (val signInWithGoogleResponse = viewModel.signInWithGoogleResponse) {
        is GoogleAuthResponse.Loading -> {
            Dialog(onDismissRequest = {}) {
                Loading()
            }
        }

        is GoogleAuthResponse.Success -> signInWithGoogleResponse.data?.let { signedIn ->
            LaunchedEffect(signedIn) {
                navigateToHomeScreen(signedIn)
            }
        }

        is GoogleAuthResponse.Failure -> LaunchedEffect(Unit) {
            signInWithGoogleResponse.e.localizedMessage?.let { context.showToast(it) }
            print(signInWithGoogleResponse.e)
        }
    }
}