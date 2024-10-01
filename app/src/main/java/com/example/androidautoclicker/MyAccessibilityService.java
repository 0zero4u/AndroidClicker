package com.example.androidautoclicker;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.content.Intent;
import android.graphics.Path;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

public class MyAccessibilityService extends AccessibilityService {
    private static final String TAG = AccessibilityService.class.getName();
    private static MyAccessibilityService myAccessibilityService;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
    }

    @Override
    public void onServiceConnected() {
        super.onServiceConnected();
        Log.d(TAG,"MyAccessibilityService onServiceConnected");
        myAccessibilityService = this;
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onInterrupt() {

    }


    public void autoClick(int startTimeMs, int durationMs, int x, int y) {
        boolean isCalled = dispatchGesture(gestureDescription(startTimeMs, durationMs, x, y), null, null);
        System.out.println("Click performed: " + isCalled);
    }

    public void autoSwipe(int startTimeMs, int durationMs, int x1, int y1, int x2, int y2) {
        Path swipePath = new Path();
        swipePath.moveTo(x1, y1);
        swipePath.lineTo(x2, y2);

        GestureDescription.StrokeDescription stroke = new GestureDescription.StrokeDescription(swipePath, startTimeMs, durationMs);
        boolean isCalled = dispatchGesture(createGestureDescription(stroke), null, null);
        System.out.println("Swipe performed: " + isCalled);
    }

    private GestureDescription gestureDescription(int startTimeMs, int durationMs, int x, int y) {
        Path path = new Path();
        path.moveTo(x, y);
        return createGestureDescription(new GestureDescription.StrokeDescription(path, startTimeMs, durationMs));
    }


    private GestureDescription createGestureDescription(GestureDescription.StrokeDescription... strokes) {
        GestureDescription.Builder builder = new GestureDescription.Builder();
        for (GestureDescription.StrokeDescription stroke : strokes) {
            builder.addStroke(stroke);
        }
        return builder.build();
    }



    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG,"MyAccessibilityService onUnbind");
        myAccessibilityService = null;
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG,"MyAccessibilityService onDestroy");
        myAccessibilityService = null;
        super.onDestroy();
    }


    public static MyAccessibilityService getInstance() {
        return myAccessibilityService;
    }


}
