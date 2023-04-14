package com.kominfotabalong.simasganteng

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.kominfotabalong.simasganteng.ui.theme.OverlayDark30
import com.kominfotabalong.simasganteng.ui.theme.OverlayLight30
import com.kominfotabalong.simasganteng.ui.theme.SIMASGANTENGTheme
import com.kominfotabalong.simasganteng.util.isLight
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)


        setContent {
            val systemUiController = rememberSystemUiController()
            val isDarkTheme = MaterialTheme.colorScheme.isLight()
            SideEffect {
                systemUiController.setSystemBarsColor(
                    if (isDarkTheme) OverlayDark30 else OverlayLight30,
                    darkIcons = false
                )
            }
            SIMASGANTENGTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SiMasGantengApp()
                }
            }
        }
    }
}
