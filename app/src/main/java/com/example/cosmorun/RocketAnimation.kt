package com.example.cosmorun

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun RocketAnimation(lives: Int, shipX: Float, shipY: Float, baseScale: Float = 2.3f) {

    val rocketSprite = ImageBitmap.imageResource(id = R.drawable.roket13)

    val configuration = LocalConfiguration.current
    val density = LocalDensity.current

    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
    val scaleFactor = screenWidthPx / 1080f

    val frameWidth = (128 * scaleFactor).toInt()
    val frameHeight = (128 * scaleFactor).toInt()
    val totalFrames = 7
    val frameDuration = 200L

    val row = when {
        lives >= 3 -> 0
        lives == 2 -> 1
        lives == 1 -> 2
        else -> 3
    }

    var currentFrame by remember { mutableStateOf(0) }

    LaunchedEffect(row) {
        while (true) {
            currentFrame = (currentFrame + 1) % totalFrames
            delay(frameDuration)
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        drawIntoCanvas { canvas ->
            val srcRect = android.graphics.Rect(
                currentFrame * 128,
                row * 128,
                (currentFrame + 1) * 128,
                (row + 1) * 128
            )

            val scaledWidth = (frameWidth * baseScale).toInt()
            val scaledHeight = (frameHeight * baseScale).toInt()

            val dstRect = android.graphics.Rect(
                (shipX - scaledWidth / 2).toInt(),
                (shipY - scaledHeight / 2).toInt(),
                (shipX + scaledWidth / 2).toInt(),
                (shipY + scaledHeight / 2).toInt()
            )

            val androidBitmap = rocketSprite.asAndroidBitmap()

            canvas.nativeCanvas.drawBitmap(
                androidBitmap,
                srcRect,
                dstRect,
                null
            )
        }
    }
}