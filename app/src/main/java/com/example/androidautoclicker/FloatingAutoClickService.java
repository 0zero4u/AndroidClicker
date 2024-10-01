package com.example.androidautoclicker;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import androidx.annotation.Nullable;

public class FloatingAutoClickService extends Service {
    private WindowManager manager;
    private View view;
    private TextView floatingTextView;
    private WindowManager.LayoutParams params;
    private int xForRecord = 0;
    private int yForRecord = 0;
    private int startDragDistance = 0;
    private MyAccessibilityService autoClickService = MyAccessibilityService.getInstance();
    private TouchAndDragListener touchAndDragListener;

    private enum ClickMode {
        CLICK, SWIPE
    }
    private ClickMode currentMode = ClickMode.CLICK;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        view = LayoutInflater.from(this).inflate(R.layout.floating_widget, null);
        floatingTextView = view.findViewById(R.id.floatingTextView);

        int overlayParam;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            overlayParam = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            overlayParam = WindowManager.LayoutParams.TYPE_PHONE;
        }

        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                overlayParam,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );

        manager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        if (manager != null) {
            manager.addView(view, params);
        }

        touchAndDragListener = new TouchAndDragListener(params, startDragDistance,
                () -> switchMode(),
                () -> manager.updateViewLayout(view, params));
        view.setOnTouchListener(touchAndDragListener);
    }

    private void switchMode() {
        currentMode = (currentMode == ClickMode.CLICK) ? ClickMode.SWIPE : ClickMode.CLICK;
        floatingTextView.setText(currentMode == ClickMode.CLICK ? "CLICK" : "SWIPE");
    }

    private void performAction(float x, float y) {
        switch (currentMode) {
            case CLICK:
                autoClickService.autoClick(0, 10, (int) x, (int) y);
                break;
            case SWIPE:
                autoClickService.autoSwipe(0, 500, (int) x, (int) y, (int) x + 100, (int) y);
                break;
        }
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        if (manager != null && view != null) {
            manager.removeView(view);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int x = params.x;
        int y = params.y;
        params.x = xForRecord;
        params.y = yForRecord;
        xForRecord = x;
        yForRecord = y;
        if (manager != null) {
            manager.updateViewLayout(view, params);
        }
    }

    private class TouchAndDragListener implements View.OnTouchListener {
        private final WindowManager.LayoutParams params;
        private final int startDragDistance;
        private final Runnable onTouch;
        private final Runnable onDrag;
        private int initialX;
        private int initialY;
        private float initialTouchX;
        private float initialTouchY;
        private boolean isDrag = false;


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
                        performAction(event.getRawX(), event.getRawY());
                        return true;
                    }
                    break;
            }
            return false;
        }


        public float getInitialTouchY() { return initialTouchY; }
        public float getInitialTouchX() { return initialTouchX; }
        public int getInitialY() { return initialY;  }
        public int getInitialX() { return initialX; }
    }


}
