package com.boyoffi9.matrixrainview.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.boyoffi9.matrixrainview.MatrixRainView
import com.boyoffi9.matrixrainview.compose.MatrixRain

private val swatches = listOf(
    "Green" to Color(0xFF00FF41),
    "Cyan" to Color(0xFF00E5FF),
    "Amber" to Color(0xFFFFB300),
    "Red" to Color(0xFFFF3B30),
    "Purple" to Color(0xFFB347FF),
    "White" to Color(0xFFFFFFFF),
)

/**
 * Compose counterpart to [MainActivity] — color, speed, density, glow, touch
 * effects, glitch and transparent mode, driving [MatrixRain] instead of the
 * XML view directly.
 */
class ComposeDemoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeDemoScreen()
        }
    }
}

@Composable
private fun ComposeDemoScreen() {
    var rainColor by remember { mutableStateOf(swatches[0].second) }
    var speed by remember { mutableFloatStateOf(1.2f) }
    var density by remember { mutableFloatStateOf(1.0f) }
    var glowEnabled by remember { mutableStateOf(true) }
    var touchEnabled by remember { mutableStateOf(true) }
    var glitch by remember { mutableFloatStateOf(0f) }
    var transparent by remember { mutableStateOf(false) }
    var panelVisible by remember { mutableStateOf(false) }

    // Transparent rain needs something behind it to show through.
    val backdrop = if (transparent) {
        Brush.verticalGradient(listOf(Color(0xFF0A1F44), Color(0xFF3A0CA3)))
    } else {
        SolidColor(Color.Black)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backdrop)
    ) {
        MatrixRain(
            modifier = Modifier.fillMaxSize(),
            rainColor = rainColor,
            speed = speed,
            density = density,
            glowEnabled = glowEnabled,
            fadeStrength = 28,
            touchEffects = if (touchEnabled) {
                MatrixRainView.TOUCH_AVOID or MatrixRainView.TOUCH_FLICKER or MatrixRainView.TOUCH_SHOCKWAVE
            } else {
                0
            },
            glitch = glitch,
            transparent = transparent,
        )

        Text(
            text = "Wake up, Neo...",
            color = rainColor,
            fontSize = 22.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.align(Alignment.Center)
        )

        FloatingActionButton(
            onClick = { panelVisible = !panelVisible },
            containerColor = Color.Black,
            contentColor = rainColor,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Text("⚙", fontSize = 20.sp)
        }

        if (panelVisible) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xCC101010)),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .padding(bottom = 80.dp)
                    .width(260.dp)
            ) {
                Column(
                    modifier = Modifier
                        .heightIn(max = 360.dp)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    Text("Color", color = Color.White, fontSize = 14.sp)
                    Row(modifier = Modifier.padding(vertical = 8.dp)) {
                        swatches.forEach { (_, color) ->
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .padding(2.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .then(
                                        Modifier.clickable { rainColor = color }
                                    )
                            )
                        }
                    }

                    Text("Speed", color = Color.White, fontSize = 14.sp)
                    Slider(
                        value = speed,
                        onValueChange = { speed = it },
                        valueRange = 0.1f..3.0f,
                    )

                    Text("Density", color = Color.White, fontSize = 14.sp)
                    Slider(
                        value = density,
                        onValueChange = { density = it },
                        valueRange = 0.1f..2.0f,
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Glow", color = Color.White, fontSize = 14.sp, modifier = Modifier.weight(1f))
                        Switch(checked = glowEnabled, onCheckedChange = { glowEnabled = it })
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Touch effects", color = Color.White, fontSize = 14.sp, modifier = Modifier.weight(1f))
                        Switch(checked = touchEnabled, onCheckedChange = { touchEnabled = it })
                    }

                    Text("Glitch", color = Color.White, fontSize = 14.sp)
                    Slider(
                        value = glitch,
                        onValueChange = { glitch = it },
                        valueRange = 0f..1f,
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Transparent", color = Color.White, fontSize = 14.sp, modifier = Modifier.weight(1f))
                        Switch(checked = transparent, onCheckedChange = { transparent = it })
                    }
                }
            }
        }
    }
}
