package net.parablack.clocktest.watchface.drawer.mode;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import net.parablack.clocktest.watchface.drawer.WatchFaceDrawer;
import net.parablack.clocktest.watchface.drawer.mode.wrapper.SuperTimeWrapper;
import net.parablack.clocktest.watchface.drawer.mode.wrapper.TimeException;

import java.util.Locale;

/**
 * Draws the time left as a simple text
 */
public class TextDrawer extends ModeFaceDrawer<SuperTimeWrapper> {

    private static Paint scheduleTimePaint = new Paint();

    static {
        scheduleTimePaint.setTextSize(60);
    }

    public TextDrawer(WatchFaceDrawer d) {
        super(d);
        reloadColors();
    }

    @Override
    public void reloadColors() {
        scheduleTimePaint.setColor(getColors("text_text"));

    }

    @Override
    protected void onDraw(Canvas c, SuperTimeWrapper time) throws TimeException {
        String text;
        scheduleTimePaint.setAntiAlias(displaySeconds());
        if(displaySeconds())
        text = String.format(Locale.GERMANY, "%01d:%02d:%02d", time.getEnd().getHours(), time.getEnd().getMinutes(), time.getEnd().getSeconds());
        else text = String.format(Locale.GERMANY,"%01d:%02d", time.getEnd().getHours(), time.getEnd().getMinutes());
        c.drawText(text, centerX - (scheduleTimePaint.measureText(text) / 2), centerX + 115, scheduleTimePaint);

    }


}
