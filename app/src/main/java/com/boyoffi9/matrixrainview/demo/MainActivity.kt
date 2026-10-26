package com.boyoffi9.matrixrainview.demo

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.transition.TransitionManager
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ScrollView
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.boyoffi9.matrixrainview.MatrixRainView
import java.util.Locale

/**
 * Demo Activity: every runtime-configurable property on MatrixRainView, driven
 * from a terminal-style console sheet (LOOK / TOUCH / FX). Picking a rain
 * color re-tints the whole console, so the UI always matches the rain.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var rain: MatrixRainView
    private lateinit var sheet: ViewGroup
    private lateinit var cursor: View

    private var accent = Color.parseColor("#00FF41")
    private val segmentGroups = mutableListOf<SegmentGroup>()
    private val swatches = mutableListOf<Pair<View, Int>>()
    private val handler = Handler(Looper.getMainLooper())
    private var cursorOn = true

    private val blink = object : Runnable {
        override fun run() {
            cursorOn = !cursorOn
            cursor.alpha = if (cursorOn) 1f else 0f
            handler.postDelayed(this, 530)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rain = findViewById(R.id.matrixRain)
        sheet = findViewById(R.id.sheet)
        cursor = findViewById(R.id.cursorText)
        sheet.tag = "outline"

        setupSheet()
        setupTabs()
        setupComposeDemoButton()
        setupColorSwatches()
        setupGlyphs()
        setupLookSliders()
        setupGlow()
        setupTouch()
        setupGlitch()
        setupTransparent()

        applyAccent(accent)
    }

    override fun onResume() {
        super.onResume()
        // A system-wide "remove animations" setting keeps the cursor solid.
        val scale = Settings.Global.getFloat(contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f)
        if (scale != 0f) handler.postDelayed(blink, 530)
    }

    override fun onPause() {
        handler.removeCallbacks(blink)
        cursor.alpha = 1f
        cursorOn = true
        super.onPause()
    }

    // ---- sheet ----------------------------------------------------------

    private fun setupSheet() {
        val body = findViewById<View>(R.id.sheetBody)
        val state = findViewById<TextView>(R.id.sheetState)
        val scroll = findViewById<ScrollView>(R.id.sheetScroll)

        // Keep the rain visible above the sheet: ~36% of the screen, never tiny.
        val minPx = (180 * resources.displayMetrics.density).toInt()
        scroll.layoutParams = scroll.layoutParams.apply {
            height = maxOf(minPx, (resources.displayMetrics.heightPixels * 0.36f).toInt())
        }

        findViewById<View>(R.id.sheetHeader).setOnClickListener {
            TransitionManager.beginDelayedTransition(sheet)
            val open = body.visibility != View.VISIBLE
            body.visibility = if (open) View.VISIBLE else View.GONE
            state.setText(if (open) R.string.sheet_collapse else R.string.sheet_expand)
        }
    }

    private fun setupTabs() {
        val scroll = findViewById<ScrollView>(R.id.sheetScroll)
        val panes = listOf<View>(
            findViewById(R.id.paneLook),
            findViewById(R.id.paneTouch),
            findViewById(R.id.paneFx),
        )
        segmentGroups += SegmentGroup(
            views = listOf(
                findViewById(R.id.tabLook),
                findViewById(R.id.tabTouch),
                findViewById(R.id.tabFx),
            ),
            labels = listOf(R.string.tab_look, R.string.tab_touch, R.string.tab_fx),
            selected = 0,
        ) { index ->
            panes.forEachIndexed { i, pane -> pane.visibility = if (i == index) View.VISIBLE else View.GONE }
            scroll.scrollTo(0, 0)
        }
    }

    private fun setupComposeDemoButton() {
        findViewById<View>(R.id.composeDemoButton).setOnClickListener {
            startActivity(Intent(this, ComposeDemoActivity::class.java))
        }
    }

    // ---- LOOK -----------------------------------------------------------

    private fun setupColorSwatches() {
        val options = listOf(
            R.id.swatchGreen to "#00FF41",
            R.id.swatchCyan to "#00E5FF",
            R.id.swatchAmber to "#FFB300",
            R.id.swatchRed to "#FF3B30",
            R.id.swatchPurple to "#B347FF",
            R.id.swatchWhite to "#FFFFFF",
        )
        options.forEach { (id, hex) ->
            val view = findViewById<View>(id)
            val color = Color.parseColor(hex)
            swatches += view to color
            view.setOnClickListener { applyAccent(color) }
        }
    }

    private fun setupGlyphs() {
        segmentGroups += SegmentGroup(
            views = listOf(
                findViewById(R.id.glyphKatakana),
                findViewById(R.id.glyphBinary),
                findViewById(R.id.glyphAlnum),
            ),
            labels = listOf(R.string.glyphs_katakana, R.string.glyphs_binary, R.string.glyphs_alnum),
            selected = 0,
        ) { index ->
            rain.charSet = when (index) {
                1 -> MatrixRainView.CharSet.BINARY
                2 -> MatrixRainView.CharSet.ALNUM
                else -> MatrixRainView.CharSet.KATAKANA
            }
        }
    }

    private fun setupLookSliders() {
        // progress 0-30 -> 0.1x - 3.0x
        bindSeek(R.id.speedSeek, R.id.speedValue, { "%.1fx".fmt(it.coerceAtLeast(1) / 10f) }) {
            rain.speed = it.coerceAtLeast(1) / 10f
        }
        // progress 0-20 -> 0.1x - 2.0x
        bindSeek(R.id.densitySeek, R.id.densityValue, { "%.1fx".fmt(it.coerceAtLeast(1) / 10f) }) {
            rain.density = it.coerceAtLeast(1) / 10f
        }
        // progress maps directly to fadeStrength, clamped so it never hits 0
        bindSeek(R.id.fadeSeek, R.id.fadeValue, { it.coerceAtLeast(8).toString() }) {
            rain.fadeStrength = it.coerceAtLeast(8)
        }
    }

    private fun setupGlow() {
        findViewById<CheckBox>(R.id.glowSwitch).setOnCheckedChangeListener { _, checked ->
            rain.glowEnabled = checked
        }
    }

    // ---- TOUCH ----------------------------------------------------------

    private fun setupTouch() {
        bindTouchFlag(R.id.avoidSwitch, MatrixRainView.TOUCH_AVOID)
        bindTouchFlag(R.id.flickerSwitch, MatrixRainView.TOUCH_FLICKER)
        bindTouchFlag(R.id.shockwaveSwitch, MatrixRainView.TOUCH_SHOCKWAVE)
        bindTouchFlag(R.id.touchGlitchSwitch, MatrixRainView.TOUCH_GLITCH)

        // progress 0-160 -> 40dp - 200dp
        bindSeek(R.id.radiusSeek, R.id.radiusValue, { "${40 + it}dp" }) {
            rain.touchRadiusPx = (40 + it) * resources.displayMetrics.density
        }
    }

    /** Ties a checkbox to one bit of [MatrixRainView.touchEffects], starting from the XML value. */
    private fun bindTouchFlag(checkId: Int, flag: Int) {
        val box = findViewById<CheckBox>(checkId)
        box.isChecked = (rain.touchEffects and flag) != 0
        box.setOnCheckedChangeListener { _, checked ->
            rain.touchEffects = if (checked) rain.touchEffects or flag else rain.touchEffects and flag.inv()
        }
    }

    // ---- FX -------------------------------------------------------------

    private fun setupGlitch() {
        // progress 0-100 -> 0.00 - 1.00
        bindSeek(R.id.glitchSeek, R.id.glitchValue, { "%.2f".fmt(it / 100f) }) {
            rain.glitchIntensity = it / 100f
        }
    }

    private fun setupTransparent() {
        val root = findViewById<View>(R.id.root)
        findViewById<CheckBox>(R.id.transparentSwitch).setOnCheckedChangeListener { _, checked ->
            rain.transparentBackground = checked
            // Transparent rain needs something behind it to show through.
            if (checked) {
                root.setBackgroundResource(R.drawable.backdrop_gradient)
            } else {
                root.setBackgroundColor(Color.BLACK)
            }
        }
    }

    // ---- accent ---------------------------------------------------------

    /** Re-tints the rain and every console element that follows the rain color. */
    private fun applyAccent(color: Int) {
        accent = color
        rain.rainColor = color
        tint(findViewById(R.id.root))
        segmentGroups.forEach { it.render() }
        swatches.forEach { (view, c) -> view.alpha = if (c == color) 1f else 0.4f }
    }

    private fun tint(v: View) {
        val tag = v.tag as? String
        if (tag != null) {
            if (v is TextView && tag.startsWith("accent")) {
                v.setTextColor(accent)
                v.setShadowLayer(GLOW_RADIUS, 0f, 0f, accent)
            }
            if (tag.endsWith("outline")) {
                val stroke = (1 * resources.displayMetrics.density).toInt().coerceAtLeast(1)
                (v.background?.mutate() as? GradientDrawable)
                    ?.setStroke(stroke, (accent and 0x00FFFFFF) or 0x66000000)
            }
        }
        if (v is SeekBar) {
            val list = ColorStateList.valueOf(accent)
            v.progressTintList = list
            v.thumbTintList = list
        } else if (v is CheckBox) {
            v.buttonTintList = ColorStateList.valueOf(accent)
        }
        if (v is ViewGroup) {
            for (i in 0 until v.childCount) tint(v.getChildAt(i))
        }
    }

    // ---- helpers --------------------------------------------------------

    private fun String.fmt(value: Float) = String.format(Locale.US, this, value)

    /** Seek bar with a live readout; [apply] only runs for user drags, the readout always updates. */
    private fun bindSeek(seekId: Int, valueId: Int, format: (Int) -> String, apply: (Int) -> Unit) {
        val seek = findViewById<SeekBar>(seekId)
        val value = findViewById<TextView>(valueId)
        value.text = format(seek.progress)
        seek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                value.text = format(progress)
                if (fromUser) apply(progress)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    /**
     * A row of text choices where the selected one is bracketed and lit, e.g.
     * `[ LOOK ]  TOUCH   FX`. Padding keeps every label the same width.
     */
    private inner class SegmentGroup(
        private val views: List<TextView>,
        private val labels: List<Int>,
        private var selected: Int,
        private val onSelect: (Int) -> Unit,
    ) {
        init {
            views.forEachIndexed { i, v -> v.setOnClickListener { select(i) } }
        }

        private fun select(index: Int) {
            if (index == selected) return
            selected = index
            render()
            onSelect(index)
        }

        fun render() {
            val dim = ContextCompat.getColor(this@MainActivity, R.color.term_dim)
            views.forEachIndexed { i, v ->
                val name = getString(labels[i])
                if (i == selected) {
                    v.text = getString(R.string.segment_selected, name)
                    v.setTextColor(accent)
                    v.setShadowLayer(GLOW_RADIUS, 0f, 0f, accent)
                } else {
                    v.text = getString(R.string.segment_idle, name)
                    v.setTextColor(dim)
                    v.setShadowLayer(0f, 0f, 0f, 0)
                }
            }
        }
    }

    private companion object {
        const val GLOW_RADIUS = 8f
    }
}
