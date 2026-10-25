package com.boyoffi9.matrixrainview.demo

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.RadioGroup
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.boyoffi9.matrixrainview.MatrixRainView

/**
 * Demo Activity showing every runtime-configurable property on MatrixRainView
 * wired up to a live settings panel — color, speed, density, trail fade,
 * glow, character set, touch effects, glitch and transparent mode all update
 * the running animation immediately, with no need to pause/restart it.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var rain: MatrixRainView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rain = findViewById(R.id.matrixRain)

        setupToggle()
        setupComposeDemoButton()
        setupColorSwatches()
        setupSpeedSlider()
        setupDensitySlider()
        setupFadeSlider()
        setupGlowSwitch()
        setupCharSetGroup()
        setupTouchSwitches()
        setupRadiusSlider()
        setupGlitchSlider()
        setupTransparentSwitch()
    }

    private fun setupComposeDemoButton() {
        findViewById<View>(R.id.composeDemoButton).setOnClickListener {
            startActivity(Intent(this, ComposeDemoActivity::class.java))
        }
    }

    private fun setupToggle() {
        val toggle = findViewById<View>(R.id.settingsToggle)
        val panel = findViewById<View>(R.id.settingsPanel)
        // The panel scrolls, so cap it at 60% of the screen height.
        panel.layoutParams = panel.layoutParams.apply {
            height = (resources.displayMetrics.heightPixels * 0.6f).toInt()
        }
        toggle.setOnClickListener {
            panel.visibility = if (panel.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }
    }

    private fun setupColorSwatches() {
        val banner = findViewById<android.widget.TextView>(R.id.bannerText)

        fun applyColor(colorHex: String) {
            val color = Color.parseColor(colorHex)
            rain.rainColor = color
            banner.setTextColor(color)
        }

        findViewById<View>(R.id.swatchGreen).setOnClickListener { applyColor("#00FF41") }
        findViewById<View>(R.id.swatchCyan).setOnClickListener { applyColor("#00E5FF") }
        findViewById<View>(R.id.swatchAmber).setOnClickListener { applyColor("#FFB300") }
        findViewById<View>(R.id.swatchRed).setOnClickListener { applyColor("#FF3B30") }
        findViewById<View>(R.id.swatchPurple).setOnClickListener { applyColor("#B347FF") }
        findViewById<View>(R.id.swatchWhite).setOnClickListener { applyColor("#FFFFFF") }
    }

    private fun setupSpeedSlider() {
        // progress 0-30 maps to speed 0.1x - 3.0x
        findViewById<SeekBar>(R.id.speedSeek).setOnSeekBarChangeListener(
            onProgressChanged { progress ->
                rain.speed = (progress.coerceAtLeast(1)) / 10f
            }
        )
    }

    private fun setupDensitySlider() {
        // progress 0-20 maps to density 0.1x - 2.0x
        findViewById<SeekBar>(R.id.densitySeek).setOnSeekBarChangeListener(
            onProgressChanged { progress ->
                rain.density = (progress.coerceAtLeast(1)) / 10f
            }
        )
    }

    private fun setupFadeSlider() {
        // progress maps directly to fadeStrength, clamped so it never hits 0
        findViewById<SeekBar>(R.id.fadeSeek).setOnSeekBarChangeListener(
            onProgressChanged { progress ->
                rain.fadeStrength = progress.coerceAtLeast(4)
            }
        )
    }

    private fun setupGlowSwitch() {
        findViewById<android.widget.Switch>(R.id.glowSwitch).setOnCheckedChangeListener { _, checked ->
            rain.glowEnabled = checked
        }
    }

    private fun setupCharSetGroup() {
        findViewById<RadioGroup>(R.id.charsetGroup).setOnCheckedChangeListener { _, checkedId ->
            rain.charSet = when (checkedId) {
                R.id.radioBinary -> MatrixRainView.CharSet.BINARY
                R.id.radioAlnum -> MatrixRainView.CharSet.ALNUM
                else -> MatrixRainView.CharSet.KATAKANA
            }
        }
    }

    private fun setupTouchSwitches() {
        bindTouchFlag(R.id.avoidSwitch, MatrixRainView.TOUCH_AVOID)
        bindTouchFlag(R.id.flickerSwitch, MatrixRainView.TOUCH_FLICKER)
        bindTouchFlag(R.id.shockwaveSwitch, MatrixRainView.TOUCH_SHOCKWAVE)
        bindTouchFlag(R.id.touchGlitchSwitch, MatrixRainView.TOUCH_GLITCH)
    }

    /** Ties a switch to one bit of [MatrixRainView.touchEffects], starting from the XML value. */
    private fun bindTouchFlag(switchId: Int, flag: Int) {
        val toggle = findViewById<android.widget.Switch>(switchId)
        toggle.isChecked = (rain.touchEffects and flag) != 0
        toggle.setOnCheckedChangeListener { _, checked ->
            rain.touchEffects = if (checked) rain.touchEffects or flag else rain.touchEffects and flag.inv()
        }
    }

    private fun setupRadiusSlider() {
        // progress 0-160 maps to a touch radius of 40dp - 200dp
        findViewById<SeekBar>(R.id.radiusSeek).setOnSeekBarChangeListener(
            onProgressChanged { progress ->
                rain.touchRadiusPx = (40 + progress) * resources.displayMetrics.density
            }
        )
    }

    private fun setupGlitchSlider() {
        // progress 0-100 maps to glitch intensity 0.0 - 1.0
        findViewById<SeekBar>(R.id.glitchSeek).setOnSeekBarChangeListener(
            onProgressChanged { progress ->
                rain.glitchIntensity = progress / 100f
            }
        )
    }

    private fun setupTransparentSwitch() {
        val root = findViewById<View>(R.id.root)
        findViewById<android.widget.Switch>(R.id.transparentSwitch).setOnCheckedChangeListener { _, checked ->
            rain.transparentBackground = checked
            // Transparent rain needs something behind it to show through.
            if (checked) {
                root.setBackgroundResource(R.drawable.backdrop_gradient)
            } else {
                root.setBackgroundColor(Color.BLACK)
            }
        }
    }

    /** Small helper since we only ever care about onProgressChanged for these sliders. */
    private fun onProgressChanged(action: (Int) -> Unit) = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            if (fromUser) action(progress)
        }
        override fun onStartTrackingTouch(seekBar: SeekBar?) {}
        override fun onStopTrackingTouch(seekBar: SeekBar?) {}
    }
}
