package com.kominfotabalong.simasganteng.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.kominfotabalong.simasganteng.data.model.GoogleAuthResponse
import com.kominfotabalong.simasganteng.ui.screen.login.LoginViewModel
import com.kominfotabalong.simasganteng.util.showToast

@Composable
fun GoogleSignOut(
    viewModel: LoginViewModel = hiltViewModel(),
    navigateToLoginScreen: (signedIn: Boolean) -> Unit
) {
    val context = LocalContext.current
    when (val signOutResponse = viewModel.signOutResponse) {
        is GoogleAuthResponse.Loading -> {
            Dialog(onDismissRequest = {}) {
                Loading()
            }
        }

        is GoogleAuthResponse.Success -> signOutResponse.data?.let { signedOut ->
            LaunchedEffect(signedOut) {
                navigateToLoginScreen(signedOut)
            }
        }

        is GoogleAuthResponse.Failure -> LaunchedEffect(Unit) {
            signOutResponse.e.localizedMessage?.let { context.showToast(it) }
            print(signOutResponse.e)
        }
    }
}