package com.example.cosmorun

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.delay

@Composable
fun CrackAnimation(
    position: Offset,
    meteorType: MeteorType,
    onAnimationEnd: () -> Unit
) {

    val crackData = when (meteorType) {
        MeteorType.SMALL -> Triple(
            ImageBitmap.imageResource(id = R.drawable.crack),
            128,
            128
        )
        MeteorType.MEDIUM -> Triple(
            ImageBitmap.imageResource(id = R.drawable.crack2),
            256,
            256
        )
        MeteorType.BIG -> Triple(
            ImageBitmap.imageResource(id = R.drawable.crack3),
            384,
            384
        )
        else -> null
    }

    if (crackData == null) return

    val (crackImage, frameWidth, frameHeight) = crackData
    val frameCount = crackImage.height / frameHeight
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
            drawCrackFrame(
                image = crackImage,
                frame = currentFrame,
                position = position,
                frameWidth = frameWidth,
                frameHeight = frameHeight
            )
        }
    }
}

fun DrawScope.drawCrackFrame(
    image: ImageBitmap,
    frame: Int,
    position: Offset,
    frameWidth: Int,
    frameHeight: Int
) {
    drawImage(
        image = image,
        srcOffset = IntOffset(0, frame * frameHeight),
        srcSize = IntSize(frameWidth, frameHeight),
        dstOffset = IntOffset((position.x - frameWidth / 2).toInt(), (position.y - frameHeight / 2).toInt()),
        dstSize = IntSize(frameWidth, frameHeight)
    )
}


