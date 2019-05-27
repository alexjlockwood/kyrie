package com.example.kyrie

import android.widget.SeekBar

import com.github.alexjlockwood.kyrie.KyrieDrawable

internal class SampleListenerAdapter(private val seekBar: SeekBar) : KyrieDrawable.ListenerAdapter() {

    override fun onAnimationUpdate(drawable: KyrieDrawable) {
        val playTime = drawable.currentPlayTime.toFloat()
        val totalDuration = drawable.totalDuration.toFloat()
        val fraction = playTime / totalDuration
        seekBar.progress = Math.round(fraction * seekBar.max)
    }
}
