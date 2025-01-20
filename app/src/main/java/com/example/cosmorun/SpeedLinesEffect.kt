package com.example.cosmorun

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import kotlinx.coroutines.delay
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlin.random.Random

@Composable
fun SpeedLinesEffect(isBoosting: Boolean) {
    if (isBoosting) {
        val speedLines = remember { mutableStateListOf<Pair<Offset, Float>>() }
        val configuration = LocalConfiguration.current
        val density = LocalDensity.current
        val screenWidth = with(density) { configuration.screenWidthDp.dp.toPx() }
        val screenHeight = with(density) { configuration.screenHeightDp.dp.toPx() }

        LaunchedEffect(isBoosting) {
            while (isBoosting) {

                repeat(3) {
                    val randomLength = Random.nextFloat() * (screenHeight * 0.3f) + (screenHeight * 0.2f)
                    speedLines.add(
                        Offset(
                            x = (0 until screenWidth.toInt()).random().toFloat(),
                            y = -200f
                        ) to randomLength
                    )
                }


                speedLines.replaceAll { (line, length) ->
                    Offset(line.x, line.y + screenHeight * 0.1f) to length
                }


                speedLines.removeAll { it.first.y > screenHeight }

                delay(16)
            }
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            speedLines.forEach { (line, length) ->
                drawLine(
                    color = Color(0xFFE0E0E0),
                    start = line,
                    end = Offset(line.x, line.y + length),
                    strokeWidth = screenWidth * 0.005f
                )
            }
        }
    }
}

