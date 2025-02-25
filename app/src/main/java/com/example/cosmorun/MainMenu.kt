package com.example.cosmorun

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily

@Composable
fun MainMenu(onStartGame: () -> Unit) {

    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
    val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }

    Box(modifier = Modifier.fillMaxSize()) {

        Image(
            painter = painterResource(id = R.drawable.menu),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )


        val meteorPositions = listOf(
            Triple(R.drawable.small, Offset(0.1f * screenWidthPx, 0.2f * screenHeightPx), 50.dp),
            Triple(R.drawable.small, Offset(0.7f * screenWidthPx, 0.5f * screenHeightPx), 50.dp),
            Triple(R.drawable.medium, Offset(0.2f * screenWidthPx, 0.1f * screenHeightPx), 80.dp),
            Triple(R.drawable.big, Offset(0.6f * screenWidthPx, 0.25f * screenHeightPx), 120.dp),
            Triple(R.drawable.small, Offset(0.1f * screenWidthPx, 0.7f * screenHeightPx), 50.dp),
            Triple(R.drawable.medium, Offset(0.15f * screenWidthPx, 0.85f * screenHeightPx), 80.dp)
        )

        meteorPositions.forEach { (meteorRes, positionPx, sizeDp) ->
            val randomRotation = remember { (0..360).random() }
            Box(
                modifier = Modifier
                    .size(sizeDp)
                    .offset(
                        x = with(density) { positionPx.x.toDp() },
                        y = with(density) { positionPx.y.toDp() }
                    )
                    .rotate(randomRotation.toFloat())
            ) {
                Image(
                    painter = painterResource(id = meteorRes),
                    contentDescription = "Meteor",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }


        Image(
            painter = painterResource(id = R.drawable.roketm),
            contentDescription = "Rocket",
            modifier = Modifier
                .size(150.dp)
                .align(Alignment.BottomEnd)
                .offset(x = (-30).dp, y = (-50).dp)
                .rotate(-30f)
        )


        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "CosmoRun",
                style = TextStyle(
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily(
                        Font(R.font.pixel)
                    ),
                    color = Color.White
                ),
                modifier = Modifier.padding(16.dp)
            )
            Spacer(modifier = Modifier.height(32.dp))
            Box(
                modifier = Modifier.height(48.dp)
            ) {
                RetroButton(
                    text = "PRESS START",
                    onClick = onStartGame
                )
            }
        }
    }
}






