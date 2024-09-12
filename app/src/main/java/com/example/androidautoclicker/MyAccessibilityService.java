package com.example.androidautoclicker;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.view.MotionEvent;
import android.view.accessibility.AccessibilityEvent;

public class MyAccessibilityService extends AccessibilityService {
    private Point point;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    //apparently this method is called every time an event occurs
    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        System.out.println("access event");
        autoClick(2000, 100, 950, 581);
    }

    @Override
    public void onServiceConnected() {
        super.onServiceConnected();
        autoClick(2000, 100, 950, 581);
    }

    @Override
    public void onInterrupt() {

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

    public boolean onTouchEvent(MotionEvent event)
    {
        int x = (int)event.getX();
        int y = (int)event.getY();

//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//            case MotionEvent.ACTION_MOVE:
//            case MotionEvent.ACTION_UP:
//        }

//        point.setX(x);
//        point.setY(y);

        printEventPosition(x, y);
        return false;
    }

    public class Point {
        private int x;
        private int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }
    }

    public void printEventPosition(int x, int y) {
        System.out.println("Event position" + x + ", " + y);
    }

}