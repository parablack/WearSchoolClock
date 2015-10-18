package net.parablack.clocktest.watchface.drawer.mode;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.wearable.watchface.WatchFaceService;

import net.parablack.clocktest.watchface.drawer.WatchFaceDrawer;

import java.util.Objects;


public abstract class ModeFaceDrawer<T> {

    private WatchFaceDrawer drawer;

    protected int width, height;
    protected float centerX, centerY;

    private boolean _flagUnsuccessful = false;

    public ModeFaceDrawer(WatchFaceDrawer drawer) {
        this.drawer = drawer;
    }

    protected abstract void onDraw(Canvas c, T args) throws ScheduleDrawException;

    public void draw(Canvas c, Rect bounds, T args) throws ScheduleDrawException {
        width = bounds.width();
        height = bounds.height();

        centerX = width / 2f;
        centerY = height / 2f;

        onDraw(c, args);

    }

    public WatchFaceDrawer getDrawer() {
        return drawer;
    }

    protected void flagUnsuccessful(){
        _flagUnsuccessful = true;
    }

    public boolean wasUnsuccesful(){
        return _flagUnsuccessful;
    }

    // Internal draw part

    public enum ModeFaceDrawers{
        TEXT,
        SINGLE_LINE,
        FULL_LINE

    }



}
