package com.example.androidautoclicker;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.content.Intent;
import android.graphics.Path;
import android.graphics.PointF;
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;
import android.view.accessibility.AccessibilityEvent;

import java.util.ArrayList;
import java.util.List;

public class MyAccessibilityService extends AccessibilityService {
    private static final String TAG = AccessibilityService.class.getName();
    private static MyAccessibilityService myAccessibilityService;
    private final List<CustomEvent> events = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
    }

    //apparently this method is called every time an event occurs
    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        System.out.println("access event getEventType " + accessibilityEvent.getEventType());
        System.out.println("access event getSource " + accessibilityEvent.getSource());

        // autoClick(2000, 100, 500, 590);
    }

    @Override
    public void onServiceConnected() {
        super.onServiceConnected();
        Log.d(TAG,"MyAccessibilityService onServiceConnected");
        myAccessibilityService = this;
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        // autoClick(2000, 100, 950, 581);
    }

    @Override
    public void onInterrupt() {

    }

    public void click(int x, int y) {
        System.out.println("click " + x + " " + y);
        Path path = new Path();
        path.moveTo(x, y);
        GestureDescription.Builder builder = new GestureDescription.Builder();
        GestureDescription gestureDescription = builder
                .addStroke(new GestureDescription.StrokeDescription(path, 10, 10))
                .build();
        dispatchGesture(gestureDescription, null, null);
    }

    public void run(List<CustomEvent> newEvents) {
        events.clear();
        events.addAll(newEvents);

        GestureDescription.Builder builder = new GestureDescription.Builder();
        for (CustomEvent event : events) {
            builder.addStroke(event.onEvent());
        }
        dispatchGesture(builder.build(), null, null);
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

    public void autoClick(int startTimeMs, int durationMs, int x, int y) {
        boolean isCalled = dispatchGesture(gestureDescription(startTimeMs, durationMs, x, y), null, null);
        System.out.println(isCalled);
    }

    public GestureDescription gestureDescription(int startTimeMs, int durationMs, int x, int y) {
        Path path = new Path();
        path.moveTo(x, y);
        return createGestureDescription(new GestureDescription.StrokeDescription(path, startTimeMs, durationMs));
    }

    public GestureDescription createGestureDescription(GestureDescription.StrokeDescription... strokes) {
        GestureDescription.Builder builder = new GestureDescription.Builder();
        for (GestureDescription.StrokeDescription stroke : strokes) {
            builder.addStroke(stroke);
        }
        return builder.build();
    }
}