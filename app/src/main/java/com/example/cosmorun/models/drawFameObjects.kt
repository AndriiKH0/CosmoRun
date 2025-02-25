package com.example.cosmorun

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.remember
import android.graphics.Rect
import android.graphics.Paint

@Composable
fun drawGameObjects(
    meteors: List<Meteor>,
    lives: Int,
    startY: Float
) {

    val smallMeteorSprite = ImageBitmap.imageResource(id = R.drawable.small)
    val mediumMeteorSprite = ImageBitmap.imageResource(id = R.drawable.medium)
    val bigMeteorSprite = ImageBitmap.imageResource(id = R.drawable.big)
    val heartImage = ImageBitmap.imageResource(id = R.drawable.hearts)
    val fuelImage = ImageBitmap.imageResource(id = R.drawable.fuel_r)
    val superfuelImage = ImageBitmap.imageResource(id = R.drawable.fuel_sr)


    val srcRect = remember { Rect() }
    val dstRect = remember { Rect() }
    val paint = remember { Paint() }


    val configuration = LocalConfiguration.current
    val density = LocalDensity.current


    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
    val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }
    val scaleFactor = screenWidthPx / 1080f


    val smallSizePx = 96f * scaleFactor
    val mediumSizePx = 144f * scaleFactor
    val bigSizePx = 288f * scaleFactor
    val fuelSizePx = 128f * scaleFactor
    val heartSizePx = 96f * scaleFactor
    val spacing = (heartSizePx * -0.1f).coerceAtMost(-18f)

    Canvas(modifier = Modifier.fillMaxSize()) {

        val visibleMeteors = meteors.filter {
            it.position.y in -bigSizePx..screenHeightPx + bigSizePx &&
                    it.position.x in -bigSizePx..screenWidthPx + bigSizePx
        }


        drawIntoCanvas { canvas ->
            srcRect.set(0, 0, heartImage.width, heartImage.height)

            for (i in 0 until lives.coerceAtMost(5)) {
                dstRect.set(
                    (i * (heartSizePx + spacing)).toInt(),
                    startY.toInt(),
                    (i * (heartSizePx + spacing) + heartSizePx).toInt(),
                    (startY + heartSizePx).toInt()
                )

                canvas.nativeCanvas.drawBitmap(
                    heartImage.asAndroidBitmap(),
                    srcRect,
                    dstRect,
                    paint
                )
            }
        }


        drawIntoCanvas { canvas ->

            visibleMeteors
                .sortedByDescending {
                    when(it.type) {
                        MeteorType.BIG -> 3
                        MeteorType.MEDIUM -> 2
                        MeteorType.SMALL -> 1
                        else -> 0
                    }
                }
                .forEach { meteor ->
                    val meteorCenter = Offset(meteor.position.x, meteor.position.y)
                    val size = when (meteor.type) {
                        MeteorType.SMALL -> smallSizePx
                        MeteorType.MEDIUM -> mediumSizePx
                        MeteorType.BIG -> bigSizePx
                        MeteorType.HEART -> heartSizePx
                        MeteorType.SUPERFUEL -> fuelSizePx
                        MeteorType.FUEL -> fuelSizePx
                        else -> smallSizePx
                    }

                    val image = when (meteor.type) {
                        MeteorType.SMALL -> smallMeteorSprite
                        MeteorType.MEDIUM -> mediumMeteorSprite
                        MeteorType.BIG -> bigMeteorSprite
                        MeteorType.HEART -> heartImage
                        MeteorType.SUPERFUEL -> superfuelImage
                        MeteorType.FUEL -> fuelImage
                        else -> smallMeteorSprite
                    }

                    srcRect.set(0, 0, image.width, image.height)
                    dstRect.set(
                        (meteor.position.x - size / 2).toInt(),
                        (meteor.position.y - size / 2).toInt(),
                        (meteor.position.x + size / 2).toInt(),
                        (meteor.position.y + size / 2).toInt()
                    )


                    if (meteor.type in listOf(MeteorType.SMALL, MeteorType.MEDIUM, MeteorType.BIG)) {
                        meteor.rotation = (meteor.rotation + meteor.rotationSpeed) % 360
                        canvas.nativeCanvas.save()
                        canvas.nativeCanvas.rotate(
                            meteor.rotation,
                            meteorCenter.x,
                            meteorCenter.y
                        )
                        canvas.nativeCanvas.drawBitmap(image.asAndroidBitmap(), srcRect, dstRect, paint)
                        canvas.nativeCanvas.restore()
                    } else {
                        canvas.nativeCanvas.drawBitmap(image.asAndroidBitmap(), srcRect, dstRect, paint)
                    }
                }
        }
    }
}