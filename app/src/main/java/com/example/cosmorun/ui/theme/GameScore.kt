package com.example.cosmorun.ui.theme

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.example.cosmorun.R



import androidx.compose.foundation.layout.*



@Composable
fun GameScore(
    score: Int,
    modifier: Modifier = Modifier
) {

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val indicatorWidth = screenWidth * 0.4f


    val textSize = (indicatorWidth.value * 0.05f).coerceIn(16f, 24f).sp


    val padding = (16.dp)

    Box(
        contentAlignment = Alignment.TopEnd,
        modifier = modifier.fillMaxSize()
    ) {
        Text(
            text = "Score: $score",
            style = TextStyle(
                color = Color.White,
                fontSize = textSize,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily(Font(R.font.pixel))
            ),
            modifier = Modifier.padding(padding)
        )
    }
}





