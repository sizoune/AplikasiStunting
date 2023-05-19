package com.kominfotabalong.simasganteng

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.gson.Gson
import com.kominfotabalong.simasganteng.data.model.Kecamatan
import com.kominfotabalong.simasganteng.data.model.LoginResponse
import com.kominfotabalong.simasganteng.data.model.PuskesmasResponse
import com.kominfotabalong.simasganteng.ui.NavGraphs
import com.kominfotabalong.simasganteng.ui.appCurrentDestinationAsState
import com.kominfotabalong.simasganteng.ui.common.UiState
import com.kominfotabalong.simasganteng.ui.component.ObserveLoggedUser
import com.kominfotabalong.simasganteng.ui.component.TopBar
import com.kominfotabalong.simasganteng.ui.destinations.AddLaporanScreenDestination
import com.kominfotabalong.simasganteng.ui.destinations.DashboardScreenDestination
import com.kominfotabalong.simasganteng.ui.destinations.Destination
import com.kominfotabalong.simasganteng.ui.destinations.DetailArtikelScreenDestination
import com.kominfotabalong.simasganteng.ui.destinations.ListLaporanVerifiedScreenDestination
import com.kominfotabalong.simasganteng.ui.destinations.LoginScreenDestination
import com.kominfotabalong.simasganteng.ui.destinations.LogoutHandlerDestination
import com.kominfotabalong.simasganteng.ui.destinations.PengukuranInputDestination
import com.kominfotabalong.simasganteng.ui.destinations.PetugasScreenDestination
import com.kominfotabalong.simasganteng.ui.destinations.SplashScreenDestination
import com.kominfotabalong.simasganteng.ui.screen.dashboard.DashboardScreen
import com.kominfotabalong.simasganteng.ui.screen.laporan.AddLaporanScreen
import com.kominfotabalong.simasganteng.ui.screen.laporan.LaporanViewModel
import com.kominfotabalong.simasganteng.ui.screen.login.LoginScreen
import com.kominfotabalong.simasganteng.ui.screen.login.LoginViewModel
import com.kominfotabalong.simasganteng.ui.screen.pengukuran.PengukuranViewModel
import com.kominfotabalong.simasganteng.ui.screen.petugas.PetugasScreen
import com.kominfotabalong.simasganteng.ui.screen.splash.SplashScreen
import com.kominfotabalong.simasganteng.ui.startAppDestination
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.animations.defaults.RootNavGraphDefaultAnimations
import com.ramcosta.composedestinations.animations.rememberAnimatedNavHostEngine
import com.ramcosta.composedestinations.manualcomposablecalls.animatedComposable
import com.ramcosta.composedestinations.navigation.dependency
import timber.log.Timber

@OptIn(
    ExperimentalAnimationApi::class, ExperimentalMaterialNavigationApi::class
)
@Composable
fun SiMasGantengApp(
    modifier: Modifier = Modifier,
    loginViewModel: LoginViewModel = hiltViewModel(),
    mainViewModel: MainViewModel = hiltViewModel(),
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
        mutableStateOf(LoginResponse())
    }
    val isFinishDoLogin by loginViewModel.isFinishLogin.collectAsStateWithLifecycle()

    var getDataKecamatanRemotely by remember {
        mutableStateOf(false)
    }
    var getDataPuskesRemotely by remember {
        mutableStateOf(false)
    }

    var dataKecamatan by remember {
        mutableStateOf(listOf<Kecamatan>())
    }
    var dataPuskesmas by remember {
        mutableStateOf(listOf<PuskesmasResponse>())
    }

    ObserveLoggedUser(mainViewModel = loginViewModel, onUserObserved = {
        LaunchedEffect(key1 = it, block = {
            Timber.tag("loggedUser").d(it.toString())
            loggedUser = it
        })
    })

    val startRoute =
        if (isSplashLoaded) (
                if (loggedUser.token.isNotEmpty() && isFinishDoLogin)
                    DashboardScreenDestination
                else
                    LoginScreenDestination)
        else
            NavGraphs.root.startRoute

    val snackbarHostState = remember { SnackbarHostState() }
    val laporanViewModel = hiltViewModel<LaporanViewModel>()

    ObserveKecamatanLocally(mainViewModel = mainViewModel) {
        dataKecamatan = it
        getDataKecamatanRemotely = dataKecamatan.isEmpty()
    }

    ObservePuskesmasLocally(mainViewModel = mainViewModel) {
        dataPuskesmas = it
        getDataPuskesRemotely = dataPuskesmas.isEmpty()
    }

    if (getDataKecamatanRemotely && loggedUser.token.isNotEmpty()) {
        println("observeData")
        mainViewModel.getTabalongDistricts(loggedUser.token)
        ObserveDataTabalongRemotely(viewModel = mainViewModel) {
            dataKecamatan = it
            mainViewModel.saveDataToLocal(it)
        }
    }

    if (getDataPuskesRemotely && loggedUser.token.isNotEmpty()) {
        println("observeDataPuskes")
        mainViewModel.getDaftarPuskes(loggedUser.token)
        ObserveDataPuskesmasRemotely(viewModel = mainViewModel) {
            dataPuskesmas = it
            mainViewModel.saveDataPuskesToLocal(it)
        }
    }

    Scaffold(
        topBar = {
            if (currentDestination != SplashScreenDestination
                && currentDestination != DashboardScreenDestination
                && currentDestination != LoginScreenDestination
                && currentDestination != DetailArtikelScreenDestination
                && currentDestination != PengukuranInputDestination
                && currentDestination != LogoutHandlerDestination
            )
                TopBar(
                    destination = currentDestination,
                    onBackClick = {
                        navController.popBackStack()
                        if (currentDestination == ListLaporanVerifiedScreenDestination)
                            laporanViewModel.setDoCariBalita("")
                    },
                    onSearchClick = { searchText ->
                        laporanViewModel.setDoCariBalita(searchText)
                    }
                )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = WindowInsets(top = 0.dp),
        modifier = modifier,
    ) { innerPadding ->
        DestinationsNavHost(
            engine = engine,
            navController = navController,
            startRoute = startRoute,
            navGraph = NavGraphs.root,
            modifier = Modifier
                .padding(innerPadding)
                .navigationBarsPadding(),
            dependenciesContainerBuilder = {
                dependency(snackbarHostState)
                dependency(Gson())
                dependency(loggedUser)
                dependency(dataKecamatan)
                dependency(LogoutHandlerDestination) { loginViewModel }
                dependency(DashboardScreenDestination) { loginViewModel }
                dependency(hiltViewModel<PengukuranViewModel>())
                dependency(laporanViewModel)
                dependency(PetugasScreenDestination){
                    dataPuskesmas
                }
            }
        ) {
            animatedComposable(SplashScreenDestination) {
                SplashScreen(
                    loginViewModel = loginViewModel,
                ) {
                    isSplashLoaded = true
                    loginViewModel.setLoginStatus(loggedUser.token.isNotEmpty())
                }
            }
            animatedComposable(LoginScreenDestination) {
                LoginScreen(
                    snackbarHostState = snackbarHostState,
                    onLoginSuccess = {
                        loginViewModel.setLoginStatus(true)
                    })
            }
            animatedComposable(DashboardScreenDestination) {
                DashboardScreen(
                    loginViewModel = loginViewModel,
                    snackbarHostState = snackbarHostState,
                    userData = loggedUser,
                    navigator = destinationsNavigator
                )
            }
            animatedComposable(AddLaporanScreenDestination) {
                AddLaporanScreen(
                    navigator = destinationsNavigator,
                    snackbarHostState = snackbarHostState,
                    dataKecamatan = dataKecamatan,
                    dataPuskesmas = dataPuskesmas,
                    userData = loggedUser,
                )
            }
        }
    }

}

@Composable
fun ObserveDataTabalongRemotely(
    viewModel: MainViewModel,
    onResultSuccess: (List<Kecamatan>) -> Unit,
) {
    viewModel.kecamatanState.collectAsStateWithLifecycle().value.let { uiState ->
        when (uiState) {

            is UiState.Loading -> {
            }

            is UiState.Success -> {
                uiState.data.data?.let {
                    onResultSuccess(it)
                }
            }

            is UiState.Unauthorized -> {

            }

            is UiState.Error -> {
                println("FailedToObtainData Tabalong : ${uiState.errorMessage}")
            }
        }
    }
}

@Composable
fun ObserveKecamatanLocally(
    mainViewModel: MainViewModel,
    onKecamatanObserved: (data: List<Kecamatan>) -> Unit,
) {

    LaunchedEffect(Unit) {
        mainViewModel.getDataKecamatanInLocal()
    }

    mainViewModel.kecState.collectAsStateWithLifecycle().value.let {
        onKecamatanObserved(it)
    }

}

@Composable
fun ObserveDataPuskesmasRemotely(
    viewModel: MainViewModel,
    onResultSuccess: (List<PuskesmasResponse>) -> Unit,
) {
    viewModel.pkmState.collectAsStateWithLifecycle().value.let { uiState ->
        when (uiState) {

            is UiState.Loading -> {
            }

            is UiState.Success -> {
                uiState.data.data?.let {
                    onResultSuccess(it)
                }
            }

            is UiState.Unauthorized -> {

            }

            is UiState.Error -> {
                println("FailedToObtainData Puskes : ${uiState.errorMessage}")
            }
        }
    }
}

@Composable
fun ObservePuskesmasLocally(
    mainViewModel: MainViewModel,
    onKecamatanObserved: (data: List<PuskesmasResponse>) -> Unit,
) {

    LaunchedEffect(Unit) {
        mainViewModel.getDataPuskesInLocal()
    }

    mainViewModel.puskesState.collectAsStateWithLifecycle().value.let {
        onKecamatanObserved(it)
    }

}

