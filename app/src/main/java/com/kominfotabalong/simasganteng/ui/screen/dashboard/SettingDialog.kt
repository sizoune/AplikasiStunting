package com.kominfotabalong.simasganteng.ui.screen.dashboard

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingDialog(
    modifier: Modifier = Modifier,
    showDialog: Boolean,
    onLogoutClick: () -> Unit,
    onEditProfileClick: () -> Unit,
    onDismiss: () -> Unit,
) {
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    if (showDialog)
        ModalBottomSheet(onDismissRequest = {
            scope.launch {
                bottomSheetState.hide()
            }.invokeOnCompletion { onDismiss() }
        }, sheetState = bottomSheetState) {
            Column(modifier = modifier.fillMaxWidth()) {
                TextButton(onClick = {
                    onEditProfileClick()
                    scope.launch {
                        bottomSheetState.hide()
                    }.invokeOnCompletion { onDismiss() }
                }, modifier = modifier.fillMaxWidth()) {
                    Text(
                        text = "Ubah Profil",
                        color = if (isSystemInDarkTheme()) Color.White else Color.Black
                    )
                }
                Divider(
                    modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 8.dp),
                    thickness = 1.dp,
                    color = Color.LightGray,
                )
                TextButton(onClick = {
                    onLogoutClick()
                    scope.launch {
                        bottomSheetState.hide()
                    }.invokeOnCompletion { onDismiss() }
                }, modifier = modifier.fillMaxWidth()) {
                    Text(text = "Keluar Aplikasi", color = Color.Red)
                }
                Spacer(modifier = modifier.size(8.dp))
            }
        }
}