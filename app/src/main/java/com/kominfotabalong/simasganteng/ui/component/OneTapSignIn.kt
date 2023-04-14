package com.kominfotabalong.simasganteng.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.kominfotabalong.simasganteng.data.model.GoogleAuthResponse.*
import com.kominfotabalong.simasganteng.ui.screen.login.LoginViewModel
import com.kominfotabalong.simasganteng.util.showToast

@Composable
fun OneTapSignIn(
    viewModel: LoginViewModel = hiltViewModel(),
    launch: (result: BeginSignInResult) -> Unit
) {
    val context = LocalContext.current
    when (val oneTapSignInResponse = viewModel.oneTapSignInResponse) {
        is Loading -> {
            Dialog(onDismissRequest = { }) {
                Loading()
            }
        }
        is Success -> oneTapSignInResponse.data?.let {
            LaunchedEffect(it) {
                launch(it)
            }
        }
        is Failure -> LaunchedEffect(Unit) {
            oneTapSignInResponse.e.localizedMessage?.let { context.showToast(it) }
            print(oneTapSignInResponse.e)
        }
    }
}
