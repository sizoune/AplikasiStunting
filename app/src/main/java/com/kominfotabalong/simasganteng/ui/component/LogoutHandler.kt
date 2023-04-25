package com.kominfotabalong.simasganteng.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.kominfotabalong.simasganteng.ui.screen.login.LoginViewModel
import com.kominfotabalong.simasganteng.util.showToast
import com.ramcosta.composedestinations.annotation.Destination

@Composable
@Destination
fun LogoutHandler(
    logoutMsg: String = "",
    loginViewModel: LoginViewModel,
) {
    val context = LocalContext.current

    if (logoutMsg != "") context.showToast(logoutMsg)
    loginViewModel.setLoginStatus(false)
    loginViewModel.logOut()
}