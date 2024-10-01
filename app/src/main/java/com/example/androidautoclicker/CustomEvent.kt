package com.example.androidautoclicker

import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.graphics.Point

sealed class CustomEvent(protected var startTime: Long = 10L, protected var duration: Long = 10L) {
    protected val path = Path()

    fun onEvent(): GestureDescription.StrokeDescription {
        path.reset()
        createPath()
        return GestureDescription.StrokeDescription(path, startTime, duration)
    }

    protected abstract fun createPath()
}

class Click(private val point: Point, startTime: Long = 10L, duration: Long = 10L) :
    CustomEvent(startTime, duration) {

    override fun createPath() {
        path.moveTo(point.x.toFloat(), point.y.toFloat()) // Use floats for moveTo
    }
}

class Swipe(private val start: Point, private val end: Point, startTime: Long = 10L, duration: Long = 10L) :
    CustomEvent(startTime, duration) {

    override fun createPath() {
        path.moveTo(start.x.toFloat(), start.y.toFloat())
        path.lineTo(end.x.toFloat(), end.y.toFloat())
    }
        }
