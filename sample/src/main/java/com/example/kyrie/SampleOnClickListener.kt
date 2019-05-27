package com.example.kyrie

import android.view.View

import com.github.alexjlockwood.kyrie.KyrieDrawable

internal class SampleOnClickListener(private val drawable: KyrieDrawable) : View.OnClickListener {

    override fun onClick(v: View) {
        if (drawable.isPaused) {
            drawable.resume()
        } else {
            if (drawable.isStarted) {
                drawable.pause()
            } else {
                drawable.start()
            }
        }
    }
}
