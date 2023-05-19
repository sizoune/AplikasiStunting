package com.kominfotabalong.simasganteng.ui.screen.dashboard

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kominfotabalong.simasganteng.MainViewModel
import com.kominfotabalong.simasganteng.data.model.LoginResponse
import com.kominfotabalong.simasganteng.data.model.User
import com.kominfotabalong.simasganteng.ui.common.UiState
import com.kominfotabalong.simasganteng.ui.component.Loading
import com.kominfotabalong.simasganteng.ui.component.OutlinedTextFieldComp
import com.kominfotabalong.simasganteng.ui.screen.login.LoginViewModel
import com.kominfotabalong.simasganteng.util.showToast

@Composable
fun ChangePasswordDialog(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel,
    userToken: String,
    showDialog: Boolean,
    onUnauthorized: () -> Unit,
    onDismiss: () -> Unit,
) {
    var newPassError by remember { mutableStateOf(false) }
    var newPass by remember {
        mutableStateOf("")
    }
    var newPassConfirmError by remember { mutableStateOf(false) }
    var newPassConfirm by remember {
        mutableStateOf("")
    }
    var newPassConfirmErrorMsg by remember {
        mutableStateOf("")
    }
    var masked by remember {
        mutableStateOf(true)
    }
    var isSubmitted by remember {
        mutableStateOf(false)
    }
    val visualTransformation by remember(masked) {
        if (masked)
            mutableStateOf(PasswordVisualTransformation())
        else
            mutableStateOf(VisualTransformation.None)
    }

    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    fun validateInput(): Boolean {
        if (newPass == "") {
            newPassError = true
            return false
        } else if (newPassConfirm == "") {
            newPassConfirmError = true
            newPassConfirmErrorMsg = "Tolong Isi Konfirmasi Password Baru dahulu!"
            return false
        } else if (newPassConfirm != newPass) {
            newPassConfirmError = true
            newPassConfirmErrorMsg = "Konfirmasi Password Baru tidak sama!"
            return false
        }
        return true
    }

    if (showDialog)
        Dialog(onDismissRequest = onDismiss) {
            Card(
                elevation = CardDefaults.cardElevation(10.dp),
                shape = RoundedCornerShape(15.dp),
                modifier = modifier
            ) {
                IconButton(
                    onClick = {
                        onDismiss()
                    },
                    modifier = modifier.align(Alignment.End)
                ) {
                    Icon(imageVector = Icons.Filled.Close, contentDescription = "tutup")
                }
                Column(modifier = modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) {
                    OutlinedTextFieldComp(
                        placeholderText = "Password Baru",
                        visualTransformation = visualTransformation,
                        trailingIcon = {
                            if (masked) {
                                IconButton(onClick = {
                                    masked = false
                                }) {
                                    Icon(
                                        imageVector = Icons.Filled.VisibilityOff,
                                        contentDescription = null
                                    )
                                }
                            } else {
                                IconButton(onClick = { masked = true }) {
                                    Icon(
                                        imageVector = Icons.Filled.Visibility,
                                        contentDescription = null
                                    )
                                }
                            }
                        },
                        query = newPass,
                        onQueryChange = {
                            newPass = it
                            newPassError = false
                        },
                        isError = newPassError,
                        errorMsg = "Tolong isi Password baru anda dahulu !",
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(
                            onNext = {
                                focusManager.moveFocus(FocusDirection.Down)
                            }
                        ),
                    )

                    OutlinedTextFieldComp(
                        placeholderText = "Konfirmasi Password Baru",
                        visualTransformation = PasswordVisualTransformation(),
                        query = newPassConfirm,
                        onQueryChange = {
                            newPassConfirm = it
                            newPassConfirmError = false
                        },
                        isError = newPassConfirmError,
                        errorMsg = newPassConfirmErrorMsg,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                if (validateInput()) {
                                    mainViewModel.changePassword(userToken, newPass, newPassConfirm)
                                    isSubmitted = true
                                }
                            }
                        ),
                    )

                    Button(
                        onClick = {
                            if (validateInput()) {
                                mainViewModel.changePassword(userToken, newPass, newPassConfirm)
                                isSubmitted = true
                            }
                        }, modifier = modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp)
                    ) {
                        Text(
                            text = "Ganti Password",
                            color = if (isSystemInDarkTheme()) Color.White else Color.Black
                        )
                    }
                }
            }
        }

    if (isSubmitted)
        mainViewModel.uiState.collectAsStateWithLifecycle().value.let { uiState ->
            when (uiState) {
                is UiState.Loading -> {
                    Dialog(onDismissRequest = { }) {
                        Loading()
                    }
                }

                is UiState.Unauthorized -> {
                    onUnauthorized()
                    onDismiss()
                }

                is UiState.Error -> {
                    LaunchedEffect(key1 = Unit) {
                        context.showToast(uiState.errorMessage)
                    }
                }

                is UiState.Success -> {
                    LaunchedEffect(key1 = Unit) {
                        context.showToast("Password berhasil diubah!")
                        onDismiss()
                    }
                }
            }
        }
}