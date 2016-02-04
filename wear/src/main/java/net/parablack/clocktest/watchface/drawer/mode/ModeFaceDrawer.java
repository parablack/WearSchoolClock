package net.parablack.clocktest.watchface.drawer.mode;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.Log;

import net.parablack.clocktest.json.InvalidDataException;
import net.parablack.clocktest.json.JSONColors;
import net.parablack.clocktest.watchface.drawer.WatchFaceDrawer;
import net.parablack.clocktest.watchface.drawer.mode.wrapper.TimeException;


public abstract class ModeFaceDrawer<T> {

    protected static final int LINE_HEIGHT = 55;
    protected static float LINE_START = -1; // Saving power, it is (center / 2) + 70 // FIXED


    private WatchFaceDrawer drawer;
    private boolean ambi;

    protected int width, height;
    protected float centerX, centerY;

    private boolean _flagUnsuccessful = false;

    public ModeFaceDrawer(WatchFaceDrawer drawer) {
        this.drawer = drawer;
    }

    protected abstract void onDraw(Canvas c, T args) throws ScheduleDrawException, TimeException;

    public abstract void reloadColors();


    public void draw(Canvas c, Rect bounds, boolean ambi, T args) throws ScheduleDrawException, TimeException {
        width = bounds.width();
        height = bounds.height();

        centerX = width / 2f;
        centerY = height / 2f;
        this.ambi = ambi;

        if(LINE_START == -1){
            LINE_START = height / 2 + 70;
        }

        onDraw(c, args);

    }

    protected int getColors(String name) {
        try {
            JSONColors colors = drawer.getColors();
            return colors.getColorByName(name);
        } catch (InvalidDataException e) {
            Log.e("Schedule", "Color not found!");
            return Color.WHITE;
        }


    }

    public WatchFaceDrawer getDrawer() {
        return drawer;
    }

    protected boolean displaySeconds() { return ambi; }

    protected void flagUnsuccessful() {
        _flagUnsuccessful = true;
    }

    public boolean wasUnsuccesful() {
        return _flagUnsuccessful;
    }

}
