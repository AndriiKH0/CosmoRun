package com.example.cosmorun

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.delay

@Composable
fun ExplosionAnimation(
    position: Offset,
    scaleFactor: Float = 2f,
    onAnimationEnd: () -> Unit
) {
    val explosionImage = ImageBitmap.imageResource(id = R.drawable.boom)
    val frameWidth = explosionImage.width / 11
    val frameHeight = explosionImage.height
    val frameCount = 11

    var currentFrame by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while (currentFrame < frameCount) {
            delay(100)
            currentFrame++
        }
        onAnimationEnd()
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        if (currentFrame < frameCount) {
            drawExplosionFrame(
                image = explosionImage,
                frame = currentFrame,
                position = position,
                frameWidth = frameWidth,
                frameHeight = frameHeight,
                scaleFactor = scaleFactor
            )
        }
    }

}
fun DrawScope.drawExplosionFrame(
    image: ImageBitmap,
    frame: Int,
    position: Offset,
    frameWidth: Int,
    frameHeight: Int,
    scaleFactor: Float
) {
    drawImage(
        image = image,
        srcOffset = IntOffset(frame * frameWidth, 0),
        srcSize = IntSize(frameWidth, frameHeight),
        dstOffset = IntOffset(
            (position.x - (frameWidth * scaleFactor) / 2).toInt(),
            (position.y - (frameHeight * scaleFactor) / 2).toInt()
        ),
        dstSize = IntSize(
            (frameWidth * scaleFactor).toInt(),
            (frameHeight * scaleFactor).toInt()
        )
    )
}



