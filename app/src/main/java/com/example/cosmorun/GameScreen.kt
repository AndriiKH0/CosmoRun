package com.example.cosmorun

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import kotlinx.coroutines.delay
import androidx.compose.runtime.remember
import android.content.Context
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.example.cosmorun.ui.theme.GameScore
import com.google.firebase.database.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sqrt

@Composable
fun GameScreen(gyroscopeHandler: GyroscopeHandler, context: Context, onExitToMenu: () -> Unit) {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
    val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }
    val scaleFactor = screenWidthPx / 1080f
    val heartStartY = with(density) { 5.dp.toPx() }
    val fuelStartY = heartStartY + with(density) { 5.dp.toPx() + 5.dp.toPx() }


    val soundManager = remember { SoundManager(context) }
    val musicManager = remember { MusicManager(context) }
    var shipX by remember { mutableStateOf(screenWidthPx / 2) }
    var shipY by remember { mutableStateOf(screenHeightPx * 0.65f) }
    var lives by remember { mutableStateOf(3) }
    var score by remember { mutableStateOf(0) }
    var hasShownBoostInstruction by remember { mutableStateOf(false) }
    var isExploding by remember { mutableStateOf(false) }
    var explosionPosition by remember { mutableStateOf(Offset(shipX, shipY)) }
    val maxFuel = 100f
    var fuel by remember { mutableStateOf(100f) }
    var superFuel by remember { mutableStateOf(0f) }
    val maxSuperFuel = 100f
    var isBoosting by remember { mutableStateOf(false) }
    var isGameOver by remember { mutableStateOf(false) }
    var distanceCovered by remember { mutableStateOf(0f) }

    var isInvincible by remember { mutableStateOf(false) }
    val activeCracks = remember { mutableStateListOf<Pair<Offset, MeteorType>>() }
    var showBoostInstruction by remember { mutableStateOf(false) }
    var rotationAngle by remember { mutableStateOf(0f) }

    var gameSpeed by remember { mutableStateOf(10f) }
    var spawnInterval by remember { mutableStateOf(2000L) }
    val meteors = remember { mutableStateListOf<Meteor>() }
    val fuels = remember { mutableStateListOf<Meteor>() }
    var shipSpeed by remember { mutableStateOf(0f) }
    var meteorSpawnInterval by remember { mutableStateOf(2000L) }
    var meteorCount by remember { mutableStateOf(1) }
    var extraMeteorChance by remember { mutableStateOf(30) }
    var bigMeteorChance by remember { mutableStateOf(20) }
    val scoreManager = remember { ScoreManager(context) }
    var highScore by remember { mutableStateOf(0) }
    var currentHighScore by remember { mutableStateOf(0) }
    var scoreListener: ValueEventListener? = null
    LaunchedEffect(Unit) {

        MeteorObjectPool.prewarm()
    }
    LaunchedEffect(Unit) {

        scoreManager.ensureUserAuthenticated()


        scoreManager.loadHighScore { loadedScore ->
            highScore = loadedScore
            currentHighScore = loadedScore
        }


        scoreListener = scoreManager.addHighScoreListener { newHighScore ->
            currentHighScore = newHighScore
        }
    }
    DisposableEffect(Unit) {
        onDispose {
            scoreListener?.let { scoreManager.removeHighScoreListener(it) }
        }
    }

    VideoBackgroud(context = context, isBoosting = isBoosting)


    LaunchedEffect(Unit) {
        while (true) {
            rotationAngle = (rotationAngle + 2) % 360
            delay(16) // ~60 FPS
        }
    }




    DisposableEffect(Unit) {
        musicManager.start()
        onDispose {
            musicManager.stop()
        }
    }

    LaunchedEffect(isGameOver) {
        if (isGameOver) {
            musicManager.stop()
        } else {
            musicManager.start()
        }
    }


    if (isExploding) {
        ExplosionAnimation(
            position = explosionPosition,
            scaleFactor = 5f * scaleFactor
        ) {
            isGameOver = true
        }
    }


    DisposableEffect(Unit) {
        onDispose {
            soundManager.release()
        }
    }

// Poziom trudnosci -------------------------------------------------------------------------------------

    LaunchedEffect(isGameOver) {
        if (!isGameOver) {
            while (true) {
                val scoreIncrement = if (isBoosting) 5 else 1
                score += scoreIncrement
                delay(1000)
            }
        }
    }
    LaunchedEffect(score) {
        when {
            score >= 1000 -> {
                meteorSpawnInterval = 800L
                meteorCount = 4
                extraMeteorChance = 70
                bigMeteorChance = 40
                gameSpeed = 15f
            }
            score >= 750 -> {
                meteorSpawnInterval = 1000L
                meteorCount = 3
                extraMeteorChance = 60
                bigMeteorChance = 35
                gameSpeed = 14f
            }
            score >= 500 -> {
                meteorSpawnInterval = 1200L
                meteorCount = 3
                extraMeteorChance = 50
                bigMeteorChance = 30
                gameSpeed = 13f
            }
            score >= 300 -> {
                meteorSpawnInterval = 1500L
                meteorCount = 2
                extraMeteorChance = 40
                bigMeteorChance = 25
                gameSpeed = 12f
            }
            score >= 100 -> {
                meteorSpawnInterval = 1800L
                meteorCount = 2
                extraMeteorChance = 30
                bigMeteorChance = 20
                gameSpeed = 11f
            }
        }
    }



    // Meteor fuel heart --------------------------------------------------------------------------

    LaunchedEffect(isGameOver) {
        if (!isGameOver) {
            while (true) {
                delay(meteorSpawnInterval)


                repeat(meteorCount) {

                    val type = when {
                        (1..100).random() <= bigMeteorChance -> MeteorType.BIG
                        (1..2).random() == 1 -> MeteorType.SMALL
                        else -> MeteorType.MEDIUM
                    }


                    val meteor = Meteor(
                        position = Offset((50..950).random().toFloat() * scaleFactor, 0f),
                        radius = when (type) {
                            MeteorType.SMALL -> 30f * scaleFactor
                            MeteorType.MEDIUM -> 48f * scaleFactor
                            MeteorType.BIG -> 144f * scaleFactor
                            else -> 35f * scaleFactor
                        },
                        type = type,
                        speedX = (-3..3).random().toFloat(),
                        speedY = (5..10).random().toFloat(),
                        rotationSpeed = (1..5).random().toFloat()
                    )

                    meteors.add(meteor)
                }


                if ((1..100).random() <= extraMeteorChance) {
                    val extraCount = (1..2).random()
                    repeat(extraCount) {
                        val meteor = Meteor(
                            position = Offset((50..950).random().toFloat() * scaleFactor, 0f),
                            radius = 30f * scaleFactor,
                            type = MeteorType.SMALL,
                            speedX = (-4..4).random().toFloat(),
                            speedY = (6..12).random().toFloat(),
                            rotationSpeed = (2..6).random().toFloat()
                        )

                        meteors.add(meteor)
                    }
                }
            }
        }
    }


    LaunchedEffect(isGameOver) {
        if (!isGameOver) {
            while (true) {
                delay((5000..8000).random().toLong())

                meteors.add(
                    MeteorObjectPool.obtain(
                        type = MeteorType.FUEL,
                        position = Offset((50..950).random().toFloat() * scaleFactor, 0f),
                        radius = 120f * scaleFactor
                    )
                )
            }
        }
    }


    LaunchedEffect(isGameOver) {
        if (!isGameOver) {
            while (true) {
                delay((10000..15000).random().toLong())

                meteors.add(
                    MeteorObjectPool.obtain(
                        type = MeteorType.SUPERFUEL,
                        position = Offset((50..950).random().toFloat() * scaleFactor, 0f),
                        radius = 120f * scaleFactor
                    )
                )
            }
        }
    }


    LaunchedEffect(isGameOver) {
        if (!isGameOver) {
            while (true) {
                delay((5000..6000).random().toLong())
                val isHeart = (1..5).random() == 1

                if (isHeart) {
                    meteors.add(
                        MeteorObjectPool.obtain(
                            type = MeteorType.HEART,
                            position = Offset((50..950).random().toFloat() * scaleFactor, 0f),
                            radius = 20f * scaleFactor
                        )
                    )
                }
            }
        }
    }



    LaunchedEffect(isGameOver) {
        if (!isGameOver) {
            while (true) {
                if (!isBoosting) {
                    fuel -= 4f
                    if (fuel <= 0) {
                        isGameOver = true
                    }
                }

                if (isBoosting) {
                    superFuel -= 10f
                    if (superFuel <= 0) {
                        superFuel = 0f
                        isBoosting = false
                        isInvincible = false
                    }
                }

                delay(1000)
            }
        }
    }


    LaunchedEffect(isGameOver) {
        while (!isGameOver) {
            val currentSpeed = if (isBoosting) gameSpeed * 3 else gameSpeed
            val meteorsToRemove = mutableListOf<Meteor>()

            meteors.forEach { meteor ->
                if (meteor.position.y in -200f..screenHeightPx + 200f) {
                    val speedMultiplier = if (isInvincible) 2 else 1
                    meteor.position = Offset(
                        meteor.position.x + meteor.speedX,
                        meteor.position.y + meteor.speedY + currentSpeed * speedMultiplier
                    )
                } else {

                    meteorsToRemove.add(meteor)
                }
            }


            meteorsToRemove.forEach { meteor ->
                meteors.remove(meteor)
                MeteorObjectPool.recycle(meteor)
            }

            delay(16L)
        }
    }



    LaunchedEffect(isGameOver) {

        if (!isGameOver) {
            delay(500)
            gyroscopeHandler.calibrate()
        }

        while (!isGameOver) {

            val tilt = gyroscopeHandler.filteredTiltX


            shipX -= tilt * 6f * scaleFactor


            shipX = when {
                shipX < 0f -> shipX + screenWidthPx
                shipX > screenWidthPx -> shipX - screenWidthPx
                else -> shipX
            }

            delay(16)
        }

        if (isGameOver) {
            shipX = screenWidthPx / 2
        }
    }


    LaunchedEffect(isGameOver) {
        while (!isGameOver && !isExploding) {
            val collidedMeteors = mutableListOf<Meteor>()

            meteors.filter {
                abs(it.position.x - shipX) < 200 * scaleFactor && abs(it.position.y - shipY) < 200 * scaleFactor
            }.forEach { meteor ->
                val distance = sqrt(
                    (meteor.position.x - shipX).pow(2) +
                            (meteor.position.y - shipY).pow(2)
                )
                if (distance < meteor.radius + (50f * scaleFactor) && !isInvincible) {

                    val meteorSizeOffset = when (meteor.type) {
                        MeteorType.SMALL -> 48f * scaleFactor
                        MeteorType.MEDIUM -> 96f * scaleFactor
                        MeteorType.BIG -> 144f * scaleFactor
                        else -> 48f * scaleFactor
                    }

                    val collisionPosition = Offset(
                        x = (meteor.position.x - meteorSizeOffset / 2 + shipX) / 2,
                        y = (meteor.position.y - meteorSizeOffset / 2 + shipY) / 2
                    )

                    activeCracks.add(collisionPosition to meteor.type)

                    when (meteor.type) {
                        MeteorType.SMALL, MeteorType.MEDIUM, MeteorType.BIG -> {
                            soundManager.playSound("collision")
                            lives -= when (meteor.type) {
                                MeteorType.SMALL -> 1
                                MeteorType.MEDIUM -> 1
                                MeteorType.BIG -> 2
                                else -> 0
                            }
                            if (lives <= 0 && !isExploding) {
                                isExploding = true
                                explosionPosition = Offset(shipX, shipY)
                                soundManager.playSound("explosion")
                            }
                        }

                        MeteorType.HEART -> {
                            if (lives < 5) {
                                lives += 1
                                soundManager.playSound("pickup")
                            }
                        }

                        MeteorType.FUEL -> {
                            fuel = (fuel + 45).coerceAtMost(maxFuel)
                            soundManager.playSound("pick_fuel")
                        }

                        MeteorType.SUPERFUEL -> {
                            superFuel = (superFuel + 25).coerceAtMost(maxSuperFuel)
                            soundManager.playSound("pick_fuel")
                        }

                        else -> {}
                    }
                    collidedMeteors.add(meteor)
                }
            }

            meteors.removeAll(collidedMeteors)
            collidedMeteors.forEach { meteor ->
                MeteorObjectPool.recycle(meteor)
            }
            if (fuel <= 0) {
                isGameOver = true
                soundManager.playSound("explosion")
            }

            delay(16) // ~60 FPS
        }
    }


    LaunchedEffect(superFuel) {
        if (superFuel > 0 && !showBoostInstruction && !isBoosting && !hasShownBoostInstruction) {
            showBoostInstruction = true
            hasShownBoostInstruction = true
            delay(3000)
            showBoostInstruction = false
        }
    }

    LaunchedEffect(isGameOver) {
        if (!isGameOver) {
            showBoostInstruction = false
            hasShownBoostInstruction = false
        }
    }

    // Interfejs ------------------------------------------------------------------------------------------------------
    Box(modifier = Modifier.fillMaxSize()) {

        drawGameObjects(
            meteors = meteors,
            lives = lives,
            startY = heartStartY

        )

        FuelAndBoostIndicators(
            fuel = fuel,
            superFuel = superFuel,
            startY = fuelStartY
        )




        activeCracks.forEach { (position, type) ->
            CrackAnimation(
                position = position,
                meteorType = type,
                onAnimationEnd = { activeCracks.remove(Pair(position, type)) }
            )
        }


        SpeedLinesEffect(isBoosting = isBoosting)


        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            if (superFuel > 0 && !isBoosting) {
                                isBoosting = true
                                isInvincible = true
                                soundManager.playSound("speed")
                                showBoostInstruction = false
                            }
                        }
                    )
                }
        ) {

            if (showBoostInstruction) {
                Text(
                    text = "Tap for BOOST",
                    style = TextStyle(
                        fontSize = (16f * scaleFactor).sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFDFDBA),
                        fontFamily = FontFamily(Font(R.font.pixel))
                    ),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(bottom = (50f * scaleFactor).dp)
                )
            }
        }


        RocketAnimation(
            lives = lives,
            shipX = shipX,
            shipY = shipY
        )

        GameScore(
            score = score,
            modifier = Modifier
        )

        if (isGameOver) {
            if (score > highScore) {

                scoreManager.saveHighScore(score) { success ->
                    if (success) {

                        highScore = score
                    }
                }
            }

                GameOverScreen(
                    score = score,
                    highScore = highScore,
                    onReplay = {
                        isGameOver = false
                        lives = 3
                        score = 0
                        MeteorObjectPool.recycleAll(meteors.toList())
                        meteors.clear()
                        fuel = 100f
                        shipSpeed = 0f
                        superFuel = 0f
                        shipX = screenWidthPx / 2
                        gameSpeed = 10f
                        spawnInterval = 2000L
                        meteors.clear()
                        fuels.clear()
                        isExploding = false
                        isInvincible = false
                        distanceCovered = 0f
                        musicManager.stop()
                        musicManager.start()
                    },
                    onExitToMenu = onExitToMenu
                )




        }
    }
}