package com.kominfotabalong.simasganteng.ui.component

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

@Composable
fun ShowSnackbarWithAction(
    snackbarHostState: SnackbarHostState,
    errorMsg: String,
    onRetryClick: () -> Unit,
) {
    LaunchedEffect(Unit) {
        when (snackbarHostState.showSnackbar(errorMsg, "Retry", true)) {
            SnackbarResult.ActionPerformed -> {
                onRetryClick()
            }

            SnackbarResult.Dismissed -> {
            }
        }
    }
}