package com.kominfotabalong.simasganteng.ui.screen.dashboard

import android.text.TextUtils
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
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
import com.kominfotabalong.simasganteng.BuildConfig
import com.kominfotabalong.simasganteng.MainViewModel
import com.kominfotabalong.simasganteng.R
import com.kominfotabalong.simasganteng.data.model.ArtikelResponse
import com.kominfotabalong.simasganteng.data.model.LoginResponse
import com.kominfotabalong.simasganteng.ui.common.UiState
import com.kominfotabalong.simasganteng.ui.component.Loading
import com.kominfotabalong.simasganteng.ui.component.NoData
import com.kominfotabalong.simasganteng.ui.component.ObserveLoggedUser
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
import java.util.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
@Destination
fun DashboardScreen(
    modifier: Modifier = Modifier.navigationBarsPadding(),
    viewModel: MainViewModel = hiltViewModel(),
    snackbarHostState: SnackbarHostState,
    navigator: DestinationsNavigator
) {
    val rightNow: Calendar = Calendar.getInstance()
    val textBg = if (isSystemInDarkTheme()) Color.DarkGray else Color.LightGray
    val titleColor = if (isSystemInDarkTheme()) Color.Black else Color.White
    var showLogoutDialog by remember {
        mutableStateOf(false)
    }
    var userData by remember {
        mutableStateOf(LoginResponse())
    }
    var doLogout by remember {
        mutableStateOf(false)
    }
    var dataArtikel by remember {
        mutableStateOf(listOf<ArtikelResponse>())
    }
    val (showSnackBar, setShowSnackBar) = remember {
        mutableStateOf(false)
    }
    val context = LocalContext.current


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

    viewModel.isRefreshing.collectAsStateWithLifecycle().value.let { loadingState ->
        isLoading = loadingState
    }

    ObserveLoggedUser(getData = userData.token == "", onUserObserved = {
        userData = it
    }, onError = {
        context.showToast(it)
    })

    WarningDialog(showDialog = showLogoutDialog,
        onDismiss = { dismiss -> showLogoutDialog = dismiss },
        dialogDesc = "Apakah anda yakin ingin keluar dari aplikasi?",
        onOkClick = {
            doLogout = true
        })

    LaunchedEffect(Unit) {
        viewModel.getDaftarArtikel()
    }

    ObserveDataArtikel(viewModel = viewModel, onResultSuccess = {
        dataArtikel = it
    }, onResultError = { errorMsg ->
        ShowSnackbarWithAction(snackbarHostState = snackbarHostState,
            errorMsg = errorMsg,
            showSnackBar = showSnackBar,
            onRetryClick = { viewModel.getDaftarArtikel() },
            onDismiss = { setShowSnackBar(it) })
    }, onUnauthorized = {})

    if (doLogout) navigator.navigate(LogoutHandlerDestination(""))


    ConstraintLayout(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        val (background, content, adminTitle, adminContent, artikelTitle, artikelContent, credit) = createRefs()

        Column(modifier = modifier
            .constrainAs(background) {
                top.linkTo(parent.top)
                width = Dimension.fillToConstraints
            }
            .height(140.dp)
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
                Row(horizontalArrangement = Arrangement.Center, modifier = modifier.weight(1f)) {
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
                            color = titleColor,
                            modifier = modifier
                                .fillMaxWidth()
                                .padding(start = 6.dp)
                        )
                        Text(
                            text = "Selamat datang di aplikasi SI MAS GANTENG",
                            style = MaterialTheme.typography.labelSmall,
                            color = titleColor,
                            modifier = modifier
                                .fillMaxWidth()
                                .padding(start = 6.dp)
                        )
                    }
                }
                IconButton(onClick = {
                    showLogoutDialog = true
                }, modifier = modifier.weight(0.1f)) {
                    Icon(
                        imageVector = Icons.Filled.Logout,
                        contentDescription = "keluar",
                        tint = titleColor
                    )
                }
            }
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
            Text(text = "Admin",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                modifier = modifier
                    .constrainAs(adminTitle) {
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
                    DashboardMenu(iconImage = R.drawable.recap,
                        menuTitle = "Rekapitulasi Pelaporan",
                        menuDesc = "Lihat rekapitulasi pelaporan masuk dan balita terverifikasi",
                        menuOnClick = {
                            navigator.navigate(MapScreenDestination)
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
                        dataArtikel.forEach {
                            ItemArtikel(artikelResp = it, menuOnClick = { clickedArticle ->
                                navigator.navigate(DetailArtikelScreenDestination(clickedArticle))
                            })
                            Divider(
                                color = Color.LightGray,
                                thickness = 1.dp,
                                modifier = modifier.padding(top = 4.dp, bottom = 4.dp)
                            )
                        }
                    }
                }
        }


        Text(text = "SIstem inforMASi Tanggap stunTING KELUarga bahagiA, dikembangkan oleh Dinas Komunikasi dan Informatika Kabupaten Tabalong",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold
            ),
            modifier = modifier
                .constrainAs(credit) {
                    bottom.linkTo(parent.bottom)
                }
                .drawBehind {
                    drawRoundRect(
                        color = textBg, cornerRadius = CornerRadius(20f, 20f)
                    )
                }
                .fillMaxWidth()
                .padding(16.dp)
                .basicMarquee(iterations = Int.MAX_VALUE))
    }

}

@Composable
fun ObserveDataArtikel(
    viewModel: MainViewModel,
    onResultSuccess: (List<ArtikelResponse>) -> Unit,
    onResultError: @Composable (message: String) -> Unit,
    onUnauthorized: @Composable () -> Unit
) {
    viewModel.artikelState.collectAsStateWithLifecycle().value.let { uiState ->
        when (uiState) {

            is UiState.Loading -> {
            }

            is UiState.Success -> {
                uiState.data.data?.let {
                    onResultSuccess(it)
                }
            }

            is UiState.Error -> {
                println("error = ${uiState.errorMessage}")
                onResultError(uiState.errorMessage)
            }

            is UiState.Unauthorized -> {
                onUnauthorized()
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
            tint = MaterialTheme.colorScheme.primary,
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