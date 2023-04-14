package com.kominfotabalong.simasganteng.ui.screen.login

import android.app.Activity.RESULT_OK
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider.getCredential
import com.kominfotabalong.simasganteng.R
import com.kominfotabalong.simasganteng.ui.component.OneTapSignIn
import com.kominfotabalong.simasganteng.ui.component.OutlinedTextFieldComp
import com.kominfotabalong.simasganteng.ui.component.SignInWithGoogle
import com.kominfotabalong.simasganteng.ui.screen.destinations.DashboardScreenDestination
import com.kominfotabalong.simasganteng.util.showToast
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Composable
@Destination
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    val context = LocalContext.current
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
                    onQueryChange = { passwordText = it },
                    modifier = modifier
                )
                Button(
                    onClick = { }, modifier = modifier
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

    SignInWithGoogle(
        navigateToHomeScreen = { signedIn ->
            if (signedIn) {
                navigator.navigate(DashboardScreenDestination)
            }
        }
    )

}



