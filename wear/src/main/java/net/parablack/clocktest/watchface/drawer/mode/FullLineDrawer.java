package net.parablack.clocktest.watchface.drawer.mode;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import net.parablack.clocktest.watchface.drawer.WatchFaceDrawer;
import net.parablack.clocktest.watchface.drawer.mode.wrapper.SuperTimeWrapper;

public class FullLineDrawer extends ModeFaceDrawer<SuperTimeWrapper>{

    private static final int LINE_HEIGHT = 55;
    private static final float LINE_START = 160 + 70; // Saving power, it is (center / 2) + 70

    static Paint greenPaint = new Paint(), redPaint = new Paint(), yellowPaint = new Paint();

    static {
        greenPaint.setColor(Color.GREEN);
        redPaint.setColor(Color.RED);
        yellowPaint.setColor(Color.YELLOW);
        yellowPaint.setTextSize(40);
    }

    public FullLineDrawer(WatchFaceDrawer drawer) {
        super(drawer);
    }


    @Override
    protected void onDraw(Canvas canvas, SuperTimeWrapper superTimeWrapper) throws ScheduleDrawException {
        drawLine(canvas, superTimeWrapper.getPercentageDone(), LINE_START, LINE_HEIGHT);
    }


    private void drawLine(Canvas c, double currentPercentage, float y, float height){
        float sP = (float) (width * currentPercentage);

        c.drawRect(0, y, sP, y + height, greenPaint);
        c.drawRect(sP, y, width, y + height, redPaint);

        int a = (int) (currentPercentage * 1000);
        float f = a / 10F;

        Rect bounds = new Rect();
        yellowPaint.getTextBounds(f + "", 0, 2, bounds);
        yellowPaint.setTextAlign(Paint.Align.CENTER);
        int hei = bounds.height();
        float offset = y + height - ((height - hei) / 2);

        c.drawText(f + "", centerX, offset, yellowPaint);

    }
}
