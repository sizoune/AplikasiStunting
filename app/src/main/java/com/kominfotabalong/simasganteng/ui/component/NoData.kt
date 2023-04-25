package com.kominfotabalong.simasganteng.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.kominfotabalong.simasganteng.R

@Composable
fun NoData(
    modifier: Modifier = Modifier,
    emptyDesc: String,
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.no_data))

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxSize()
    ) {
        LottieAnimation(
            composition,
            iterations = LottieConstants.IterateForever,
            modifier = modifier
                .size(180.dp)

        )
        Text(
            text = emptyDesc,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelLarge,
            modifier = modifier.padding(16.dp)
        )
    }
}