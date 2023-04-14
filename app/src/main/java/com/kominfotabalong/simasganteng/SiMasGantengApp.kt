package com.kominfotabalong.simasganteng

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kominfotabalong.simasganteng.ui.component.TopBar
import com.kominfotabalong.simasganteng.ui.screen.NavGraphs
import com.kominfotabalong.simasganteng.ui.screen.appCurrentDestinationAsState
import com.kominfotabalong.simasganteng.ui.screen.destinations.*
import com.kominfotabalong.simasganteng.ui.screen.splash.SplashScreen
import com.kominfotabalong.simasganteng.ui.screen.startAppDestination
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.animations.defaults.RootNavGraphDefaultAnimations
import com.ramcosta.composedestinations.animations.rememberAnimatedNavHostEngine
import com.ramcosta.composedestinations.manualcomposablecalls.animatedComposable

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialNavigationApi::class)
@Composable
fun SiMasGantengApp(
    modifier: Modifier = Modifier,
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

    val startRoute = if (isSplashLoaded) checkLoginState() else NavGraphs.root.startRoute

    Scaffold(
        topBar = {
            if (currentDestination == AddLaporanScreenDestination)
                TopBar(
                    destination = currentDestination,
                    onBackClick = { navController.popBackStack() }
                )
        },
        modifier = modifier,
    ) { innerPadding ->
        DestinationsNavHost(
            engine = engine,
            navController = navController,
            startRoute = startRoute,
            navGraph = NavGraphs.root,
            modifier = Modifier.padding(innerPadding)
        ) {
            animatedComposable(SplashScreenDestination) {
                SplashScreen {
                    isSplashLoaded = true
                    destinationsNavigator.navigate(checkLoginState())
                }
            }
        }
    }

}

private val Destination.shouldShowScaffoldElements get() = this is AddLaporanScreenDestination

fun checkLoginState() =
    if (Firebase.auth.currentUser != null) DashboardScreenDestination else LoginScreenDestination