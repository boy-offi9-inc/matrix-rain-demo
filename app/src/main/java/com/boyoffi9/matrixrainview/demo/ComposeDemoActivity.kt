package com.boyoffi9.matrixrainview.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
 * Compose counterpart to [MainActivity] — same set of controls (color, speed,
 * density, glow), driving [MatrixRain] instead of the XML view directly.
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
    var panelVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        MatrixRain(
            modifier = Modifier.fillMaxSize(),
            rainColor = rainColor,
            speed = speed,
            density = density,
            glowEnabled = glowEnabled,
            fadeStrength = 28,
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
                Column(modifier = Modifier.padding(16.dp)) {
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
                }
            }
        }
    }
}
