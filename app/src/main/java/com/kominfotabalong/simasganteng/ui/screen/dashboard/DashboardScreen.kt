package com.kominfotabalong.simasganteng.ui.screen.dashboard

import androidx.annotation.DrawableRes
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import com.kominfotabalong.simasganteng.R
import com.kominfotabalong.simasganteng.ui.component.GoogleSignOut
import com.kominfotabalong.simasganteng.ui.component.WarningDialog
import com.kominfotabalong.simasganteng.ui.screen.destinations.AddLaporanScreenDestination
import com.kominfotabalong.simasganteng.ui.screen.destinations.LoginScreenDestination
import com.kominfotabalong.simasganteng.ui.screen.login.LoginViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import java.util.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
@Destination
fun DashboardScreen(
    modifier: Modifier = Modifier,
    loginViewModel: LoginViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    val rightNow: Calendar = Calendar.getInstance()
    val textBg = if (isSystemInDarkTheme()) Color.DarkGray else Color.LightGray
    val titleColor = if (isSystemInDarkTheme()) Color.Black else Color.White
    var showLogoutDialog by remember {
        mutableStateOf(false)
    }

    fun writeWelcomeUser(): String {
        return when (rightNow.get(Calendar.HOUR_OF_DAY)) {
            in 6..11 -> {
                "Selamat Pagi "
            }

            in 12..15 -> {
                "Selamat Siang "
            }

            in 16..18 -> {
                "Selamat Sore "
            }

            else -> {
                "Selamat Malam"
            }
        }
    }


    WarningDialog(
        showDialog = showLogoutDialog,
        onDismiss = { dismiss -> showLogoutDialog = dismiss },
        dialogDesc = "Apakah anda yakin ingin keluar dari aplikasi?",
        onOkClick = {
            loginViewModel.logOut()
        })


    ConstraintLayout(modifier = modifier.fillMaxSize()) {
        val (background, content, adminTitle, adminContent, credit) = createRefs()

        Column(modifier = modifier
            .constrainAs(background) {
                top.linkTo(parent.top)
                width = Dimension.fillToConstraints
            }
            .height(120.dp)
            .background(
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
            )) {
            Row(
                modifier = modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp),
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

        Card(
            modifier = modifier
                .constrainAs(content) {
                    top.linkTo(background.bottom)
                    bottom.linkTo(background.bottom)
                    width = Dimension.fillToConstraints
                }
                .padding(start = 16.dp, end = 16.dp)
                .clickable {
                    navigator.navigate(AddLaporanScreenDestination)
                },
            elevation = CardDefaults.cardElevation(10.dp),
            shape = RoundedCornerShape(20.dp)
        )
        {
            DashboardMenu(
                iconImage = R.drawable.report,
                menuTitle = "Pelaporan",
                menuDesc = "Tambah laporan anak yang tersuspeksi Stunting",
                menuOnClick = { navigator.navigate(AddLaporanScreenDestination) }
            )
        }

        Text(
            text = "Admin",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
            modifier = modifier
                .constrainAs(adminTitle) {
                    top.linkTo(content.bottom)
                }
                .padding(start = 16.dp, top = 24.dp, bottom = 16.dp)
        )

        // Admin Area
        Card(
            modifier = modifier
                .constrainAs(adminContent) {
                    top.linkTo(adminTitle.bottom)
                    width = Dimension.fillToConstraints
                }
                .padding(start = 16.dp, end = 16.dp),
            elevation = CardDefaults.cardElevation(10.dp),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column {
                DashboardMenu(
                    iconImage = R.drawable.in_report,
                    menuTitle = "Laporan Masuk",
                    menuDesc = "Lihat laporan anak terindikasi stunting dari masyarakat",
                    menuOnClick = {}
                )
                Spacer(
                    modifier = modifier
                        .height(1.dp)
                        .background(color = Color.LightGray)
                        .fillMaxWidth()
                )
                DashboardMenu(
                    iconImage = R.drawable.verif_report,
                    menuTitle = "Laporan Terverifikasi",
                    menuDesc = "Lihat balita yang telah diverifikasi petugas kesehatan",
                    menuOnClick = {}
                )
                Spacer(
                    modifier = modifier
                        .height(1.dp)
                        .background(color = Color.LightGray)
                        .fillMaxWidth()
                )
                DashboardMenu(
                    iconImage = R.drawable.recap,
                    menuTitle = "Rekapitulasi Pelaporan",
                    menuDesc = "Lihat rekapitulasi pelaporan masuk dan balita terverifikasi",
                    menuOnClick = {}
                )
            }
        }

        Text(
            text = "SIstem inforMASi Tanggap stunTING KELUarga bahagiA, dikembangkan oleh Dinas Komunikasi dan Informatika Kabupaten Tabalong",
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
                .basicMarquee(iterations = Int.MAX_VALUE)
        )
    }

    GoogleSignOut(navigateToLoginScreen = { signedOut ->
        if (signedOut) {
            navigator.navigate(LoginScreenDestination)
        }
    })

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
            onClick = menuOnClick,
            modifier = modifier.weight(0.2f)
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowForwardIos,
                contentDescription = menuTitle,
            )
        }
    }
}