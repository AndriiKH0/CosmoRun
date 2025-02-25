package com.example.cosmorun

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity

@Composable
fun GameOverScreen(
    score: Int,
    highScore: Int,
    onReplay: () -> Unit,
    onExitToMenu: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current


    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
    val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }
    val scaleFactor = ((screenWidthPx / 1080f) + (screenHeightPx / 1920f)) / 2


    val gameOverTextSize = (35f * scaleFactor).sp
    val scoreTextSize = (25f * scaleFactor).sp
    val highScoreTextSize = (22f * scaleFactor).sp
    val buttonHeight = (48f * scaleFactor).dp
    val padding = (16f * scaleFactor).dp

    val pixelFont = FontFamily(Font(R.font.pixel, FontWeight.Normal))

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Text(
                text = "GAME OVER",
                style = TextStyle(
                    fontSize = gameOverTextSize,
                    fontWeight = FontWeight.Bold,
                    fontFamily = pixelFont,
                    color = Color.Red
                ),
                modifier = Modifier
                    .padding(bottom = padding)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Text(
                text = "Your Score:",
                style = TextStyle(
                    fontSize = scoreTextSize,
                    fontWeight = FontWeight.Medium,
                    fontFamily = pixelFont,
                    color = Color.White,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier
                    .padding(bottom = padding / 2)
                    .fillMaxWidth()
            )
            Text(
                text = "$score",
                style = TextStyle(
                    fontSize = scoreTextSize,
                    fontWeight = FontWeight.Medium,
                    fontFamily = pixelFont,
                    color = Color.White,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier
                    .padding(bottom = padding)
                    .fillMaxWidth()
            )
            Text(
                text = "High Score: $highScore",
                style = TextStyle(
                    fontSize = highScoreTextSize,
                    fontWeight = FontWeight.Medium,
                    fontFamily = pixelFont,
                    color = Color.White,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier
                    .padding(bottom = padding)
                    .fillMaxWidth()
            )

            StaticButton(
                text = "REPLAY",
                onClick = onReplay,
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(buttonHeight)
            )

            Spacer(modifier = Modifier.height(padding / 2))

            StaticButton(
                text = "HOME",
                onClick = onExitToMenu,
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(buttonHeight)
            )
        }
    }
}
