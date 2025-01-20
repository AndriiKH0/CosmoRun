package com.example.cosmorun

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlin.math.max
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.res.imageResource
import kotlinx.coroutines.delay
import androidx.compose.runtime.remember
import android.content.Context
import android.util.Log
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

@Composable
fun GameScreen(gyroscopeHandler: GyroscopeHandler, context: Context, onExitToMenu: () -> Unit) {

    val configuration = LocalConfiguration.current
    val screenWidthPx = with(LocalDensity.current) { configuration.screenWidthDp.dp.toPx() }
    val screenHeightPx = with(LocalDensity.current) { configuration.screenHeightDp.dp.toPx() }

    // Состояния игры------------------------------------------------------------------------------------------------------
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
    val database = FirebaseDatabase.getInstance("https://cosmorun-d5d84-default-rtdb.europe-west1.firebasedatabase.app")
    val highScoreRef = database.getReference("high-score")
    var highScore by remember { mutableStateOf(0) }
    val maxSuperFuel = 100f
    var isBoosting by remember { mutableStateOf(false) }
    var isGameOver by remember { mutableStateOf(false) }
    var distanceCovered by remember { mutableStateOf(0f) }
    val heartImage = ImageBitmap.imageResource(id = R.drawable.hearts)
    val fuelImage = ImageBitmap.imageResource(id = R.drawable.fuel_r)
    val superfuelImage = ImageBitmap.imageResource(id = R.drawable.fuel_sr)
    var isInvincible by remember { mutableStateOf(false) }
    val activeCracks = remember { mutableStateListOf<Pair<Offset, MeteorType>>() }
    var showBoostInstruction by remember { mutableStateOf(false) }
    var rotationAngle by remember { mutableStateOf(0f) }
    val meteorSprite = ImageBitmap.imageResource(id = R.drawable.small)
    val mediumMeteorSprite = ImageBitmap.imageResource(id = R.drawable.medium)
    val bigMeteorSprite = ImageBitmap.imageResource(id = R.drawable.big)
    var currentHighScore by remember {mutableStateOf(0)}
    var gameSpeed by remember { mutableStateOf(10f) }
    var spawnInterval by remember { mutableStateOf(2000L) }
    val meteors = remember { mutableStateListOf<Meteor>() }
    val fuels = remember { mutableStateListOf<Meteor>() }
    var shipSpeed by remember { mutableStateOf(0f) }
    //------------------------------------------------------------------------------------------------------


    fun saveHighScore(score: Int) {

        highScoreRef.setValue(score).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("Firebase", "High score saved successfully")
            } else {
                Log.e("Firebase", "Failed to save high score", task.exception)
            }
        }
    }



    fun loadHighScore(onResult: (Int) -> Unit) {


        highScoreRef.get().addOnSuccessListener { dataSnapshot ->
            val highScore = dataSnapshot.getValue(Int::class.java) ?: 0
            onResult(highScore)
            Log.d("Firebase", "High score loaded: $highScore")
        }.addOnFailureListener { exception ->
            Log.e("Firebase", "Failed to load high score", exception)
        }
    }

    highScoreRef.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {

            val newHighScore = snapshot.getValue(Int::class.java) ?: 0
            currentHighScore = newHighScore
        }

        override fun onCancelled(error: DatabaseError) {

            println("Ошибка получения данных: ${error.message}")
        }
    })







    LaunchedEffect(Unit) {
        loadHighScore { loadedScore ->
            highScore = loadedScore
        }
    }

    // ВИДЕО
        VideoBackgroud(context = context, isBoosting = isBoosting)

        // Логика обновления угла вращения------------------------------------------------------------------------------------------------------
        LaunchedEffect(Unit) {
            while (true) {
                rotationAngle = (rotationAngle + 2) % 360
                delay(16)
            }
        }


        val pixelFont = FontFamily(
            Font(R.font.font)
        )
        // МУЗИКА------------------------------------------------------------------------------------------------------

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
                scaleFactor = 5f
            ) {
                isGameOver = true
            }
        }



        DisposableEffect(Unit) {
            onDispose {
                soundManager.release()
            }
        }
        // СЛОЖНОСТЬ ИГРИ------------------------------------------------------------------------------------------------------
        // Увеличение сложности
        LaunchedEffect(isGameOver) {
            if (!isGameOver) {
                while (true) {
                    delay(20000)
                    gameSpeed += 0.5f
                    spawnInterval = max(500L, spawnInterval - 100L)
                    delay(5000)
                }
            }
        }
        // Увеличение счёта
        LaunchedEffect(isGameOver) {
            if (!isGameOver) {
                while (true) {
                    val scoreIncrement =
                        if (isBoosting) 5 else 1
                    score += scoreIncrement
                    delay(100)
                }
            }
        }

        //ТОПЛИВО И СЕРЦЕ--------------------------------------------------------------------------------------------------------------------

        // ГЕНЕРАЦИЯ МЕТЕОРИТОВ ТОПЛИВО И СЕРЦЕ--------------------------------------------------------------------------------------------------------------------

    LaunchedEffect(isGameOver) {
        if (!isGameOver) {
            var currentSpawnInterval = spawnInterval

            while (true) {
                delay(currentSpawnInterval)

                val type = when ((1..3).random()) {
                    1 -> MeteorType.SMALL
                    2 -> MeteorType.MEDIUM
                    else -> MeteorType.BIG
                }

                meteors.add(
                    Meteor(
                        position = Offset((50..950).random().toFloat(), 0f),
                        radius = when (type) {
                            MeteorType.SMALL -> 30f
                            MeteorType.MEDIUM -> 48f
                            MeteorType.BIG -> 144f
                            else -> 35f
                        },
                        type = type,
                        speedX = (-3..3).random().toFloat(),
                        speedY = (5..10).random().toFloat(),
                        rotationSpeed = (1..5).random().toFloat()
                    )
                )


                if (score >= 300) {
                    currentSpawnInterval = max(500L, currentSpawnInterval - 50L)
                    if ((1..10).random() <= 3) {
                        meteors.add(
                            Meteor(
                                position = Offset((50..950).random().toFloat(), 0f),
                                radius = 30f,
                                type = MeteorType.SMALL,
                                speedX = (-3..3).random().toFloat(),
                                speedY = (5..15).random().toFloat(),
                                rotationSpeed = (2..6).random().toFloat()
                            )
                        )
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
                        Meteor(
                            position = Offset((50..950).random().toFloat(), 0f),
                            radius = 30f,
                            type = MeteorType.FUEL
                        )
                    )
                }
            }
        }

// Генерация супер-топлива
        LaunchedEffect(isGameOver) {
            if (!isGameOver) {
                while (true) {
                    delay(
                        (10000..15000).random().toLong()
                    )

                    meteors.add(
                        Meteor(
                            position = Offset((50..950).random().toFloat(), 0f),
                            radius = 30f,
                            type = MeteorType.SUPERFUEL
                        )
                    )
                }
            }
        }

// Генерация сердец
        LaunchedEffect(isGameOver) {
            if (!isGameOver) {
                while (true) {
                    delay(
                        (5000..6000).random().toLong()
                    )
                    val isHeart = (1..5).random() == 1

                    if (isHeart) {
                        meteors.add(
                            Meteor(
                                position = Offset((50..950).random().toFloat(), 0f),
                                radius = 20f,
                                type = MeteorType.HEART
                            )
                        )
                    }
                }
            }
        }


        // Уменьшение топлива и супе топливо

        LaunchedEffect(isGameOver) {
            if (!isGameOver) {
                while (true) {
                    if (!isBoosting) {

                        fuel -= 4.toFloat()
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


        // Движение метеоритов и топлива
        LaunchedEffect(isGameOver) {
            while (!isGameOver) {
                val currentSpeed =
                    if (isBoosting) gameSpeed * 3 else gameSpeed

                val visibleMeteors =
                    meteors.filter { it.position.y in -200f..2000f }
                visibleMeteors.forEach { meteor ->
                    if (!isInvincible) {
                        meteor.position = Offset(
                            meteor.position.x + meteor.speedX,
                            meteor.position.y + meteor.speedY + currentSpeed
                        )
                    } else {
                        meteor.position = Offset(
                            meteor.position.x + meteor.speedX,
                            meteor.position.y + meteor.speedY + currentSpeed * 2
                        )
                    }
                }

                fuels.forEach { fuelObject ->
                    fuelObject.position = Offset(
                        fuelObject.position.x,
                        fuelObject.position.y + currentSpeed
                    )
                }

                meteors.removeAll { it.position.y > 2000f || it.position.x !in -100f..1100f }
                fuels.removeAll { it.position.y > 2000f }

                delay(16)

            }
        }


        // Управление кораблём--------------------------------------------------------------------------------------------------------------------
        LaunchedEffect(isGameOver) {
            var velocityX = 0f
            var initialTiltZ = 0f
            var initialTiltX = 0f
            var lastStableTiltZ = 0f
            var lastStableTiltX = 0f
            var lastStableTime = System.currentTimeMillis()


            fun autoCalibrate() {
                initialTiltZ = lastStableTiltZ
                initialTiltX = lastStableTiltX
                velocityX = 0f
            }


            if (!isGameOver) {
                lastStableTiltZ = gyroscopeHandler.filteredTiltZ
                lastStableTiltX = gyroscopeHandler.tiltX
                autoCalibrate()
            }

            while (!isGameOver) {

                val tiltFromRotation = gyroscopeHandler.filteredTiltZ
                val tiltFromInclination = gyroscopeHandler.tiltX


                val isStable = abs(tiltFromRotation - lastStableTiltZ) < 0.05f &&
                        abs(tiltFromInclination - lastStableTiltX) < 0.05f

                if (isStable) {
                    val currentTime = System.currentTimeMillis()

                    if (currentTime - lastStableTime > 2000) {
                        lastStableTiltZ = tiltFromRotation
                        lastStableTiltX = tiltFromInclination
                        autoCalibrate()
                    }
                } else {
                    lastStableTime = System.currentTimeMillis()
                }


                val relativeTiltZ = -(tiltFromRotation - initialTiltZ) * 0.6f
                val relativeTiltX = -(tiltFromInclination - initialTiltX) * 0.6f
                val combinedTilt = relativeTiltZ * 0.3f + relativeTiltX * 0.3f


                if (abs(combinedTilt) < 0.05f) {
                    velocityX *= 0.7f
                } else {
                    velocityX += combinedTilt
                }

                if (abs(velocityX) < 0.05f) velocityX = 0f


                velocityX = velocityX.coerceIn(-15f, 15f)


                shipX += velocityX


                if (shipX < 0f) {
                    shipX += 1000f
                } else if (shipX > 1000f) {
                    shipX -= 1000f
                }
                delay(16)
            }


            if (isGameOver) {
                velocityX = 0f
                shipX = 500f
            }
        }
        // Проверка столкновений--------------------------------------------------------------------------------------------------------------------
        LaunchedEffect(isGameOver) {

            while (!isGameOver && !isExploding) {

                val collidedMeteors = mutableListOf<Meteor>()

                meteors.filter {
                    abs(it.position.x - shipX) < 200 && abs(it.position.y - shipY) < 200
                }.forEach { meteor ->
                    val distance = sqrt(
                        (meteor.position.x - shipX).pow(2) +
                                (meteor.position.y - shipY).pow(2)
                    )
                    if (distance < meteor.radius + 50f && !isInvincible) {

                        val meteorSizeOffset = when (meteor.type) {
                            MeteorType.SMALL -> 48f
                            MeteorType.MEDIUM -> 96f
                            MeteorType.BIG -> 144f
                            else -> 48f
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

                if (fuel <= 0) {
                    isGameOver = true
                    soundManager.playSound("explosion")
                }

                delay(16)
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
        // ИНТЕРФЕЙС------------------------------------------------------------------------------------------------------
        Box(modifier = Modifier.fillMaxSize()) {
            FuelAndBoostIndicators(
                fuel = fuel,
                superFuel = superFuel
            )



            Canvas(modifier = Modifier.fillMaxSize()) {



                for (i in 0 until lives) {
                    drawImage(
                        image = heartImage,
                        topLeft = Offset(
                            20f + i * 70f,
                            20f
                        )
                    )
                }



                meteors.filter { it.position.y in 0f..size.height }.forEach { meteor ->
                    val meteorCenter = Offset(meteor.position.x, meteor.position.y)
                    meteor.rotation = (meteor.rotation + meteor.rotationSpeed) % 360
                    when (meteor.type) {
                        MeteorType.SMALL -> {
                            rotate(degrees = meteor.rotation, pivot = meteorCenter) {
                                drawImage(
                                    image = meteorSprite,
                                    topLeft = Offset(
                                        meteor.position.x - 48,
                                        meteor.position.y - 48
                                    )
                                )
                            }
                        }

                        MeteorType.MEDIUM -> {
                            rotate(degrees = meteor.rotation, pivot = meteorCenter) {
                                drawImage(
                                    image = mediumMeteorSprite,
                                    topLeft = Offset(
                                        meteor.position.x - 96,
                                        meteor.position.y - 96
                                    )
                                )
                            }
                        }

                        MeteorType.BIG -> {
                            rotate(degrees = meteor.rotation, pivot = meteorCenter) {
                                drawImage(
                                    image = bigMeteorSprite,
                                    topLeft = Offset(
                                        meteor.position.x - 144,
                                        meteor.position.y - 144
                                    )
                                )
                            }
                        }

                        MeteorType.HEART -> {
                            drawImage(
                                image = heartImage,
                                topLeft = Offset(
                                    meteor.position.x - 45f,
                                    meteor.position.y - 45f
                                )
                            )
                        }

                        MeteorType.SUPERFUEL -> {
                            drawImage(
                                image = superfuelImage,
                                topLeft = Offset(
                                    meteor.position.x - 45f,
                                    meteor.position.y - 45f
                                )
                            )
                        }

                        MeteorType.FUEL -> {
                            drawImage(
                                image = fuelImage,
                                topLeft = Offset(
                                    meteor.position.x - 45f,
                                    meteor.position.y - 45f
                                )
                            )
                        }

                    }
                }
            }
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
                        text = "Tap anywhere for BOOST",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFDFDBA),
                            fontFamily = FontFamily(Font(R.font.pixel))
                        ),
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(bottom = 50.dp)
                    )
                }
            }


            RocketAnimation(
                lives = lives,
                shipX = shipX,
                shipY = shipY,
                scaleFactor = 2.4f
            )



            Text(
                text = "Score: $score",
                style = TextStyle(
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = pixelFont
                ),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(20.dp)
            )


            if (isGameOver) {
                if (score > highScore) {
                    saveHighScore(score)
                }

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {

                        Text(
                            text = "GAME OVER",
                            style = TextStyle(
                                fontSize = 35.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily(Font(R.font.pixel)),
                                color = Color.Red
                            ),
                            modifier = Modifier
                                .padding(bottom = 16.dp)
                                .fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(32.dp))


                        Text(
                            text = "Your Score:",
                            style = TextStyle(
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Medium,
                                fontFamily = FontFamily(Font(R.font.pixel)),
                                color = Color.White,
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier
                                .padding(bottom = 8.dp)
                                .fillMaxWidth()
                        )
                        Text(
                            text = "$score",
                            style = TextStyle(
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Medium,
                                fontFamily = FontFamily(Font(R.font.pixel)),
                                color = Color.White,
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier
                                .padding(bottom = 32.dp)
                                .fillMaxWidth()
                        )
                        Text(
                            text = "High Score: $currentHighScore",
                            style = TextStyle(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            ),
                            modifier = Modifier.padding(16.dp)
                        )


                        Spacer(modifier = Modifier.height(32.dp))


                        StaticButton(
                            text = "REPLAY",
                            onClick = {

                                isGameOver = false
                                lives = 3
                                score = 0
                                fuel = 100f
                                shipSpeed = 0f
                                superFuel = 0f
                                shipX = 500f
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
                            modifier = Modifier.fillMaxWidth(0.6f)
                        )

                        Spacer(modifier = Modifier.height(16.dp))


                        StaticButton(
                            text = "HOME",
                            onClick = onExitToMenu,
                            modifier = Modifier.fillMaxWidth(0.6f)
                        )
                    }
                }
            }

        }
    }






