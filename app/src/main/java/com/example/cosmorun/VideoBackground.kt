package com.example.cosmorun
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.PlayerView
import androidx.compose.runtime.Composable
import android.content.Context
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout


object VideoPlayerSingleton {
    private var exoPlayer: ExoPlayer? = null

    fun getPlayer(context: Context): ExoPlayer {
        if (exoPlayer == null) {
            exoPlayer = ExoPlayer.Builder(context).build().apply {
                val mediaItem = MediaItem.fromUri("file:///android_asset/3.mp4")
                setMediaItem(mediaItem)
                prepare()
                playWhenReady = true
                repeatMode = ExoPlayer.REPEAT_MODE_ONE
            }
        }
        return exoPlayer!!
    }

    fun releasePlayer() {
        exoPlayer?.release()
        exoPlayer = null
    }
}

@Composable
fun VideoBackgroud(context: Context, isBoosting: Boolean) {

    val exoPlayer = remember { VideoPlayerSingleton.getPlayer(context) }


    LaunchedEffect(isBoosting) {
        if (isBoosting) {
            exoPlayer.setPlaybackSpeed(2.0f)
        } else {
            exoPlayer.setPlaybackSpeed(1.0f)
        }
    }


    DisposableEffect(Unit) {
        onDispose {

        }
    }

    AndroidView(
        factory = {
            PlayerView(it).apply {
                player = exoPlayer
                useController = false
                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}