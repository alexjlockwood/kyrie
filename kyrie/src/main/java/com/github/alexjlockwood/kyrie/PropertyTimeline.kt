package com.github.alexjlockwood.kyrie

import androidx.annotation.IntRange

import java.util.ArrayList

internal class PropertyTimeline(private val drawable: KyrieDrawable) {

    private val properties = ArrayList<Property<*>>()
    private val listener = object : Property.Listener {
        override fun onCurrentPlayTimeChanged(property: Property<*>) {
            drawable.invalidateSelf()
        }
    }

    var totalDuration: Long = 0
        private set

    fun <V> registerAnimatableProperty(animations: List<Animation<*, V>>): Property<V> {
        val property = Property(animations)
        properties.add(property)
        property.addListener(listener)
        if (totalDuration != Animation.INFINITE) {
            val currTotalDuration = property.totalDuration
            totalDuration = if (currTotalDuration == Animation.INFINITE) {
                Animation.INFINITE
            } else {
                Math.max(currTotalDuration, totalDuration)
            }
        }
        return property
    }

    fun setCurrentPlayTime(@IntRange(from = 0) currentPlayTime: Long) {
        properties.forEach { it.setCurrentPlayTime(currentPlayTime) }
    }
}
