package com.boyoffi9.matrixrainview.demo

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
 * glow, and character set all update the running animation immediately,
 * with no need to pause/restart it.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var rain: MatrixRainView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rain = findViewById(R.id.matrixRain)

        setupToggle()
        setupColorSwatches()
        setupSpeedSlider()
        setupDensitySlider()
        setupFadeSlider()
        setupGlowSwitch()
        setupCharSetGroup()
    }

    private fun setupToggle() {
        val toggle = findViewById<View>(R.id.settingsToggle)
        val panel = findViewById<View>(R.id.settingsPanel)
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

    /** Small helper since we only ever care about onProgressChanged for these sliders. */
    private fun onProgressChanged(action: (Int) -> Unit) = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            if (fromUser) action(progress)
        }
        override fun onStartTrackingTouch(seekBar: SeekBar?) {}
        override fun onStopTrackingTouch(seekBar: SeekBar?) {}
    }
}
