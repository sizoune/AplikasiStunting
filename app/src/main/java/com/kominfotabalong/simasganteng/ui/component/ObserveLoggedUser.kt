package com.kominfotabalong.simasganteng.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import com.kominfotabalong.simasganteng.MainViewModel
import com.kominfotabalong.simasganteng.data.model.LoginResponse
import com.kominfotabalong.simasganteng.ui.common.UiState

@Composable
fun ObserveLoggedUser(
    mainViewModel: MainViewModel = hiltViewModel(),
    getData: Boolean,
    onUserObserved: @Composable (data: LoginResponse) -> Unit,
    onError: @Composable (errorMsg: String) -> Unit,
) {
    LaunchedEffect(getData) {
        mainViewModel.getUserData()
    }

    mainViewModel.uiState.collectAsState().value.let { uiState ->
        when (uiState) {
            is UiState.Loading -> {

            }

            is UiState.Success -> {
                onUserObserved(uiState.data)
            }

            is UiState.Error -> {
                onError(uiState.errorMessage)
            }

            is UiState.Unauthorized -> {}
        }
    }
}