package com.example.cosmorun

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StaticButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current


    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
    val scaleFactor = (screenWidthPx / 1080f).toFloat()

    val pixelFont = FontFamily(
        Font(R.font.pixel, FontWeight.Normal)
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .padding((8f * scaleFactor).dp)
            .fillMaxWidth()
            .height((48f * scaleFactor).dp)
            .clickable(onClick = onClick)
            .background(Color.Transparent)
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontFamily = pixelFont,
                fontWeight = FontWeight.Bold,
                fontSize = (24f * scaleFactor).sp,
                color = Color(0xFF00FFFF)
            )
        )
    }
}
