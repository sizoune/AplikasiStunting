package com.kominfotabalong.simasganteng.ui.component

import android.text.TextUtils
import android.widget.TextView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.kominfotabalong.simasganteng.BuildConfig
import com.kominfotabalong.simasganteng.R
import com.kominfotabalong.simasganteng.data.model.ArtikelResponse

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