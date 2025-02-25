package com.example.cosmorun

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.example.cosmorun.ui.theme.CosmoRunTheme

class MainActivity : ComponentActivity() {
    private lateinit var gyroscopeHandler: GyroscopeHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gyroscopeHandler = GyroscopeHandler(this)
        gyroscopeHandler.startListening()

        setContent {
            CosmoRunTheme {
                var isGameRunning by remember { mutableStateOf(false) }

                if (isGameRunning) {
                    GameScreen(
                        gyroscopeHandler = gyroscopeHandler,
                        context = this,
                        onExitToMenu = { isGameRunning = false }
                    )
                } else {
                    MainMenu(
                        onStartGame = { isGameRunning = true }
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        VideoPlayerSingleton.releasePlayer()

        if (::gyroscopeHandler.isInitialized) {
            gyroscopeHandler.stopListening()
        }
    }

}
