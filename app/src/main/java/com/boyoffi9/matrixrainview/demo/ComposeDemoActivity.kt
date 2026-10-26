package com.boyoffi9.matrixrainview.demo

import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.setProgress
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.boyoffi9.matrixrainview.MatrixRainView
import com.boyoffi9.matrixrainview.compose.MatrixRain
import kotlinx.coroutines.delay
import java.util.Locale

private val TermPanel = Color(0xF0030804)
private val TermText = Color(0xFFD6EBDD)
private val TermDim = Color(0xFF7A9483)
private val TermLine = Color(0xFF24402B)

private val swatches = listOf(
    Color(0xFF00FF41),
    Color(0xFF00E5FF),
    Color(0xFFFFB300),
    Color(0xFFFF3B30),
    Color(0xFFB347FF),
    Color(0xFFFFFFFF),
)

private val glyphSets = listOf(
    "KATAKANA" to MatrixRainView.CharSet.KATAKANA,
    "BINARY" to MatrixRainView.CharSet.BINARY,
    "ALNUM" to MatrixRainView.CharSet.ALNUM,
)

private val tabNames = listOf("LOOK", "TOUCH", "FX")

/**
 * Compose counterpart to [MainActivity]: the same console sheet (LOOK / TOUCH /
 * FX) driving the [MatrixRain] composable instead of the XML view.
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
    var accent by remember { mutableStateOf(swatches[0]) }
    var speed by remember { mutableFloatStateOf(1.2f) }
    var density by remember { mutableFloatStateOf(1.0f) }
    var fade by remember { mutableFloatStateOf(28f) }
    var glow by remember { mutableStateOf(true) }
    var glyphs by remember { mutableIntStateOf(0) }
    var avoid by remember { mutableStateOf(true) }
    var flicker by remember { mutableStateOf(true) }
    var shockwave by remember { mutableStateOf(true) }
    var touchGlitch by remember { mutableStateOf(false) }
    var radiusDp by remember { mutableFloatStateOf(96f) }
    var glitch by remember { mutableFloatStateOf(0f) }
    var transparent by remember { mutableStateOf(false) }
    var tab by remember { mutableIntStateOf(0) }
    var sheetOpen by remember { mutableStateOf(true) }

    val screenDensity = LocalDensity.current.density
    var touchFlags = 0
    if (avoid) touchFlags = touchFlags or MatrixRainView.TOUCH_AVOID
    if (flicker) touchFlags = touchFlags or MatrixRainView.TOUCH_FLICKER
    if (shockwave) touchFlags = touchFlags or MatrixRainView.TOUCH_SHOCKWAVE
    if (touchGlitch) touchFlags = touchFlags or MatrixRainView.TOUCH_GLITCH

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
            rainColor = accent,
            speed = speed.coerceAtLeast(0.1f),
            density = density.coerceAtLeast(0.1f),
            glowEnabled = glow,
            fadeStrength = fade.toInt().coerceAtLeast(8),
            charSet = glyphSets[glyphs].second,
            touchEffects = touchFlags,
            touchRadiusPx = radiusDp * screenDensity,
            glitch = glitch,
            transparent = transparent,
        )

        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "> touch the rain (compose)",
                    style = termStyle(TermDim, 12),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(16.dp)
                )
                Text(
                    text = "Wake up, Neo...",
                    style = termStyle(accent, 22, glow = true),
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            ConsoleSheet(
                accent = accent,
                open = sheetOpen,
                onToggleOpen = { sheetOpen = !sheetOpen },
                tab = tab,
                onTab = { tab = it },
            ) {
                when (tab) {
                    0 -> LookTab(
                        accent = accent,
                        onAccent = { accent = it },
                        glyphs = glyphs,
                        onGlyphs = { glyphs = it },
                        speed = speed,
                        onSpeed = { speed = it },
                        density = density,
                        onDensity = { density = it },
                        fade = fade,
                        onFade = { fade = it },
                        glow = glow,
                        onGlow = { glow = it },
                    )
                    1 -> TouchTab(
                        accent = accent,
                        avoid = avoid, onAvoid = { avoid = it },
                        flicker = flicker, onFlicker = { flicker = it },
                        shockwave = shockwave, onShockwave = { shockwave = it },
                        touchGlitch = touchGlitch, onTouchGlitch = { touchGlitch = it },
                        radiusDp = radiusDp, onRadius = { radiusDp = it },
                    )
                    else -> FxTab(
                        accent = accent,
                        glitch = glitch, onGlitch = { glitch = it },
                        transparent = transparent, onTransparent = { transparent = it },
                    )
                }
            }
        }
    }
}

// ---- console sheet ------------------------------------------------------

@Composable
private fun ConsoleSheet(
    accent: Color,
    open: Boolean,
    onToggleOpen: () -> Unit,
    tab: Int,
    onTab: (Int) -> Unit,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    // A system-wide "remove animations" setting keeps the cursor solid.
    val canBlink = remember {
        Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) != 0f
    }
    var cursorOn by remember { mutableStateOf(true) }
    LaunchedEffect(canBlink) {
        while (canBlink) {
            delay(530)
            cursorOn = !cursorOn
        }
    }

    val scroll = rememberScrollState()
    LaunchedEffect(tab) { scroll.scrollTo(0) }

    val paneHeight = (LocalConfiguration.current.screenHeightDp * 0.36f).coerceAtLeast(180f).dp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .background(TermPanel)
            .border(1.dp, accent.copy(alpha = 0.4f))
            // Swallow touches so the rain underneath doesn't react to the sheet.
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {},
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clickable(onClick = onToggleOpen)
                .padding(horizontal = 16.dp)
        ) {
            Text("matrix@rain:~$ config", style = termStyle(accent, 13, glow = true))
            Text(
                text = "█",
                style = termStyle(accent, 13, glow = true),
                modifier = Modifier
                    .padding(start = 4.dp)
                    .alpha(if (cursorOn) 1f else 0f)
            )
            Spacer(Modifier.weight(1f))
            Text(if (open) "[ - ]" else "[ + ]", style = termStyle(accent, 13, glow = true))
        }

        if (open) {
            Segments(
                names = tabNames,
                selected = tab,
                accent = accent,
                onSelect = onTab,
                modifier = Modifier.padding(horizontal = 8.dp),
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(TermLine)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(paneHeight)
                    .verticalScroll(scroll)
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 12.dp)
            ) {
                content()
            }
        }
    }
}

// ---- tabs ---------------------------------------------------------------

@Composable
private fun LookTab(
    accent: Color,
    onAccent: (Color) -> Unit,
    glyphs: Int,
    onGlyphs: (Int) -> Unit,
    speed: Float,
    onSpeed: (Float) -> Unit,
    density: Float,
    onDensity: (Float) -> Unit,
    fade: Float,
    onFade: (Float) -> Unit,
    glow: Boolean,
    onGlow: (Boolean) -> Unit,
) {
    Label("[ COLOR ]", modifier = Modifier.padding(top = 12.dp))
    Row(modifier = Modifier.padding(top = 8.dp)) {
        swatches.forEach { color ->
            Box(
                modifier = Modifier
                    .padding(end = 10.dp)
                    .size(36.dp)
                    .alpha(if (color == accent) 1f else 0.4f)
                    .background(color)
                    .clickable { onAccent(color) }
            )
        }
    }

    Label("[ GLYPHS ]", modifier = Modifier.padding(top = 14.dp))
    Segments(
        names = glyphSets.map { it.first },
        selected = glyphs,
        accent = accent,
        onSelect = onGlyphs,
    )

    ParamRow("[ SPEED ]", String.format(Locale.US, "%.1fx", speed), accent)
    TermSlider(speed, onSpeed, 0.1f..3.0f, accent)

    ParamRow("[ DENSITY ]", String.format(Locale.US, "%.1fx", density), accent)
    TermSlider(density, onDensity, 0.1f..2.0f, accent)

    ParamRow("[ TRAIL FADE ]", fade.toInt().coerceAtLeast(8).toString(), accent)
    TermSlider(fade, onFade, 8f..60f, accent)

    TermCheck("Glow", glow, accent, onGlow, modifier = Modifier.padding(top = 6.dp))
}

@Composable
private fun TouchTab(
    accent: Color,
    avoid: Boolean, onAvoid: (Boolean) -> Unit,
    flicker: Boolean, onFlicker: (Boolean) -> Unit,
    shockwave: Boolean, onShockwave: (Boolean) -> Unit,
    touchGlitch: Boolean, onTouchGlitch: (Boolean) -> Unit,
    radiusDp: Float, onRadius: (Float) -> Unit,
) {
    Spacer(Modifier.height(6.dp))
    TermCheck("Flow around finger", avoid, accent, onAvoid)
    TermCheck("Flicker under finger", flicker, accent, onFlicker)
    TermCheck("Shockwave on tap", shockwave, accent, onShockwave)
    TermCheck("Glitch while held", touchGlitch, accent, onTouchGlitch)

    ParamRow("[ RADIUS ]", "${radiusDp.toInt()}dp", accent)
    TermSlider(radiusDp, onRadius, 40f..200f, accent)
}

@Composable
private fun FxTab(
    accent: Color,
    glitch: Float, onGlitch: (Float) -> Unit,
    transparent: Boolean, onTransparent: (Boolean) -> Unit,
) {
    ParamRow("[ GLITCH ]", String.format(Locale.US, "%.2f", glitch), accent)
    TermSlider(glitch, onGlitch, 0f..1f, accent)

    TermCheck("Transparent background", transparent, accent, onTransparent, modifier = Modifier.padding(top = 8.dp))
    Text(
        text = "The rain draws over a gradient instead of black.",
        style = termStyle(TermDim, 12),
        modifier = Modifier.padding(top = 6.dp)
    )
}

// ---- terminal controls --------------------------------------------------

private fun termStyle(color: Color, size: Int, glow: Boolean = false) = TextStyle(
    color = color,
    fontSize = size.sp,
    fontFamily = FontFamily.Monospace,
    shadow = if (glow) Shadow(color = color, offset = Offset.Zero, blurRadius = 8f) else null,
)

@Composable
private fun Label(text: String, modifier: Modifier = Modifier) {
    Text(text, style = termStyle(TermDim, 12), modifier = modifier)
}

@Composable
private fun ParamRow(label: String, value: String, accent: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Label(label)
        Text(value, style = termStyle(accent, 12, glow = true))
    }
}

/** A row of text choices; the selected one is bracketed and lit, e.g. `[ LOOK ]  TOUCH   FX`. */
@Composable
private fun Segments(
    names: List<String>,
    selected: Int,
    accent: Color,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier.fillMaxWidth()) {
        names.forEachIndexed { index, name ->
            val on = index == selected
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .clickable(role = Role.Tab) { onSelect(index) }
            ) {
                Text(
                    text = if (on) "[ $name ]" else "  $name  ",
                    style = termStyle(if (on) accent else TermDim, 12, glow = on),
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    softWrap = false,
                )
            }
        }
    }
}

@Composable
private fun TermCheck(
    label: String,
    checked: Boolean,
    accent: Color,
    onChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 44.dp)
            .toggleable(value = checked, role = Role.Checkbox, onValueChange = onChange)
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .border(1.dp, accent)
                .padding(4.dp)
        ) {
            if (checked) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(accent)
                )
            }
        }
        Spacer(Modifier.width(12.dp))
        Text(label, style = termStyle(TermText, 13))
    }
}

/** Thin track with a block cursor for a thumb, matching the XML demo's seek bars. */
@Composable
private fun TermSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    range: ClosedFloatingPointRange<Float>,
    accent: Color,
) {
    val latest by rememberUpdatedState(onValueChange)
    val span = range.endInclusive - range.start
    val fraction = ((value - range.start) / span).coerceIn(0f, 1f)

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .semantics {
                progressBarRangeInfo = ProgressBarRangeInfo(value, range)
                setProgress { target ->
                    latest(target.coerceIn(range))
                    true
                }
            }
            .pointerInput(range) {
                val half = 5.dp.toPx()
                fun update(x: Float) {
                    val usable = (size.width - 2 * half).coerceAtLeast(1f)
                    val f = ((x - half) / usable).coerceIn(0f, 1f)
                    latest(range.start + f * span)
                }
                awaitEachGesture {
                    val down = awaitFirstDown()
                    update(down.position.x)
                    drag(down.id) { change ->
                        update(change.position.x)
                        change.consume()
                    }
                }
            }
    ) {
        val half = 5.dp.toPx()
        val cy = size.height / 2f
        val x = half + fraction * (size.width - 2 * half)
        drawLine(TermLine, Offset(half, cy), Offset(size.width - half, cy), strokeWidth = 2.dp.toPx())
        drawLine(accent, Offset(half, cy), Offset(x, cy), strokeWidth = 2.dp.toPx())
        drawRect(accent, Offset(x - half, cy - 9.dp.toPx()), Size(10.dp.toPx(), 18.dp.toPx()))
    }
}
