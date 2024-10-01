package com.example.androidautoclicker;

import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.graphics.Point;

public abstract class CustomEvent {
    protected long startTime = 10L;
    protected long duration = 10L;
    protected Path path = new Path();

    public GestureDescription.StrokeDescription onEvent() {
        path.reset();
        createPath();
        return new GestureDescription.StrokeDescription(path, startTime, duration);
    }

    protected abstract void createPath();

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}

class Click extends CustomEvent {
    private final Point point;

    public Click(Point point) {
        this.point = point;
    }

    @Override
    protected void createPath() {
        path.moveTo(point.x, point.y);
    }
}

class Swipe extends CustomEvent {
    private final Point start;
    private final Point end;

    public Swipe(Point start, Point end) {
        this.start = start;
        this.end = end;
    }

    @Override
    protected void createPath() {
        path.moveTo(start.x, start.y);
        path.lineTo(end.x, end.y);
    }
}
