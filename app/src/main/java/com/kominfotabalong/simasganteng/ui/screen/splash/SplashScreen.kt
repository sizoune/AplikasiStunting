package com.kominfotabalong.simasganteng.ui.screen.splash

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kominfotabalong.simasganteng.R
import com.kominfotabalong.simasganteng.ui.common.UiState
import com.kominfotabalong.simasganteng.ui.component.ObserveLoggedUser
import com.kominfotabalong.simasganteng.ui.screen.login.LoginViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import kotlinx.coroutines.delay

@Composable
@RootNavGraph(start = true)
@Destination
fun SplashScreen(
    modifier: Modifier = Modifier,
    loginViewModel: LoginViewModel,
    gotoDashboard: () -> Unit,
) {
    val scale = remember {
        androidx.compose.animation.core.Animatable(0f)
    }
    var showText by remember {
        mutableStateOf(false)
    }
    var userToken by remember {
        mutableStateOf("")
    }

    // AnimationEffect
    LaunchedEffect(key1 = true) {
        scale.animateTo(
            targetValue = 1.5f,
            animationSpec = tween(
                durationMillis = 800,
                easing = {
                    OvershootInterpolator(4f).getInterpolation(it)
                })
        )
        showText = true
    }

    ObserveLoggedUser(mainViewModel = loginViewModel, onUserObserved = {
        userToken = it.token
        LaunchedEffect(Unit) {
            delay(1000)
            loginViewModel.getUserData(userToken)
        }
    })

    loginViewModel.userRemotelyState.collectAsStateWithLifecycle().value.let { userState ->
        when (userState) {
            is UiState.Loading -> {
            }

            is UiState.Success -> {
                LaunchedEffect(key1 = Unit, block = { gotoDashboard() })
            }

            is UiState.Unauthorized -> {
                LaunchedEffect(key1 = Unit) {
                    loginViewModel.logOut()
                    gotoDashboard()
                }
            }

            is UiState.Error -> {
                LaunchedEffect(key1 = Unit) {
                    loginViewModel.logOut()
                    gotoDashboard()
                }
                println("FailedToObtain User : ${userState.errorMessage}")
            }
        }
    }

    // Image
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.primary)
    ) {
        Card(
            elevation = CardDefaults.cardElevation(10.dp),
            shape = RoundedCornerShape(50.dp),
            modifier = modifier.scale(scale.value),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = modifier.size(80.dp)
            )
        }
        AnimatedVisibility(
            visible = showText,
            enter = slideInHorizontally(),
            exit = slideOutHorizontally()
        ) {
            Text(
                text = stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = modifier.padding(top = 24.dp)
            )
        }
    }
}