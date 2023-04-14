package com.kominfotabalong.simasganteng.util

import android.content.Context
import android.widget.Toast
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.luminance

@Composable
fun ColorScheme.isLight() = this.background.luminance() > 0.5

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}