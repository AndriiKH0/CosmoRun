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


@Composable
fun RetroButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

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
            .padding(8.dp)
            .clickable(onClick = onClick)
    ) {

        Text(
            text = if (isVisible) text else " ",
            style = TextStyle(
                fontFamily = pixelFont,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = if (isVisible) Color(0xFF00FFFF) else Color.Transparent
            )
        )
    }
}




