package com.kominfotabalong.simasganteng

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.kominfotabalong.simasganteng.ui.NavGraphs
import com.kominfotabalong.simasganteng.ui.appCurrentDestinationAsState
import com.kominfotabalong.simasganteng.ui.component.TopBar
import com.kominfotabalong.simasganteng.ui.destinations.AddLaporanScreenDestination
import com.kominfotabalong.simasganteng.ui.destinations.DashboardScreenDestination
import com.kominfotabalong.simasganteng.ui.destinations.Destination
import com.kominfotabalong.simasganteng.ui.destinations.LoginScreenDestination
import com.kominfotabalong.simasganteng.ui.destinations.LogoutHandlerDestination
import com.kominfotabalong.simasganteng.ui.destinations.MapScreenDestination
import com.kominfotabalong.simasganteng.ui.destinations.SplashScreenDestination
import com.kominfotabalong.simasganteng.ui.screen.laporan.AddLaporanScreen
import com.kominfotabalong.simasganteng.ui.screen.login.LoginScreen
import com.kominfotabalong.simasganteng.ui.screen.login.LoginViewModel
import com.kominfotabalong.simasganteng.ui.screen.splash.SplashScreen
import com.kominfotabalong.simasganteng.ui.startAppDestination
import com.kominfotabalong.simasganteng.util.showToast
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.animations.defaults.RootNavGraphDefaultAnimations
import com.ramcosta.composedestinations.animations.rememberAnimatedNavHostEngine
import com.ramcosta.composedestinations.manualcomposablecalls.animatedComposable
import com.ramcosta.composedestinations.navigation.dependency
import com.ramcosta.composedestinations.scope.resultRecipient

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialNavigationApi::class)
@Composable
fun SiMasGantengApp(
    modifier: Modifier = Modifier,
    mainViewModel: LoginViewModel = hiltViewModel(),
    navController: NavHostController = rememberAnimatedNavController(),
) {
    val currentDestination: Destination = navController.appCurrentDestinationAsState().value
        ?: NavGraphs.root.startAppDestination

    val engine = rememberAnimatedNavHostEngine(
        navHostContentAlignment = Alignment.TopCenter,
        rootDefaultAnimations = RootNavGraphDefaultAnimations.ACCOMPANIST_FADING,
    )
    var isSplashLoaded by rememberSaveable {
        mutableStateOf(false)
    }

    var loggedUser by remember {
        mutableStateOf("")
    }
    var isFinishDoLogin by remember {
        mutableStateOf(false)
    }

    mainViewModel.getLoggedUserData().collectAsState(initial = "").value.let { userData ->
        println("userData = $userData")
        loggedUser = userData
    }

    mainViewModel.isFinishLogin.collectAsState().value.let {
        isFinishDoLogin = it
    }

    val startRoute =
        if (isSplashLoaded) (if (loggedUser.isNotEmpty() && isFinishDoLogin) DashboardScreenDestination else LoginScreenDestination)
        else NavGraphs.root.startRoute

    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            if (currentDestination == AddLaporanScreenDestination || currentDestination == MapScreenDestination)
                TopBar(
                    destination = currentDestination,
                    onBackClick = { navController.popBackStack() }
                )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier,
    ) { innerPadding ->
        DestinationsNavHost(
            engine = engine,
            navController = navController,
            startRoute = startRoute,
            navGraph = NavGraphs.root,
            modifier = Modifier.padding(innerPadding),
            dependenciesContainerBuilder = {
                dependency(snackbarHostState)
                dependency(LogoutHandlerDestination) { mainViewModel }
            }
        ) {
            animatedComposable(SplashScreenDestination) {
                SplashScreen {
                    isSplashLoaded = true
                    mainViewModel.setLoginStatus(loggedUser.isNotEmpty())
                }
            }
            animatedComposable(LoginScreenDestination) {
                LoginScreen(snackbarHostState = snackbarHostState, onLoginSuccess = {
                    mainViewModel.setLoginStatus(true)
                })
            }
            animatedComposable(AddLaporanScreenDestination) {
                AddLaporanScreen(
                    navigator = destinationsNavigator,
                    snackbarHostState = snackbarHostState,
                    resultRecipient = resultRecipient(),
                    onLocPermissionDeniedForever = {
                        context.showToast("Izin lokasi tidak diberikan!, silahkan mengizinkan aplikasi untuk menggunakan lokasi anda!")
                        gotoSetting(context)
                    },
                )
            }
        }
    }

}

private fun gotoSetting(context: Context) {
    context.startActivity(Intent().apply {
        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        data = Uri.fromParts("package", context.packageName, null)
    })
}

