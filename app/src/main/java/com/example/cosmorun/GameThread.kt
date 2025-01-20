package com.example.cosmorun

import android.graphics.Canvas
import android.view.SurfaceHolder

class GameThread(private val surfaceHolder: SurfaceHolder) : Thread() {
    var running: Boolean = false

    override fun run() {
        while (running) {
            val canvas: Canvas? = surfaceHolder.lockCanvas()
            try {
                canvas?.let {
                    synchronized(surfaceHolder) {

                    }
                }
            } finally {
                canvas?.let { surfaceHolder.unlockCanvasAndPost(it) }
            }
        }
    }
}
