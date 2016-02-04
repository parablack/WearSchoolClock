package net.parablack.clocktest.watchface.drawer.mode;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import net.parablack.clocktest.watchface.drawer.WatchFaceDrawer;
import net.parablack.clocktest.watchface.drawer.mode.wrapper.SuperTimeWrapper;
import net.parablack.clocktest.watchface.drawer.mode.wrapper.TimeException;

/**
 * Draws the time left in one line, also the percentage done
 */
public class FullLineDrawer extends ModeFaceDrawer<SuperTimeWrapper>{


    static Paint donePaint = new Paint(), todoPaint = new Paint(), percentagePaint = new Paint();


    public FullLineDrawer(WatchFaceDrawer drawer) {
        super(drawer);

       reloadColors();

    }


    @Override
    protected void onDraw(Canvas canvas, SuperTimeWrapper superTimeWrapper) throws ScheduleDrawException, TimeException {

        drawLine(canvas, superTimeWrapper.getPercentageDone(), LINE_START, LINE_HEIGHT);

    }

    @Override
    public void reloadColors() {
        donePaint.setColor(getColors("full_done"));
        todoPaint.setColor(getColors("full_todo"));
        percentagePaint.setColor(getColors("full_percentage"));
        percentagePaint.setTextSize(40);
    }


    private void drawLine(Canvas c, double currentPercentage, float y, float height){

        float sP = (float) (width * currentPercentage);

        c.drawRect(0, y, sP, y + height, donePaint);
        c.drawRect(sP, y, width, y + height, todoPaint);
        float f;
        if(displaySeconds()) {
            int a = (int) (currentPercentage * 1000);
            f = a / 10F;
        }
        else f = Math.round(currentPercentage * 100);

        Rect bounds = new Rect();
        percentagePaint.getTextBounds(f + "", 0, 2, bounds);
        percentagePaint.setTextAlign(Paint.Align.CENTER);
        int hei = bounds.height();
        float offset = y + height - ((height - hei) / 2);

        c.drawText(f + "", centerX, offset, percentagePaint);

    }
}
