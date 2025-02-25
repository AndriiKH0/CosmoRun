package com.example.cosmorun

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class GyroscopeHandler(context: Context) : SensorEventListener {
    private val sensorManager: SensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    var tiltX = 0f
    private var _filteredTiltX = 0f


    private val alpha = 0.08f


    var sensitivity = 0.8f


    private var neutralPositionX = 0f


    val filteredTiltX: Float
        get() = (_filteredTiltX - neutralPositionX) * sensitivity

    fun startListening() {
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    fun stopListening() {
        sensorManager.unregisterListener(this)
    }

    fun calibrate() {
        neutralPositionX = _filteredTiltX
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {

                tiltX = event.values[0]


                _filteredTiltX = _filteredTiltX + alpha * (tiltX - _filteredTiltX)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}