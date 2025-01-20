package com.example.cosmorun

import android.content.Context
import android.media.MediaPlayer

class MusicManager(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null

    fun start() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, R.raw.music1).apply {
                isLooping = true
                start()
            }
        } else if (!mediaPlayer!!.isPlaying) {
            mediaPlayer!!.start()
        }
    }

    fun stop() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
            }
            it.release()
        }
        mediaPlayer = null
    }
}
