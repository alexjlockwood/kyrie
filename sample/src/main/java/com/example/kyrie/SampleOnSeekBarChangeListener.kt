package com.example.kyrie

import android.widget.SeekBar

import com.github.alexjlockwood.kyrie.KyrieDrawable

internal class SampleOnSeekBarChangeListener(private val drawable: KyrieDrawable) : SeekBar.OnSeekBarChangeListener {

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        val totalDuration = drawable.totalDuration
        drawable.currentPlayTime = (progress / 100f * totalDuration).toLong()
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {
        if (drawable.isRunning) {
            drawable.pause()
        }
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {}
}
