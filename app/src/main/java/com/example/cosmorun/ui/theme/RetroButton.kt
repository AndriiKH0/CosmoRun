package com.example.cosmorun

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import androidx.compose.material3.Text
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity


@Composable
fun RetroButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current


    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
    val scaleFactor = (screenWidthPx / 1080f).toFloat()

    val buttonHeight = (48f * scaleFactor).dp
    val textSize = (22f * scaleFactor).sp

    val pixelFont = FontFamily(
        Font(R.font.pixel, FontWeight.Normal)
    )

    var isVisible by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        while (true) {
            isVisible = !isVisible
            delay(500)
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
            .height(buttonHeight)
            .padding((8f * scaleFactor).dp)
            .clickable(onClick = onClick)
    ) {
        Text(
            text = if (isVisible) text else " ",
            style = TextStyle(
                fontFamily = pixelFont,
                fontWeight = FontWeight.Bold,
                fontSize = textSize,
                color = if (isVisible) Color(0xFF00FFFF) else Color.Transparent
            )
        )
    }
}








