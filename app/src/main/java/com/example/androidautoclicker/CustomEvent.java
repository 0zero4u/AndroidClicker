package com.example.androidautoclicker;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.accessibility.AccessibilityEvent;

public abstract class CustomEvent {
    protected long startTime = 10L;
    protected long duration = 10L;
    protected Path path = new Path();

    public GestureDescription.StrokeDescription onEvent() {
        path.reset(); // Reset the path instead of reinitializing it
        movePath();
        return new GestureDescription.StrokeDescription(path, startTime, duration);
    }

    protected abstract void movePath();
}

class Move extends CustomEvent {
    private final Point to;

    public Move(Point to) {
        this.to = to;
    }

    public Point getTo() {
        return to;
    }

    @Override
    protected void movePath() {
        path.moveTo(to.x, to.y);
    }
}

class Click extends CustomEvent {
    private final Point to;

    public Click(Point to) {
        this.to = to;
    }

    public Point getTo() {
        return to;
    }

    @Override
    protected void movePath() {
        path.moveTo(to.x, to.y);
    }
}

class Swipe extends CustomEvent {
    private final Point from;
    private final Point to;

    public Swipe(Point from, Point to) {
        this.from = from;
        this.to = to;
    }

    public Point getFrom() {
        return from;
    }

    public Point getTo() {
        return to;
    }

    @Override
    protected void movePath() {
        path.moveTo(from.x, from.y);
        path.lineTo(to.x, to.y);
    }
}

