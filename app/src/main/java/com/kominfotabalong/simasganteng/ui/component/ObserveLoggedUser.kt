package com.kominfotabalong.simasganteng.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kominfotabalong.simasganteng.data.model.LoginResponse
import com.kominfotabalong.simasganteng.ui.screen.login.LoginViewModel

@Composable
fun ObserveLoggedUser(
    mainViewModel: LoginViewModel,
    onUserObserved: @Composable (data: LoginResponse) -> Unit,
) {

    LaunchedEffect(Unit) {
        mainViewModel.getUserDataV2()
    }

    mainViewModel.userState.collectAsStateWithLifecycle().value.let {
        onUserObserved(it)
    }

}