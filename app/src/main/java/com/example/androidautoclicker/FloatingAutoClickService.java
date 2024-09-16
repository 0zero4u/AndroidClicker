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
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.Timer;
import java.util.TimerTask;

public class FloatingAutoClickService extends Service {
    private WindowManager manager;
    private View view;
    private WindowManager.LayoutParams params;
    private int xForRecord = 0;
    private int yForRecord = 0;
    private final int[] location = new int[2];
    private int startDragDistance = 0;
    private Timer timer;
    private boolean isOn = false;
    private MyAccessibilityService autoClickService = MyAccessibilityService.getInstance();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        view = LayoutInflater.from(this).inflate(R.layout.floating_widget, null);
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
        view.setOnTouchListener(new TouchAndDragListener(params, startDragDistance,
                () -> viewOnClick(),
                () -> manager.updateViewLayout(view, params)));
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
//        if (isOn) {
//            if (timer != null) {
//                timer.cancel();
//            }
//        } else {
//            timer = new Timer();
//            timer.schedule(new TimerTask() {
//                @Override
//                public void run() {
//                    view.getLocationOnScreen(location);
//                    autoClickService.click(location[0] + view.getRight() + 10,
//                            location[1] + view.getBottom() + 10);
//                   //autoClickService.autoClick(100, 2,location[0] + view.getRight() + 10,
//                      //      location[1] + view.getBottom() + 10);
//                }
//            }, 0, 200);
//        }
        Log.d("FloatingClickService", "viewOnClick");
        isOn = !isOn;
        ((TextView) view).setText(isOn ? "ON" : "OFF");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // "FloatingClickService onConfigurationChanged".logd(); // Use Log.d(TAG, "message") in Java
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
}
