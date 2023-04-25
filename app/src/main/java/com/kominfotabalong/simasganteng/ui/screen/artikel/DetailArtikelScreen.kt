package com.kominfotabalong.simasganteng.ui.screen.artikel

import android.widget.TextView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.kominfotabalong.simasganteng.BuildConfig
import com.kominfotabalong.simasganteng.R
import com.kominfotabalong.simasganteng.data.model.ArtikelResponse
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Composable
@Destination
fun DetailArtikelScreen(
    modifier: Modifier = Modifier.navigationBarsPadding(),
    artikelResp: ArtikelResponse,
    navigator: DestinationsNavigator
) {
    ConstraintLayout(
        modifier = modifier
            .fillMaxSize()
            .navigationBarsPadding()
    ) {
        val (topSection, bottomSection, guideline, titleSection) = createRefs()
        val isDarkTheme = isSystemInDarkTheme()

        Box(modifier = modifier
            .height(250.dp)
            .constrainAs(topSection) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            }) {
            val backBtnBG =
                if (isSystemInDarkTheme()) colorResource(id = R.color.overlay_dark_50) else colorResource(
                    id = R.color.overlay_light_50
                )
            AsyncImage(model = ImageRequest.Builder(LocalContext.current)
                .data("${BuildConfig.IMAGE_URL}artikel/${artikelResp.gambar}")
                .crossfade(true).build(),
                placeholder = painterResource(R.drawable.img_placeholder),
                contentDescription = "gambar",
                error = painterResource(R.drawable.img_placeholder),
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .fillMaxSize()
                    .drawWithCache {
                        val gradient = Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black),
                            startY = size.height / 5,
                            endY = size.height
                        )
                        onDrawWithContent {
                            drawContent()
                            drawRect(gradient, blendMode = BlendMode.Multiply)
                        }
                    })
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Kembali",
                modifier = Modifier
                    .statusBarsPadding()
                    .drawBehind {
                        drawCircle(color = backBtnBG, radius = 40f)
                    }.padding(16.dp)
                    .clickable { navigator.navigateUp() },
            )
        }
        Spacer(modifier = modifier
            .size(32.dp)
            .constrainAs(guideline) {
                bottom.linkTo(topSection.bottom)
            })

        Card(elevation = CardDefaults.cardElevation(8.dp),
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            modifier = modifier.constrainAs(bottomSection) {
                top.linkTo(guideline.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }) {
            Column(
                modifier = modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Deskripsi", style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold
                    ), modifier = modifier
                )
                AndroidView(factory = { context ->
                    TextView(context).apply {
                        text = HtmlCompat.fromHtml(
                            artikelResp.isi, HtmlCompat.FROM_HTML_MODE_LEGACY
                        )
                        setTextColor(
                            ContextCompat.getColor(
                                context, if (isDarkTheme) R.color.white else R.color.black
                            )
                        )
                    }
                })
            }
        }

        Column(modifier = modifier
            .constrainAs(titleSection) {
                bottom.linkTo(bottomSection.top)
                start.linkTo(parent.start)
            }
            .padding(16.dp)) {
            Text(
                text = "Artikel dan informasi",
                color = Color.White,
                style = MaterialTheme.typography.labelSmall,
                modifier = modifier
            )
            Text(
                text = artikelResp.judul,
                color = Color.White,
                style = MaterialTheme.typography.labelLarge,
                modifier = modifier.padding(start = 2.dp, top=6.dp)
            )
        }
    }
}