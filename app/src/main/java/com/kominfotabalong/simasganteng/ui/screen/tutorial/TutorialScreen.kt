package com.kominfotabalong.simasganteng.ui.screen.tutorial

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.ramcosta.composedestinations.annotation.Destination

data class VideoItem(
    val title: String,
    val thumbnailResId: Int, // Resource ID for the thumbnail image
    val youtubeVideoId: String,
    val description: String
)

@Composable
@Destination
fun TutorialScreen() {
    // Remember these states to prevent recreation during recomposition
    var selectedVideoId by remember { mutableStateOf<String?>(null) }
    LocalContext.current

    val videos = listOf(
        VideoItem(
            title = "Pengukuran Panjang Bayi",
            // Replace with your actual resource ID
            thumbnailResId = android.R.drawable.ic_media_play,
            youtubeVideoId = "KxFs54ZzBxY",
            description = "Tutorial penimbangan bayi yang benar menggunakan timbangan digital"
        ),
        VideoItem(
            title = "Pengukuran Panjang Badan Bayi",
            // Replace with your actual resource ID
            thumbnailResId = android.R.drawable.ic_media_play,
            youtubeVideoId = "WESP6_Jyv2o",
            description = "Cara mengukur panjang badan bayi dengan tepat"
        ),
        VideoItem(
            title = "Pengukuran Tinggi Badan Balita",
            // Replace with your actual resource ID
            thumbnailResId = android.R.drawable.ic_media_play,
            youtubeVideoId = "cE3781McuLM",
            description = "Teknik pengukuran tinggi badan balita yang akurat"
        ),
        VideoItem(
            title = "Antropometri untuk Pemantauan Pertumbuhan",
            // Replace with your actual resource ID
            thumbnailResId = android.R.drawable.ic_media_play,
            youtubeVideoId = "nM1OD5QVj-s",
            description = "Penjelasan tentang antropometri dan penggunaannya dalam pemantauan pertumbuhan"
        )
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(videos) { video ->
            VideoCard(
                video = video,
                isPlaying = selectedVideoId == video.youtubeVideoId,
                onVideoSelected = { selectedVideoId = video.youtubeVideoId },
                onClose = { selectedVideoId = null }
            )
        }
    }
}

@Composable
fun VideoCard(
    video: VideoItem,
    isPlaying: Boolean,
    onVideoSelected: () -> Unit,
    onClose: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isPlaying, onClick = onVideoSelected),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            if (isPlaying) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp) // Adjust height as needed
                ) {
                    YouTubePlayerWithControls(
                        videoId = video.youtubeVideoId,
                        onClose = onClose
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                ) {
                    AsyncImage(
                        model = "https://img.youtube.com/vi/${video.youtubeVideoId}/hqdefault.jpg",
                        contentDescription = "Video thumbnail",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp),
                        contentScale = ContentScale.Crop
                    )
                    IconButton(
                        onClick = onVideoSelected,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(64.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayCircleFilled,
                            contentDescription = "Play",
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
            }

//            Column(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(16.dp)
//            ) {
//                Text(
//                    text = video.title,
//                    fontSize = 18.sp,
//                    fontWeight = FontWeight.Bold,
//                    color = MaterialTheme.colorScheme.onSurface
//                )
//
//                Spacer(modifier = Modifier.height(8.dp))
//
//                Text(
//                    text = video.description,
//                    fontSize = 14.sp,
//                    color = MaterialTheme.colorScheme.onSurfaceVariant
//                )
//            }
        }
    }
}


@Composable
fun YouTubePlayerWithControls(
    videoId: String,
    onClose: () -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { context ->
                YouTubePlayerView(context).apply {
                    enableAutomaticInitialization = false

                    val iFramePlayerOptions = IFramePlayerOptions.Builder()
                        .build()

                    lifecycleOwner.lifecycle.addObserver(this)

                    initialize(object : AbstractYouTubePlayerListener() {
                        override fun onReady(player: YouTubePlayer) {
                            player.loadVideo(videoId, 0f)
                        }
                    }, iFramePlayerOptions)
                }
            }
        )

        IconButton(
            onClick = onClose,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(48.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}