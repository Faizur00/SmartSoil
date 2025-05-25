package com.example.projects

import android.health.connect.datatypes.units.Temperature
import android.os.Bundle
import android.text.style.AlignmentSpan
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.projects.ui.theme.ProjectsTheme
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.min
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.delay
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.google.firebase.database.*
import androidx.lifecycle.viewmodel.compose.viewModel



class MainViewModel : ViewModel() {

    private val database = FirebaseDatabase.getInstance()
    private val humidityRef = database.getReference("Sensor/humidity")
    private val percentSoilSensorDataRef = database.getReference("Sensor/percent_soil_sensor_data")
    private val rawSoilSensorDataRef = database.getReference("Sensor/raw_soil_sensor_data")
    private val temperatureRef = database.getReference("Sensor/temperature")

    var humidity = mutableStateOf(0.0)
        private set

    var percentSoil = mutableStateOf(0.0)
        private set

    var rawSoil = mutableStateOf(0.0)
        private set

    var temperature = mutableStateOf(0.0)
        private set

    init {
        humidityRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                humidity.value = snapshot.getValue(Double::class.java) ?: 0.0
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        percentSoilSensorDataRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                percentSoil.value = snapshot.getValue(Double::class.java) ?: 0.0
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        rawSoilSensorDataRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                rawSoil.value = snapshot.getValue(Double::class.java) ?: 0.0
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        temperatureRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                temperature.value = snapshot.getValue(Double::class.java) ?: 0.0
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            ProjectsTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainLayout(
                        modifier = Modifier
                            .padding(innerPadding) // Padding applied to LazyColumn, not items
                    )
                }
            }
        }
    }
}

@Composable
fun MainLayout(
    viewModel: MainViewModel = viewModel(), modifier: Modifier = Modifier) {

    // Ambil state dari ViewModel
    val humidity by viewModel.humidity
    val percentSoil by viewModel.percentSoil
    val rawSoil by viewModel.rawSoil
    val temperature by viewModel.temperature

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding()
            .background(color = Color.White),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(8) { index ->
            when (index) {
                0 -> Spacer(modifier = Modifier.height(15.dp))
                1 -> Text(
                    text = "SmartSoil",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif
                )
                2 -> MainIndicator(percentSoil.toInt(), Modifier)
                3 -> RawSoilIndicator(rawSoil.toInt(), Modifier)
                4 -> SoilMoistIndicator(percentSoil.toInt(), Modifier)
                5 -> MoistIndicator(humidity.toInt(), Modifier)
                6 -> FirstTemperatureIndicator(temperature.toInt(), Modifier)
            }
        }
    }
}



@Composable
fun MainIndicator(value: Int, modifier: Modifier) {
    Box(
        modifier = Modifier
            .clip(shape = RoundedCornerShape(16.dp))
            .background(color = Color(0xFFFDF7F4))
            .fillMaxWidth(0.9f)
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        when (value) {
            in 0..20 -> EmojiVeryDryDarkCanvas()
            in 21..35 -> EmojiVeryDryOrangeCanvas()
            in 36..50 -> EmojiNeutralYellowCanvas()
            in 51..75 -> EmojiMoistLightGreenCanvas()
            in 76..100 -> EmojiHappyPlantCanvas()
            else -> EmojiErrorCanvas()
        }
    }
}

@Composable
fun RawSoilIndicator(value: Int, modifier: Modifier) {
    Box(
        modifier = Modifier
            .clip(shape = RoundedCornerShape(16.dp))
            .background(color = Color(0xFFFDF7F4))
            .fillMaxWidth(0.9f)
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        HorizontalBarIndicator(value, Modifier)
    }
}

@Composable
fun SoilMoistIndicator(value: Int, modifier: Modifier) {
    Box (
        Modifier
            .clip(shape = RoundedCornerShape(16.dp))
            .background(color = Color(0xFFFDF7F4))
            .fillMaxWidth(0.9f)
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        SoilMoistBar(value, Modifier)
    }
}

@Composable
fun MoistIndicator(value: Int, modifier: Modifier) {
    Box (
        Modifier
            .clip(shape = RoundedCornerShape(16.dp))
            .background(color = Color(0xFFFDF7F4))
            .fillMaxWidth(0.9f)
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Column {
            MoistBar(value, Modifier)
        }
    }
}

@Composable
fun FirstTemperatureIndicator(value: Int, modifier: Modifier) {
    Box (
        Modifier
            .clip(shape = RoundedCornerShape(16.dp))
            .background(color = Color(0xFFFDF7F4))
            .fillMaxWidth(0.9f)
            .height(200.dp),
        contentAlignment = Alignment.Center
    ){
        TemperatureBar(value, Modifier)
    }
}



@Composable
fun HorizontalBarIndicator(value: Int, modifier: Modifier) {
    val minValue = 1500f
    val maxValue = 4095f
    val rangedValue = value.coerceIn(minValue.toInt(), maxValue.toInt())
    val percentage = ((rangedValue - minValue) / (maxValue - minValue)).coerceIn(0f, 1f)

    val animatedPercentage by animateFloatAsState(
        targetValue = percentage,
        animationSpec = tween(durationMillis = 600)
    )

    Column(modifier = Modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Raw Soil Indicator",
            fontSize = 20.sp,
            color = Color(0xFF4E1F00),
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.SansSerif
        )
        Spacer(Modifier.height(8.dp))
        Box(
            modifier = modifier
                .fillMaxWidth(0.9f)
                .height(25.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(color = Color(0x804E1F00)),
            contentAlignment = Alignment.CenterStart
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animatedPercentage)
                    .background(Color(0xFF4E1F00))
            )
            Text(
                text = "${(animatedPercentage * 100).toInt()}%",
                color = Color.White,
                modifier = Modifier.align(Alignment.Center),
                fontWeight = FontWeight.SemiBold
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(0.9f),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                color = Color(0xFF4E1F00),
                text = minValue.toInt().toString(),
                fontWeight = FontWeight.Medium
            )
            Text(
                color = Color(0xFF4E1F00),
                text = maxValue.toInt().toString(),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun SoilMoistBar(value: Int, modifier: Modifier){
    val minValue: Int = 0
    val maxValue: Int = 100

    val rangedValue: Int = value.coerceIn(minValue, maxValue)
    val percentage: Float = ((rangedValue - minValue).toFloat() / (maxValue - minValue))

    val animatedPercentage by animateFloatAsState(
        targetValue = percentage,
        animationSpec = tween(durationMillis = 600)
    )

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Soil Moisture",
            fontWeight = FontWeight.Light,
            fontSize = 20.sp,
            color = Color(0xFF4E1F00)
        )
        Spacer(Modifier.height(16.dp))
        Box(
            contentAlignment = Alignment.Center
        ){
            Canvas(
                modifier = Modifier
                    .width(150.dp)
                    .height(200.dp)
            ){
                drawArc(
                    color = Color(0x804E1F00),
                    startAngle = -180f,
                    sweepAngle = 180f,
                    useCenter = false,
                    style = Stroke(width = 53f, cap = StrokeCap.Round)
                )

                drawArc(
                    color = Color(0xFF4E1F00),
                    startAngle = -180f,
                    sweepAngle = 180f *animatedPercentage,
                    useCenter = false,
                    style = Stroke(width = 50f, cap = StrokeCap.Round)
                )
            }
            Text(
                text = "${(animatedPercentage * 100).toInt()}%",
                fontWeight = FontWeight.Bold,
                fontSize = 40.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun MoistBar(value: Int, modifier: Modifier){
    val minValue: Int = 0
    val maxValue: Int = 100

    val rangedValue: Int = value.coerceIn(minValue, maxValue)
    val percentage: Float = (rangedValue - minValue).toFloat() / (maxValue - minValue).toFloat()

    val animatedPercentage by animateFloatAsState(
        targetValue = percentage,
        animationSpec = tween(
            durationMillis = 600
        )
    )

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Moisture",
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp,
            color = Color(0xFF4E1F00)
        )
        Spacer(Modifier.height(16.dp))
        Box(
            contentAlignment = Alignment.Center
        ){
            Canvas(
                modifier = Modifier
                    .width(150.dp)
                    .height(200.dp)
            ){
                drawArc(
                    color = Color(0x804E1F00),
                    startAngle = -180f,
                    sweepAngle = 180f,
                    useCenter = false,
                    style = Stroke(width = 53f, cap = StrokeCap.Round)
                )

                drawArc(
                    color = Color(0xFF4E1F00),
                    startAngle = -180f,
                    sweepAngle = 180f * animatedPercentage,
                    useCenter = false,
                    style = Stroke(width = 50f, cap = StrokeCap.Round)
                )
            }
            Text(
                text = "${(animatedPercentage * 100).toInt()}%",
                fontWeight = FontWeight.Bold,
                fontSize = 40.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun TemperatureBar(value: Int, modifier: Modifier = Modifier) {
    val minValue = 0
    val maxValue = 50

    val rangedValue = value.coerceIn(minValue, maxValue)
    val percentage = ((rangedValue - minValue).toFloat() / (maxValue - minValue).toFloat())
    val animatedPercentage by animateFloatAsState(
        targetValue = percentage,
        animationSpec = tween(600)
    )

    // Needle angle: maps 0–50 to -180° to 0°
    val needleAngle = -90f + (180f * animatedPercentage)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Temperature",
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp,
            color = Color(0xFF4E1F00)
        )
        Spacer(Modifier.height(16.dp))
        Canvas(
            modifier = Modifier
                .width(150.dp)
                .height(200.dp)
        ) {

            // Filled arc
            drawArc(
                color = Color(0xFF4E1F00),
                startAngle = -180f,
                sweepAngle = 180f,
                useCenter = false,
                style = Stroke(width = 53f, cap = StrokeCap.Square)
            )

            // Draw needle
            val centerX = size.width / 2
            val baseY = size.height * 0.52f // Rotate from bottom center
            val tipOffset = size.height * 0.52f
            val curveWidth = 70f
            val baseOffset = 1f // roundness of base

            rotate(needleAngle, pivot = Offset(centerX, baseY)) {
                val path = Path().apply {
                    moveTo(centerX, baseY) // base center

                    // Right side curve up to tip
                    quadraticBezierTo(
                        centerX + curveWidth, baseY - baseOffset,
                        centerX, baseY - tipOffset
                    )

                    // Left side curve back to base
                    quadraticBezierTo(
                        centerX - curveWidth, baseY - baseOffset,
                        centerX, baseY
                    )
                }
                drawPath(path, color = Color.LightGray)
            }
        }
    }
}




// this below is for make the damn emoticon
data class Mood(
    val faceColor: Color,
    val expression: Expression
)

enum class Expression {
    Happy, Neutral, Sad, XMark
}


@Composable
fun FaceCanvas(mood: Mood, modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier
            .size(100.dp)
            .padding(8.dp)
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val centerX = canvasWidth / 2
        val centerY = canvasHeight / 2

        // Face circle
        drawCircle(
            color = mood.faceColor.copy(alpha = 0.2f),
            radius = centerX,
            center = Offset(centerX, centerY)
        )
        drawCircle(
            color = mood.faceColor,
            radius = centerX - 4.dp.toPx(),
            center = Offset(centerX, centerY),
            style = Stroke(width = 4.dp.toPx())
        )

        // Eyes
        val eyeOffsetX = 20.dp.toPx()
        val eyeOffsetY = 20.dp.toPx()
        val eyeRadius = 5.dp.toPx()

        // Mouth
        val mouthWidth = 40.dp.toPx()
        val mouthHeight = 20.dp.toPx()
        val startX = centerX - mouthWidth / 2
        val endX = centerX + mouthWidth / 2
        val mouthY = centerY + 20.dp.toPx()

        val path = Path().apply {
            moveTo(startX, mouthY)
            if (mood.expression != Expression.XMark) {
                drawCircle(mood.faceColor, eyeRadius, Offset(centerX - eyeOffsetX, centerY - eyeOffsetY))
                drawCircle(mood.faceColor, eyeRadius, Offset(centerX + eyeOffsetX, centerY - eyeOffsetY))
            }
            when (mood.expression) {
                Expression.Happy -> quadraticBezierTo(centerX, mouthY + mouthHeight, endX, mouthY)
                Expression.Neutral -> lineTo(endX, mouthY)
                Expression.Sad -> quadraticBezierTo(centerX, mouthY - mouthHeight, endX, mouthY)
                Expression.XMark -> {
                    // Draw "X" mouth
                    moveTo(centerX - 10.dp.toPx(), centerY - 10.dp.toPx())
                    lineTo(centerX + 10.dp.toPx(), centerY + 10.dp.toPx())
                    moveTo(centerX - 10.dp.toPx(), centerY + 10.dp.toPx())
                    lineTo(centerX + 10.dp.toPx(), centerY - 10.dp.toPx())
                }
            }
        }
        drawPath(path, mood.faceColor, style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round))
    }
}

@Composable
fun EmojiVeryDryDarkCanvas(modifier: Modifier = Modifier) {
    Row(
        modifier = Modifier,
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        FaceCanvas(
            mood = Mood(Color(0xFF4E1F00), Expression.Sad),
            modifier = modifier
        )
        Text(
            text = "Gersang Wak, tambahin air!!!",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF4E1F00)
        )
    }
}

@Composable
fun EmojiVeryDryOrangeCanvas(modifier: Modifier = Modifier) {
    Row(
        modifier = Modifier,
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        FaceCanvas(
            mood = Mood(Color(0xFFFF8000), Expression.Sad),
            modifier = modifier
        )
        Text(
            text = "Tanah Kering, Tambahkan Air",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFFFF8000)
        )
    }
}


@Composable
fun EmojiNeutralYellowCanvas(modifier: Modifier = Modifier) {
    Row(
        modifier = Modifier,
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        FaceCanvas(
            mood = Mood(Color(0xFFFFD700), Expression.Neutral),
            modifier = modifier
        )
        Text(
            text = "Tanah Mulai Kering, Tambahkan Air!",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFFFFD700)
        )
    }
}


@Composable
fun EmojiMoistLightGreenCanvas(modifier: Modifier = Modifier) {
    Row(
        modifier = Modifier,
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        FaceCanvas(
            mood = Mood(Color(0xFF85BB65), Expression.Happy),
            modifier = modifier
        )
        Text(
            text = "Tanah Cukup Lembab, Pantau Beberapa Saat Lagi",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF85BB65)
        )
    }
}


@Composable
fun EmojiHappyPlantCanvas(modifier: Modifier = Modifier) {
    Row(
        modifier = Modifier,
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        FaceCanvas(
            mood = Mood(Color(0xFF007F5C), Expression.Happy),
            modifier = modifier
        )
        Text(
            text = "Kelembapan Tanah Baik!",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF007F5C)
        )
    }
}


@Composable
fun EmojiErrorCanvas(modifier: Modifier = Modifier) {
    Row(
        modifier = Modifier,
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        FaceCanvas(
            mood = Mood(Color.Red, Expression.XMark),
            modifier = modifier
        )
        Text(
            text = "ERROR, DATA TERPUTUS!.  Coba Sambungkan Ulang",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Red
        )
    }
}





@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ProjectsTheme {
        MainLayout()
    }
}