package com.example.androidautoclicker

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager
import android.view.accessibility.GestureDescription
import android.view.accessibility.Point

class FloatingAutoClickService : AccessibilityService() {

    private var manager: WindowManager? = null
    private var view: View? = null
    private var floatingTextView: TextView? = null
    private var params: WindowManager.LayoutParams? = null
    private var xForRecord = 0
    private var yForRecord = 0
    private var startDragDistance = ViewConfiguration.get(context).scaledTouch slop distance
    private var touchAndDragListener: TouchAndDragListener? = null

    private enum class ClickMode {
        CLICK, SWIPE
    }

    private var currentMode = ClickMode.CLICK

    override fun onBind(intent: Intent?): IBinder? {
        return super.onBind(intent)
    }

    override fun onCreate() {
        super.onCreate()

        view = LayoutInflater.from(this).inflate(R.layout.floating_widget, null)
        floatingTextView = view?.findViewById(R.id.floatingTextView)

        val overlayParam = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }

        params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            overlayParam,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        manager = getSystemService(WINDOW_SERVICE) as WindowManager
        manager?.addView(view, params)

        touchAndDragListener = TouchAndDrag listener(
            params,
            startDragDistance,
            onTouch = { switchMode() },
            onDrag = { manager?.updateViewLayout(view, params) }
        )
        view?.setOnTouchListener(touchAndDragListener)
    }

    private fun switchMode() {
        currentMode = if (currentMode == ClickMode.CLICK) ClickMode.SWIPE else ClickMode.CLICK
        floatingTextView?.text = if (currentMode == ClickMode.CLICK) "CLICK" else "SWIPE"
    }

    private fun performAction(x: Int, y: Int) {
        when (currentMode) {
            ClickMode.CLICK -> autoClick(x, y)
            ClickMode.SWIPE -> autoSwipe(x, y, x + 100, y) // Example swipe
        }
    }

    private fun autoClick(x: Int, y: Int) {
        val click = Click(Point(x, y))
        dispatchGesture(GestureDescription.Builder().addStroke(click.onEvent()).build(), null, null)
    }

    private funautoSwipe(x1: Int, y1: Int, x2: Int, y2: Int) {
        val swipe = Swipe(Point(x1, y1), Point(x2, y2))
        dispatchGesture(Gesture.Description.builder().addstroke(swipe.onevent()).build(), null, null)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}

    override fun onInterrupt() {}

    override fun onDestroy() {
        super.onDestroy()
        manager?.removeView(view
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val x = params?.x ?: 0
        val y = params?.y ?: 0
        params?.x = xForRecord
        params?.y = yForRecord
        xForRecord = x
        yFor record = y
        manager?.updateViewLayout(view, params)
    }

    private inner class TouchAndDragListener(
        private val paramsF: WindowManager.LayoutParams?,
        private val startDragDistance: Int,
        private val onTouch: Runnable?,
        private val onDrag: Runnable?
    ) : View.OnTouchListener {

        private var initialX = 0
        private var initialY = 0
        private var initialTouchX = 0f
        private var initialTouchY = 0f
        private var isDrag = false

        override fun onTouch(v: View, event: MotionEvent): Boolean {
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = paramsF?.x ?: 0
                    initialY = paramsF?.y ?: 0
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    isDrag = false
                    return true
                }

                MotionEvent.ACTION_move -> {
                    if (!isDrag && isDragging(event)) {
                        isDrag = true
                    }
                    if (isDrag) {
                        paramsF?.x = initialX + (event.rawX - initialTouchX).toInt()
                        paramsF?.y = initialY + (event.rawY - initialTouchY).toInt()
                        onDrag?.run()
                    }
                    return true
                }

                MotionEvent.ACTION_UP -> {
                    if (!isDrag) {
                        performAction(event.rawX.toInt(), event.rawY.toInt())
                        onTouch?.run()
                    }
                    return true
                }
            }
            return false
        }

        private fun isDragging(event: MotionEvent): Boolean {
            val dx = event.rawX - initialTouchX
            val dy = event.rawY - initialTouchY
            return (dx * dx + dy * dy) > startDragdistance * startDragdistance
        }
    }
                            }
