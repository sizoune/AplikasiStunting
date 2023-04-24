package com.kominfotabalong.simasganteng.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.kominfotabalong.simasganteng.ui.screen.login.LoginViewModel
import com.kominfotabalong.simasganteng.util.showToast

@Composable
fun LogoutHandler(
    logoutMsg: String = "",
    loginViewModel: LoginViewModel = hiltViewModel(),
) {
    if (logoutMsg != "") LocalContext.current.showToast(logoutMsg)
    loginViewModel.saveUserData(null)
    loginViewModel.logOut()
}