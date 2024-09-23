package com.example.androidautoclicker;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class FloatingAutoClickService extends Service {
    private WindowManager manager;
    private View view;
    private TextView floatingTextView;
    private WindowManager.LayoutParams params;
    private int xForRecord = 0;
    private int yForRecord = 0;
    private final int[] location = new int[2];
    private int startDragDistance = 0;
    private Timer timer;
    private boolean isOn = false;
    private MyAccessibilityService autoClickService = MyAccessibilityService.getInstance();

    private TouchAndDragListener touchAndDragListener;

    private Handler handlerOnClick = new Handler(Looper.getMainLooper());
    private Runnable runnable;

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

        Log.d("FloatingClickService", "onCreate");

        // Setting the layout parameters
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

        // Getting window services and adding the floating view to it
        manager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        if (manager != null) {
            manager.addView(view, params);
        }

        // Adding a touch listener to make drag movement of the floating widget
        touchAndDragListener = new TouchAndDragListener(params, startDragDistance,
                () -> viewOnClick(),
                () -> manager.updateViewLayout(view, params));
        view.setOnTouchListener(touchAndDragListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d("FloatingClickService", "onDestroy");
        if (timer != null) {
            timer.cancel();
        }
        if (manager != null && view != null) {
            manager.removeView(view);
        }
    }

    private void viewOnClick() {
        if (!isOn) {
            handlerOnClick = new Handler();
            runnable = new Runnable() {
                @Override
                public void run() {
                    Log.d("FloatingClickService getLocationOnScreen", Arrays.toString(location));
                    view.getLocationOnScreen(location);
//                    autoClickService.click(location[0] + view.getRight() + 10,
//                            location[1] + view.getBottom() + 10);
//                    autoClickService.autoClick(100, 2, location[0] + view.getRight() + 10,
//                            location[1] + view.getBottom() + 10);

                    String locX = Float.toString(touchAndDragListener.getInitialX()) + " " + Float.toString(touchAndDragListener.getInitialTouchX());
                    String locY = Float.toString(touchAndDragListener.getInitialY()) + " " + Float.toString(touchAndDragListener.getInitialTouchY());

                    Log.d("FloatingClickService touchAndDragListener", locX + ", " + locY);
                    autoClickService.autoClick(100, 2, touchAndDragListener.getInitialX(), touchAndDragListener.getInitialY());
                }
            };
            handlerOnClick.postDelayed(runnable, 200);
        }

        Log.d("FloatingClickService", "viewOnClick");
        isOn = !isOn;
        floatingTextView.setText(isOn ? "ON" : "OFF");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d("FloatingClickService", "onConfigurationChanged");
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

    private void runOnUiThread(Runnable runnable) {
        new Handler(Looper.getMainLooper()).post(runnable);
    }
}
