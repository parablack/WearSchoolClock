package net.parablack.clocktest.watchface.drawer.mode;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import net.parablack.clocktest.watchface.drawer.WatchFaceDrawer;


public class TextDrawer extends ModeFaceDrawer<String> {

    private static Paint scheduleTimePaint = new Paint();

    static {
        scheduleTimePaint.setColor(Color.RED);
        scheduleTimePaint.setTextSize(60);
    }

    public TextDrawer(WatchFaceDrawer d) {
        super(d);
    }

    @Override
    protected void onDraw(Canvas c, String text) {

        c.drawText(text, width - (scheduleTimePaint.measureText(text) + 10), centerX + 120, scheduleTimePaint);

    }


}