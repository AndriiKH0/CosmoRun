package com.example.cosmorun

import androidx.compose.ui.geometry.Offset
import java.util.concurrent.ConcurrentLinkedQueue


object MeteorObjectPool {

    private val smallPool = ConcurrentLinkedQueue<Meteor>()
    private val mediumPool = ConcurrentLinkedQueue<Meteor>()
    private val bigPool = ConcurrentLinkedQueue<Meteor>()
    private val heartPool = ConcurrentLinkedQueue<Meteor>()
    private val fuelPool = ConcurrentLinkedQueue<Meteor>()
    private val superFuelPool = ConcurrentLinkedQueue<Meteor>()


    private const val MAX_SMALL_POOL_SIZE = 30
    private const val MAX_MEDIUM_POOL_SIZE = 20
    private const val MAX_BIG_POOL_SIZE = 10
    private const val MAX_HEART_POOL_SIZE = 5
    private const val MAX_FUEL_POOL_SIZE = 5
    private const val MAX_SUPERFUEL_POOL_SIZE = 5


    fun obtain(
        type: MeteorType,
        position: Offset,
        radius: Float,
        speedX: Float = 0f,
        speedY: Float = 0f,
        rotationSpeed: Float = 0f
    ): Meteor {
        val pool = when (type) {
            MeteorType.SMALL -> smallPool
            MeteorType.MEDIUM -> mediumPool
            MeteorType.BIG -> bigPool
            MeteorType.HEART -> heartPool
            MeteorType.FUEL -> fuelPool
            MeteorType.SUPERFUEL -> superFuelPool
        }


        val meteor = pool.poll() ?: createNewMeteor(type)


        configureMeteor(meteor, type, position, radius, speedX, speedY, rotationSpeed)

        return meteor
    }


    private fun createNewMeteor(type: MeteorType): Meteor {
        return Meteor(
            position = Offset(0f, 0f),
            radius = 0f,
            type = type,
            speedX = 0f,
            speedY = 0f,
            rotationSpeed = 0f
        )
    }


    private fun configureMeteor(
        meteor: Meteor,
        type: MeteorType,
        position: Offset,
        radius: Float,
        speedX: Float,
        speedY: Float,
        rotationSpeed: Float
    ) {

        meteor.position = position
        meteor.radius = radius
        meteor.type = type
        meteor.speedX = speedX
        meteor.speedY = speedY
        meteor.rotationSpeed = rotationSpeed
        meteor.rotation = (0..360).random().toFloat()


    }


    fun recycle(meteor: Meteor) {

        meteor.speedX = 0f
        meteor.speedY = 0f
        meteor.rotation = 0f
        meteor.rotationSpeed = 0f


        when (meteor.type) {
            MeteorType.SMALL -> if (smallPool.size < MAX_SMALL_POOL_SIZE) smallPool.offer(meteor)
            MeteorType.MEDIUM -> if (mediumPool.size < MAX_MEDIUM_POOL_SIZE) mediumPool.offer(meteor)
            MeteorType.BIG -> if (bigPool.size < MAX_BIG_POOL_SIZE) bigPool.offer(meteor)
            MeteorType.HEART -> if (heartPool.size < MAX_HEART_POOL_SIZE) heartPool.offer(meteor)
            MeteorType.FUEL -> if (fuelPool.size < MAX_FUEL_POOL_SIZE) fuelPool.offer(meteor)
            MeteorType.SUPERFUEL -> if (superFuelPool.size < MAX_SUPERFUEL_POOL_SIZE) superFuelPool.offer(meteor)
        }
    }


    fun recycleAll(meteors: List<Meteor>) {
        meteors.forEach { recycle(it) }
    }


    fun prewarm() {
        repeat(10) { recycle(createNewMeteor(MeteorType.SMALL)) }
        repeat(6) { recycle(createNewMeteor(MeteorType.MEDIUM)) }
        repeat(3) { recycle(createNewMeteor(MeteorType.BIG)) }
        repeat(2) { recycle(createNewMeteor(MeteorType.HEART)) }
        repeat(2) { recycle(createNewMeteor(MeteorType.FUEL)) }
        repeat(2) { recycle(createNewMeteor(MeteorType.SUPERFUEL)) }
    }


    fun clear() {
        smallPool.clear()
        mediumPool.clear()
        bigPool.clear()
        heartPool.clear()
        fuelPool.clear()
        superFuelPool.clear()
    }
}