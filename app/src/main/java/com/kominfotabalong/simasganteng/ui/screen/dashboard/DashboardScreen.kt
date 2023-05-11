package com.kominfotabalong.simasganteng.ui.screen.dashboard

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import android.widget.TextView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.kominfotabalong.simasganteng.BuildConfig
import com.kominfotabalong.simasganteng.MainViewModel
import com.kominfotabalong.simasganteng.R
import com.kominfotabalong.simasganteng.data.model.ArtikelResponse
import com.kominfotabalong.simasganteng.data.model.LoginResponse
import com.kominfotabalong.simasganteng.data.model.StatistikResponse
import com.kominfotabalong.simasganteng.ui.common.UiState
import com.kominfotabalong.simasganteng.ui.component.Loading
import com.kominfotabalong.simasganteng.ui.component.NoData
import com.kominfotabalong.simasganteng.ui.component.ShowSnackbarWithAction
import com.kominfotabalong.simasganteng.ui.component.WarningDialog
import com.kominfotabalong.simasganteng.ui.destinations.AddLaporanScreenDestination
import com.kominfotabalong.simasganteng.ui.destinations.DetailArtikelScreenDestination
import com.kominfotabalong.simasganteng.ui.destinations.ListLaporanMasukScreenDestination
import com.kominfotabalong.simasganteng.ui.destinations.ListLaporanRejectedScreenDestination
import com.kominfotabalong.simasganteng.ui.destinations.ListLaporanVerifiedScreenDestination
import com.kominfotabalong.simasganteng.ui.destinations.LogoutHandlerDestination
import com.kominfotabalong.simasganteng.ui.destinations.MapScreenDestination
import com.kominfotabalong.simasganteng.util.showToast
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import java.time.LocalDateTime
import java.util.Calendar


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(
    ExperimentalFoundationApi::class, ExperimentalPermissionsApi::class,
    ExperimentalLayoutApi::class
)
@Composable
@Destination
fun DashboardScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = hiltViewModel(),
    snackbarHostState: SnackbarHostState,
    userData: LoginResponse,
    navigator: DestinationsNavigator
) {
    val rightNow: Calendar = Calendar.getInstance()
    val textBg = if (isSystemInDarkTheme()) Color.DarkGray else Color.LightGray


    var doLogout by remember {
        mutableStateOf(false)
    }
    var showWarningDialog by remember {
        mutableStateOf(false)
    }
    var warningMsg by remember {
        mutableStateOf("")
    }
    var dataArtikel by remember {
        mutableStateOf(listOf<ArtikelResponse>())
    }
    val dataStatistik = remember {
        mutableStateListOf(StatistikResponse())
    }
    val context = LocalContext.current

    var isPostNotifAccessGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val postNotifPermissionsState = rememberPermissionState(
        Manifest.permission.POST_NOTIFICATIONS,
    )
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) isPostNotifAccessGranted = true
            else {
                val neverAskAgain = !postNotifPermissionsState.status.shouldShowRationale
                if (neverAskAgain) {
                    context.showToast("Izin POST Notifikasi dibutuhkan !")
                    context.startActivity(Intent().apply {
                        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        data = Uri.fromParts("package", context.packageName, null)
                    })
                } else {
                    showWarningDialog = true
                    warningMsg =
                        "Akses Push Notifikasi dibutuhkan agar kamu bisa mendapatkan notifikasi!"
                }
            }
        }
    )

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !isPostNotifAccessGranted) {
        SideEffect {
            permissionLauncher.launch(
                Manifest.permission.POST_NOTIFICATIONS
            )
        }
    }

    fun writeWelcomeUser(): String {
        return when (rightNow.get(Calendar.HOUR_OF_DAY)) {
            in 6..11 -> {
                "Selamat Pagi ${userData.user.name}"
            }

            in 12..15 -> {
                "Selamat Siang ${userData.user.name}"
            }

            in 16..18 -> {
                "Selamat Sore  ${userData.user.name}"
            }

            else -> {
                "Selamat Malam  ${userData.user.name}"
            }
        }
    }

    var isLoading by rememberSaveable {
        mutableStateOf(false)
    }


    WarningDialog(showDialog = showWarningDialog,
        onDismiss = { dismiss ->
            if (doLogout) doLogout = false
            showWarningDialog = dismiss
        },
        dialogDesc = warningMsg,
        onOkClick = {
            println("logoutstate = $doLogout")
            if (doLogout) navigator.navigate(LogoutHandlerDestination())
            else postNotifPermissionsState.launchPermissionRequest()
        })

    LaunchedEffect(Unit) {
        viewModel.getDaftarArtikel()
        if (userData.user.role.lowercase() != "public")
            viewModel.getDataStatistik(userData.token, "2023")
    }

    ObserveDataArtikel(viewModel = viewModel, onResultSuccess = {
        isLoading = false
        dataArtikel = it
    }, onError = { errorMsg ->
        isLoading = false
        ShowSnackbarWithAction(
            snackbarHostState = snackbarHostState,
            errorMsg = errorMsg,
            onRetryClick = {
                viewModel.getDaftarArtikel()
            },
        )
    }, onUnauthorized = {}, onLoading = {
        isLoading = true
    })

    if (userData.user.role.lowercase() != "public")
        ObserveDataStatistikGizi(
            viewModel = viewModel,
            onResultSuccess = {
                isLoading = false
                if (dataStatistik.isNotEmpty()) dataStatistik.clear()
                dataStatistik.addAll(it)
            },
            onLoading = { isLoading = true },
            onError = { errorMsg ->
                isLoading = false
                ShowSnackbarWithAction(
                    snackbarHostState = snackbarHostState,
                    errorMsg = errorMsg,
                    onRetryClick = {
                        viewModel.getDataStatistik(
                            userData.token,
                            LocalDateTime.now().year.toString()
                        )
                    },
                )
            },
            onUnauthorized = {
                navigator.navigate(LogoutHandlerDestination("Sesi login anda telah berakhir!"))
            }
        )

    ConstraintLayout(
        modifier = modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        val (background, content, statistikTitle, statistikContent, adminTitle, adminContent, artikelTitle, artikelContent, credit) = createRefs()

        Column(modifier = modifier
            .constrainAs(background) {
                top.linkTo(parent.top)
                width = Dimension.fillToConstraints
            }
            .height(160.dp)
            .background(
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
            )) {
            Row(
                modifier = modifier
                    .statusBarsPadding()
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = modifier.weight(1f)
                ) {
                    Card(
                        elevation = CardDefaults.cardElevation(10.dp),
                        shape = RoundedCornerShape(50.dp),
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logo),
                            contentDescription = "Logo",
                        )
                    }
                    Column(modifier = modifier.padding(start = 8.dp)) {
                        Text(
                            text = writeWelcomeUser(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = modifier
                                .fillMaxWidth()
                                .padding(start = 6.dp)
                        )
                        Text(
                            text = "Selamat datang di aplikasi SI MAS GANTENG",
                            style = MaterialTheme.typography.labelSmall,
                            modifier = modifier
                                .fillMaxWidth()
                                .padding(start = 6.dp)
                        )
                    }
                }
                IconButton(onClick = {
                    showWarningDialog = true
                    doLogout = true
                    warningMsg = "Apakah anda yakin ingin keluar dari aplikasi ?"

                }, modifier = modifier.weight(0.1f)) {
                    Icon(
                        imageVector = Icons.Filled.Logout,
                        contentDescription = "keluar",
                    )
                }
            }
            Text(
                text = "SIstem inforMASi Tanggap stunTING KELUarga bahagiA, dikembangkan oleh Dinas Komunikasi dan Informatika Kabupaten Tabalong",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                modifier = modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .basicMarquee(iterations = Int.MAX_VALUE)
            )
        }

        Card(modifier = modifier
            .constrainAs(content) {
                top.linkTo(background.bottom)
                bottom.linkTo(background.bottom)
                width = Dimension.fillToConstraints
            }
            .padding(start = 16.dp, end = 16.dp)
            .clickable {
                navigator.navigate(AddLaporanScreenDestination)
            }, elevation = CardDefaults.cardElevation(10.dp), shape = RoundedCornerShape(20.dp)
        ) {
            DashboardMenu(iconImage = R.drawable.report,
                menuTitle = "Pelaporan",
                menuDesc = "Tambah laporan anak yang tersuspeksi Stunting",
                menuOnClick = { navigator.navigate(AddLaporanScreenDestination) })
        }

        if (userData.user.role.lowercase() != "public") {
            if (isLoading) Loading(modifier = modifier
                .constrainAs(statistikContent) {
                    top.linkTo(content.bottom)
                }
                .padding(16.dp))
            else {
                if (dataStatistik.isNotEmpty()) {
                    Text(text = "Statistik Gizi Tahun ${LocalDateTime.now().year}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                        modifier = modifier
                            .constrainAs(statistikTitle) {
                                top.linkTo(content.bottom)
                            }
                            .padding(start = 16.dp, top = 24.dp, bottom = 16.dp))
                    FlowRow(
                        modifier = modifier
                            .constrainAs(statistikContent) {
                                top.linkTo(statistikTitle.bottom)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                                width = Dimension.fillToConstraints
                            }
                            .padding(start = 16.dp, end = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        maxItemsInEachRow = 2
                    ) {
                        dataStatistik.forEach {
                            ItemStatistik(dataStatistik = it)
                        }
                    }
                }


            }

            Text(text = "Admin",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                modifier = modifier
                    .constrainAs(adminTitle) {
                        if (!isLoading && dataStatistik.isNotEmpty())
                            top.linkTo(statistikContent.bottom)
                        else
                            if (isLoading)
                                top.linkTo(statistikContent.bottom)
                            else
                                top.linkTo(content.bottom)
                    }
                    .padding(start = 16.dp, top = 24.dp, bottom = 16.dp))

            // Admin Area
            Card(modifier = modifier
                .constrainAs(adminContent) {
                    top.linkTo(adminTitle.bottom)
                    width = Dimension.fillToConstraints
                }
                .padding(start = 16.dp, end = 16.dp),
                elevation = CardDefaults.cardElevation(10.dp),
                shape = RoundedCornerShape(20.dp)) {
                Column {
                    DashboardMenu(iconImage = R.drawable.in_report,
                        menuTitle = "Laporan Masuk",
                        menuDesc = "Lihat laporan anak terindikasi stunting dari masyarakat",
                        menuOnClick = {
                            navigator.navigate(ListLaporanMasukScreenDestination)
                        })
                    Spacer(
                        modifier = modifier
                            .height(1.dp)
                            .background(color = Color.LightGray)
                            .fillMaxWidth()
                    )
                    DashboardMenu(iconImage = R.drawable.verif_report,
                        menuTitle = "Laporan Terverifikasi",
                        menuDesc = "Lihat balita yang telah diverifikasi petugas kesehatan",
                        menuOnClick = {
                            navigator.navigate(ListLaporanVerifiedScreenDestination)
                        })
                    Spacer(
                        modifier = modifier
                            .height(1.dp)
                            .background(color = Color.LightGray)
                            .fillMaxWidth()
                    )
                    DashboardMenu(iconImage = R.drawable.rejected,
                        menuTitle = "Laporan Ditolak",
                        menuDesc = "Lihat laporan yang ditolak",
                        menuOnClick = {
                            navigator.navigate(ListLaporanRejectedScreenDestination)
                        })
                    Spacer(
                        modifier = modifier
                            .height(1.dp)
                            .background(color = Color.LightGray)
                            .fillMaxWidth()
                    )
                    DashboardMenu(iconImage = R.drawable.sebaran,
                        menuTitle = "Peta Sebaran",
                        menuDesc = "Lihat Peta Sebaran Stunting",
                        menuOnClick = {
                            navigator.navigate(MapScreenDestination)
                        })

                }
            }
        }

        Text(text = "Artikel dan Informasi",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
            modifier = modifier
                .constrainAs(artikelTitle) {
                    top.linkTo(if (userData.user.role.lowercase() != "public") adminContent.bottom else content.bottom)
                }
                .padding(start = 16.dp, top = 24.dp, bottom = 16.dp))

        if (isLoading) Loading(modifier = modifier
            .constrainAs(artikelContent) {
                top.linkTo(artikelTitle.bottom)
            }
            .padding(16.dp))
        else {
            if (dataArtikel.isEmpty())
                NoData(emptyDesc = "tidak ada artikel saat ini!")
            else
                Card(modifier = modifier
                    .constrainAs(artikelContent) {
                        top.linkTo(artikelTitle.bottom)
                        width = Dimension.fillToConstraints

                    }
                    .padding(start = 16.dp, end = 16.dp),
                    elevation = CardDefaults.cardElevation(10.dp),
                    shape = RoundedCornerShape(20.dp)) {
                    Column {
                        dataArtikel.forEachIndexed { index, data ->
                            ItemArtikel(artikelResp = data, menuOnClick = { clickedArticle ->
                                navigator.navigate(DetailArtikelScreenDestination(clickedArticle))
                            })
                            if (index + 1 < dataArtikel.size)
                                Divider(
                                    color = Color.LightGray,
                                    thickness = 1.dp,
                                    modifier = modifier.padding(top = 4.dp, bottom = 4.dp)
                                )
                        }
                    }
                }
        }

        Spacer(modifier = modifier
            .size(20.dp)
            .constrainAs(credit) {
                top.linkTo(artikelContent.bottom)
            })
    }

}

@Composable
fun ObserveDataArtikel(
    viewModel: MainViewModel,
    onResultSuccess: (List<ArtikelResponse>) -> Unit,
    onLoading: () -> Unit,
    onError: @Composable (String) -> Unit,
    onUnauthorized: @Composable () -> Unit
) {
    viewModel.artikelState.collectAsStateWithLifecycle().value.let { uiState ->
        when (uiState) {

            is UiState.Loading -> {
                onLoading()
            }

            is UiState.Success -> {
                uiState.data.data?.let {
                    onResultSuccess(it)
                }
            }

            is UiState.Unauthorized -> {
                onUnauthorized()
            }

            is UiState.Error -> {
                onError(uiState.errorMessage)
            }
        }
    }
}

@Composable
fun ObserveDataStatistikGizi(
    viewModel: MainViewModel,
    onResultSuccess: (List<StatistikResponse>) -> Unit,
    onLoading: () -> Unit,
    onError: @Composable (String) -> Unit,
    onUnauthorized: @Composable () -> Unit
) {
    viewModel.statistikState.collectAsStateWithLifecycle().value.let { uiState ->
        when (uiState) {

            is UiState.Loading -> {
                onLoading()
            }

            is UiState.Success -> {
                uiState.data.data?.let {
                    onResultSuccess(it)
                }
            }

            is UiState.Unauthorized -> {
                onUnauthorized()
            }

            is UiState.Error -> {
                onError(uiState.errorMessage)
            }
        }
    }
}

@Composable
fun DashboardMenu(
    modifier: Modifier = Modifier,
    @DrawableRes iconImage: Int,
    menuTitle: String,
    menuDesc: String,
    menuOnClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .padding(12.dp)
            .clickable { menuOnClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Icon(
            painter = painterResource(iconImage),
            contentDescription = menuTitle,
            modifier = modifier
                .size(32.dp)
                .weight(0.2f)
        )
        Column(modifier = modifier.weight(1f)) {
            Text(
                text = menuTitle,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.ExtraBold),
                modifier = modifier
            )
            Text(
                text = menuDesc,
                style = MaterialTheme.typography.labelSmall,
                modifier = modifier.paddingFromBaseline(16.dp)
            )
        }
        IconButton(
            onClick = menuOnClick, modifier = modifier.weight(0.2f)
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowForwardIos,
                contentDescription = menuTitle,
            )
        }
    }
}

@Composable
fun ItemStatistik(
    modifier: Modifier = Modifier,
    dataStatistik: StatistikResponse,
) {
    Card(
        modifier = modifier.fillMaxWidth(0.48f),
        elevation = CardDefaults.cardElevation(10.dp),
        shape = RoundedCornerShape(20.dp)
    )
    {
        Row(
            modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Column(modifier = modifier.weight(1f)) {
                Text(
                    text = dataStatistik.status,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold)
                )
                Text(
                    text = dataStatistik.jumlah.toString(),
                    style = MaterialTheme.typography.labelSmall
                )
            }
            Icon(
                painter = painterResource(R.drawable.stats),
                contentDescription = "",
                modifier = modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun ItemArtikel(
    modifier: Modifier = Modifier,
    artikelResp: ArtikelResponse,
    menuOnClick: (ArtikelResponse) -> Unit,
) {
    Row(
        modifier = modifier
            .padding(12.dp)
            .clickable { menuOnClick(artikelResp) },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        val textColor = if (isSystemInDarkTheme()) R.color.white else R.color.black
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data("${BuildConfig.IMAGE_URL}artikel/${artikelResp.gambar}")
                .crossfade(true)
                .build(),
            placeholder = painterResource(R.drawable.img_placeholder),
            error = painterResource(R.drawable.img_placeholder),
            contentDescription = "Artikel dan informasi",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(4.dp))
        )
        Column(
            modifier = modifier
                .weight(1f)
                .padding(8.dp)
        ) {
            Text(
                text = artikelResp.judul,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.ExtraBold),
                modifier = modifier
            )
            AndroidView(factory = { context ->
                TextView(context).apply {
                    text = HtmlCompat.fromHtml(
                        artikelResp.isi, HtmlCompat.FROM_HTML_MODE_LEGACY
                    )
                    setTextColor(
                        ContextCompat.getColor(
                            context, textColor
                        )
                    )
                    maxLines = 2
                    ellipsize = TextUtils.TruncateAt.END
                    textSize = 11f
                }
            }, modifier = modifier.paddingFromBaseline(6.dp))
        }
    }
}