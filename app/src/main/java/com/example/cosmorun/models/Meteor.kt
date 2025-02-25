package com.example.cosmorun

import androidx.compose.ui.geometry.Offset


enum class MeteorType {
    SMALL, MEDIUM, BIG, HEART, FUEL, SUPERFUEL
}


data class Meteor(
    var position: Offset,
    var radius: Float,
    var type: MeteorType,
    var speedX: Float = 0f,
    var speedY: Float = 0f,
    var rotationSpeed: Float = 0f,
    var rotation: Float = 0f
)
