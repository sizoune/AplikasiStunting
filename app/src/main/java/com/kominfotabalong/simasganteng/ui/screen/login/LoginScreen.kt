package com.kominfotabalong.simasganteng.ui.screen.login

import android.app.Activity.RESULT_OK
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider.getCredential
import com.kominfotabalong.simasganteng.R
import com.kominfotabalong.simasganteng.data.model.GoogleAuthResponse
import com.kominfotabalong.simasganteng.ui.common.UiState
import com.kominfotabalong.simasganteng.ui.component.Loading
import com.kominfotabalong.simasganteng.ui.component.OneTapSignIn
import com.kominfotabalong.simasganteng.ui.component.OutlinedTextFieldComp
import com.kominfotabalong.simasganteng.ui.component.ShowSnackbarWithAction
import com.kominfotabalong.simasganteng.ui.component.SignInWithGoogle
import com.kominfotabalong.simasganteng.util.Constants.LOGIN_ADMIN
import com.kominfotabalong.simasganteng.util.Constants.LOGIN_GOOGLE
import com.kominfotabalong.simasganteng.util.showToast
import com.ramcosta.composedestinations.annotation.Destination
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalFoundationApi::class)
@Composable
@Destination
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel(),
    snackbarHostState: SnackbarHostState,
    onLoginSuccess: () -> Unit,
) {
    val context = LocalContext.current
    val coroutine = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val bringIntoViewRequester = BringIntoViewRequester()

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                try {
                    val credentials =
                        viewModel.oneTapClient.getSignInCredentialFromIntent(result.data)
                    val googleIdToken = credentials.googleIdToken
                    val googleCredentials = getCredential(googleIdToken, null)
                    viewModel.signInWithGoogle(googleCredentials)
                } catch (it: ApiException) {
                    print(it)
                    context.showToast("gagal login : $it")
                }
            }
        }

    var loginState by remember {
        mutableStateOf("")
    }

    var usernameText by remember {
        mutableStateOf("")
    }

    var passwordText by remember {
        mutableStateOf("")
    }
    var masked by remember {
        mutableStateOf(true)
    }
    val visualTransformation by remember(masked) {
        if (masked)
            mutableStateOf(PasswordVisualTransformation())
        else
            mutableStateOf(VisualTransformation.None)
    }

    fun doLogin() {
        if (usernameText != "" && passwordText != "") {
            loginState = LOGIN_ADMIN
            viewModel.doLogin(usernameText, passwordText)
        } else {
            context.showToast("Username  / Password tidak boleh kosong!")
        }
    }

    ConstraintLayout(
        modifier = modifier
            .fillMaxSize()
            .navigationBarsPadding()
    ) {
        val (logo, content) = createRefs()

        Card(
            elevation = CardDefaults.cardElevation(10.dp),
            shape = RoundedCornerShape(20.dp),
            modifier = modifier
                .constrainAs(content) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                }
                .padding(start = 24.dp, end = 24.dp)
        ) {
            Column(modifier = modifier.padding(16.dp)) {
                Text(
                    text = "Silahkan Login untuk lanjut kedalam aplikasi",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center,
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp)
                )
                OutlinedTextFieldComp(
                    placeholderText = "Username",
                    query = usernameText,
                    onQueryChange = { usernameText = it },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        }
                    ),
                    modifier = modifier
                )
                OutlinedTextFieldComp(
                    placeholderText = "Password",
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
                    query = passwordText,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            doLogin()
                        }
                    ),
                    onQueryChange = { passwordText = it },
                    modifier = modifier.onFocusEvent { event ->
                        if (event.isFocused) {
                            coroutine.launch {
                                bringIntoViewRequester.bringIntoView()
                            }
                        }
                    }
                )
                Button(
                    onClick = {
                        doLogin()
                    }, modifier = modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth()
                ) {
                    Text(text = "Login")
                }
                Text(
                    text = "Atau",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center,
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                )
                Button(
                    onClick = {
                        viewModel.oneTapSignIn()
                    }, modifier = modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth()
                        .bringIntoViewRequester(bringIntoViewRequester)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.google),
                        contentDescription = "login dengan google",
                        modifier = modifier.size(24.dp)
                    )
                    Text(
                        text = "Login dengan Google",
                        modifier = modifier.padding(start = 8.dp)
                    )
                }
            }
        }

        Card(
            elevation = CardDefaults.cardElevation(12.dp),
            shape = RoundedCornerShape(50.dp),
            modifier = modifier
                .constrainAs(logo) {
                    top.linkTo(content.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(content.top)
                }
                .scale(1.5f)
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = modifier
            )
        }
    }


    fun launch(signInResult: BeginSignInResult) {
        val intent = IntentSenderRequest.Builder(signInResult.pendingIntent.intentSender).build()
        launcher.launch(intent)
    }

    OneTapSignIn(launch = {
        launch(it)
    })

    var currentEmail by remember {
        mutableStateOf("")
    }
    var currentFirebaseToken by remember {
        mutableStateOf("")
    }
    var currentName by remember {
        mutableStateOf("")
    }
    var fcmToken by remember {
        mutableStateOf("")
    }
    var userToken by remember {
        mutableStateOf("")
    }

    viewModel.isError.collectAsStateWithLifecycle().value.let {
        if (it != "")
            ShowSnackbarWithAction(
                snackbarHostState = snackbarHostState,
                errorMsg = it,
                onRetryClick = {
                    if (loginState == LOGIN_ADMIN)
                        viewModel.doLogin(usernameText, passwordText)
                    else if (loginState == LOGIN_GOOGLE)
                        viewModel.doLoginWithGoogle(
                            email = currentEmail,
                            name = currentName,
                            firebaseToken = currentFirebaseToken
                        )
                },
            )
    }

    SignInWithGoogle(
        navigateToHomeScreen = { currentUser ->
            coroutine.launch {
                val firebaseToken = currentUser.getIdToken(true).await()
                currentEmail = currentUser.email ?: ""
                currentName = currentUser.displayName ?: ""
                currentFirebaseToken = firebaseToken.token ?: ""
                loginState = LOGIN_GOOGLE
                viewModel.doLoginWithGoogle(
                    email = currentEmail,
                    name = currentName,
                    firebaseToken = currentFirebaseToken
                )
            }
        }
    )

    GetFCMToken(viewModel = viewModel, onTokenObserved = {
        fcmToken = it
        println("fcmToken = $it")
        LaunchedEffect(userToken != "" && fcmToken != "") {
            println("panggil")
            viewModel.postFCMToken(
                userToken = userToken,
                fcmToken = fcmToken
            )
        }
    })

    if (userToken != "" && fcmToken != "")
        ObservePostFCM(
            viewModel = viewModel,
            onResultSuccess = {
                onLoginSuccess()
            },
            onUnauthorized = {})

    viewModel.isRefreshing.collectAsStateWithLifecycle().value.let {
        if (it)
            Dialog(onDismissRequest = {}) {
                Loading()
            }
    }

    if (loginState != "")
        viewModel.uiState.collectAsStateWithLifecycle().value.let { uiState ->
            when (uiState) {
                is UiState.Loading -> {

                }

                is UiState.Success -> {
                    viewModel.getFCMToken()
                    userToken = uiState.data.data.token
                    viewModel.saveUserData(uiState.data.data)
                }

                is UiState.Unauthorized -> {}
            }
        }

}

@Composable
fun GetFCMToken(viewModel: LoginViewModel, onTokenObserved: @Composable (String) -> Unit) {
    val context = LocalContext.current
    when (val fcmResp = viewModel.fcmResponse) {
        is GoogleAuthResponse.Loading -> {
        }

        is GoogleAuthResponse.Success -> fcmResp.data?.let { fcmToken ->
            onTokenObserved(fcmToken)
        }

        is GoogleAuthResponse.Failure -> LaunchedEffect(Unit) {
            fcmResp.e.localizedMessage?.let { context.showToast(it) }
        }
    }
}

@Composable
fun ObservePostFCM(
    viewModel: LoginViewModel,
    onResultSuccess: @Composable (String) -> Unit,
    onUnauthorized: @Composable () -> Unit
) {
    viewModel.fcmState.collectAsState().value.let { uiState ->
        when (uiState) {

            is UiState.Loading -> {
                Dialog(onDismissRequest = {}) {
                    Loading()
                }
            }

            is UiState.Success -> {
                onResultSuccess(uiState.data.message ?: "FCM Submitted successfully!")
            }

            is UiState.Unauthorized -> {
                onUnauthorized()
            }
        }
    }
}




