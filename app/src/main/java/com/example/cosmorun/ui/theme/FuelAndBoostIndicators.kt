package com.example.cosmorun

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight


@Composable
fun FuelAndBoostIndicators(
    fuel: Float,
    superFuel: Float,
    modifier: Modifier = Modifier,
    startY: Float
) {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val screenWidth = configuration.screenWidthDp.dp
    val indicatorWidth = screenWidth * 0.4f
    val textSize = (indicatorWidth.value * 0.05f).coerceIn(10f, 18f).sp

    val indicatorHeight = indicatorWidth * 0.05f

    Column(
        modifier = modifier
            .padding(start = 16.dp, top = with(density) { startY.toDp() }),
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(modifier = Modifier.height(30.dp))


        Text(
            text = "FUEL",
            style = TextStyle(
                color = Color.White,
                fontSize = textSize,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily(Font(R.font.pixel))
            )
        )
        Spacer(modifier = Modifier.height(4.dp))


        Box(
            modifier = Modifier
                .width(indicatorWidth)
                .height(indicatorHeight)
                .background(Color.Gray)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(indicatorWidth * (fuel / 100f))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(Color.Green, Color.Yellow, Color.Red)
                        )
                    )
            )
        }

        Spacer(modifier = Modifier.height(8.dp))


        Text(
            text = "BOOST",
            style = TextStyle(
                color = Color.White,
                fontSize = textSize,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily(Font(R.font.pixel))
            )
        )
        Spacer(modifier = Modifier.height(4.dp))


        Box(
            modifier = Modifier
                .width(indicatorWidth)
                .height(indicatorHeight)
                .background(Color.Gray)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(indicatorWidth * (superFuel / 100f))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(Color.Magenta, Color.Yellow)
                        )
                    )
            )
        }
    }
}




