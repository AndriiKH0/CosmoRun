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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily

@Composable
fun MainMenu(
    onStartGame: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {

        Image(
            painter = painterResource(id = R.drawable.menu),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )


        val meteorImages = listOf(
            R.drawable.small,
            R.drawable.medium,
            R.drawable.big
        )


        val meteorPositions = listOf(
            Triple(R.drawable.small, Offset(100f, 250f), 50.dp),
            Triple(R.drawable.small, Offset(300f, 400f), 50.dp),
            Triple(R.drawable.medium, Offset(75f, 75f), 80.dp),
            Triple(R.drawable.big, Offset(250f, 150f), 120.dp),
            Triple(R.drawable.small, Offset(50f, 500f), 50.dp),
            Triple(R.drawable.medium, Offset(70f, 620f), 80.dp)
        )

        meteorPositions.forEach { (meteorRes, position, size) ->
            val randomRotation = remember { (0..360).random() }

            Box(
                modifier = Modifier
                    .size(size)
                    .offset(x = position.x.dp, y = position.y.dp)
                    .rotate(randomRotation.toFloat())
            ) {
                Image(
                    painter = painterResource(id = meteorRes),
                    contentDescription = "Метеорит",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }


        Image(
            painter = painterResource(id = R.drawable.roketm),
            contentDescription = "Ракета",
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
                modifier = Modifier
                    .height(48.dp)
            ) {
                RetroButton(
                    text = "PRESS START",
                    onClick = onStartGame
                )
            }
        }
    }
}





