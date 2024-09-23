package com.example.androidautoclicker;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class TouchAndDragListener implements View.OnTouchListener {
    private final WindowManager.LayoutParams params;
    private final int startDragDistance;
    private final Runnable onTouch;
    private final Runnable onDrag;
    private int initialX;
    private int initialY;
    private float initialTouchX;
    private float initialTouchY;
    private boolean isDrag = false;
    private MyAccessibilityService autoClickService = MyAccessibilityService.getInstance();

    public TouchAndDragListener(WindowManager.LayoutParams params, int startDragDistance, Runnable onTouch, Runnable onDrag) {
        this.params = params;
        this.startDragDistance = startDragDistance;
        this.onTouch = onTouch;
        this.onDrag = onDrag;
    }

    private boolean isDragging(MotionEvent event) {
        double dx = event.getRawX() - initialTouchX;
        double dy = event.getRawY() - initialTouchY;
        return (Math.pow(dx, 2) + Math.pow(dy, 2)) > Math.pow(startDragDistance, 2);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                isDrag = false;
                initialX = params.x;
                initialY = params.y;
                initialTouchX = event.getRawX();
                initialTouchY = event.getRawY();
                return true;

            case MotionEvent.ACTION_MOVE:
                if (!isDrag && isDragging(event)) {
                    isDrag = true;
                }
                if (!isDrag) return true;
                params.x = initialX + (int) (event.getRawX() - initialTouchX);
                params.y = initialY + (int) (event.getRawY() - initialTouchY);
                if (onDrag != null) {
                    onDrag.run();
                }
                return true;

            case MotionEvent.ACTION_UP:
                if (!isDrag) {
                    if (onTouch != null) {
                        onTouch.run();
                    }
                    return true;
                }
                break;
        }
        Log.d("TouchAndDragListener onTouch", event.toString());
        return false;
    }

    public boolean isDrag() {
        return isDrag;
    }

    public void setDrag(boolean drag) {
        isDrag = drag;
    }

    public float getInitialTouchY() {
        return initialTouchY;
    }

    public void setInitialTouchY(float initialTouchY) {
        this.initialTouchY = initialTouchY;
    }

    public float getInitialTouchX() {
        return initialTouchX;
    }

    public void setInitialTouchX(float initialTouchX) {
        this.initialTouchX = initialTouchX;
    }

    public int getInitialY() {
        return initialY;
    }

    public void setInitialY(int initialY) {
        this.initialY = initialY;
    }

    public int getInitialX() {
        return initialX;
    }

    public void setInitialX(int initialX) {
        this.initialX = initialX;
    }

    public int getStartDragDistance() {
        return startDragDistance;
    }
}
