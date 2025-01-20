package com.example.cosmorun

import android.content.Context
import android.media.SoundPool

class SoundManager(context: Context) {
    private val soundPool = SoundPool.Builder().setMaxStreams(5).build()
    private val soundMap = mutableMapOf<String, Int>()

    init {
        soundMap["pickup"] = soundPool.load(context, R.raw.pickup, 1)
        soundMap["collision"] = soundPool.load(context, R.raw.collision, 1)
        soundMap["explosion"] = soundPool.load(context, R.raw.explosion, 1)
        soundMap["pick_fuel"] = soundPool.load(context, R.raw.pick_fuel, 1)
        soundMap["speed"] = soundPool.load(context, R.raw.speed, 1)
    }

    fun playSound(soundName: String) {
        soundMap[soundName]?.let { soundPool.play(it, 1f, 1f, 0, 0, 1f) }
    }

    fun release() {
        soundPool.release()
    }
}

