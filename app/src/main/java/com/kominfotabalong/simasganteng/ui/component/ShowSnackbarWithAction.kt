package com.kominfotabalong.simasganteng.ui.component

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

@Composable
fun ShowSnackbarWithAction(
    snackbarHostState: SnackbarHostState,
    errorMsg: String,
    showSnackBar: Boolean,
    onRetryClick: () -> Unit,
    onDismiss: (Boolean) -> Unit
) {
    LaunchedEffect(showSnackBar) {
        when (snackbarHostState.showSnackbar(errorMsg, "Retry", true)) {
            SnackbarResult.ActionPerformed -> {
                onDismiss(false)
                onRetryClick()
            }

            SnackbarResult.Dismissed -> {
                onDismiss(false)
            }
        }
    }
}