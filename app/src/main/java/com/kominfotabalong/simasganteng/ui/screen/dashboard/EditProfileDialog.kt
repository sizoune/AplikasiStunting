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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.input.KeyboardType
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
import timber.log.Timber

@Composable
fun EditProfileDialog(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel,
    loginViewModel: LoginViewModel,
    userToken: String,
    showDialog: Boolean,
    currentUser: User,
    onUnauthorized: () -> Unit,
    onDismiss: () -> Unit,
) {
    var nameError by remember { mutableStateOf(false) }
    var name by remember {
        mutableStateOf(currentUser.name)
    }

    var usernameError by remember { mutableStateOf(false) }
    var username by remember {
        mutableStateOf(currentUser.username)
    }

    var phoneError by remember { mutableStateOf(false) }
    var phone by remember {
        mutableStateOf(currentUser.phone)
    }
    var isSubmitted by remember {
        mutableStateOf(false)
    }
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    fun validateInput(): Boolean {
        if (name == "") {
            nameError = true
            return false
        } else if (username == "") {
            usernameError = true
            return false
        } else if (phone == "") {
            phoneError = true
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
                        placeholderText = "Nama",
                        query = name,
                        isError = nameError,
                        errorMsg = "nama tidak boleh kosong!",
                        onQueryChange = { newText ->
                            name = newText
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = {
                                focusManager.moveFocus(FocusDirection.Down)
                            }
                        ),
                        modifier = modifier
                            .fillMaxWidth()

                    )

                    OutlinedTextFieldComp(
                        placeholderText = "Username",
                        query = username,
                        isError = usernameError,
                        errorMsg = "username tidak boleh kosong!",
                        onQueryChange = { newText ->
                            username = newText
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = {
                                focusManager.moveFocus(FocusDirection.Down)
                            }
                        ),
                        modifier = modifier
                            .fillMaxWidth()

                    )

                    OutlinedTextFieldComp(
                        placeholderText = "No HP / WA",
                        query = phone ?: "",
                        isError = phoneError,
                        errorMsg = "No HP / WA tidak boleh kosong!",
                        onQueryChange = { newText ->
                            phone = newText
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                if (validateInput()) {
                                    viewModel.updateProfile(
                                        userToken,
                                        name,
                                        username,
                                        phone ?: "",
                                        currentUser.email
                                    )
                                    isSubmitted = true
                                }
                            }
                        ),
                        modifier = modifier
                            .fillMaxWidth()

                    )

                    Button(
                        onClick = {
                            if (validateInput()) {
                                viewModel.updateProfile(
                                    userToken,
                                    name,
                                    username,
                                    phone ?: "",
                                    currentUser.email
                                )
                                isSubmitted = true
                            }
                        }, modifier = modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp)
                    ) {
                        Text(
                            text = "Update Profile",
                            color = if (isSystemInDarkTheme()) Color.White else Color.Black
                        )
                    }
                }
            }
        }

    if (isSubmitted)
        viewModel.uiState.collectAsStateWithLifecycle().value.let { uiState ->
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
                        loginViewModel.saveUserData(LoginResponse(userToken, uiState.data))
                        context.showToast("Profile berhasil diupdate!")
                        onDismiss()
                    }
                }
            }
        }
}