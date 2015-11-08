package net.parablack.clocktest.watchface.drawer.mode;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import net.parablack.clocktest.watchface.drawer.WatchFaceDrawer;
import net.parablack.clocktest.watchface.drawer.mode.wrapper.SuperTimeWrapper;

/**
 * Draws the time left in single lines (Minutes + Seconds)
 */
public class SingeLineDrawer extends ModeFaceDrawer<SuperTimeWrapper> {


    private static final int LINE_HEIGHT = 45;
    private static float LINE_START = -1; // Saving power, it is (center / 2) + 70

    private static final int SECOND_START = 160 + 115;
    private static final int SECOND_HEIGHT = 10;


    static Paint greenPaint = new Paint(), redPaint = new Paint(), yellowPaint = new Paint();




    public SingeLineDrawer(WatchFaceDrawer drawer) {
        super(drawer);
        greenPaint.setColor(getColors("single_done"));
        redPaint.setColor(getColors("single_todo"));
        yellowPaint.setColor(getColors("single_inprogress"));
    }


    @Override
    protected void onDraw(Canvas canvas, SuperTimeWrapper superTimeWrapper) throws ScheduleDrawException {
        if(LINE_START == -1){
            LINE_START = height / 2 + 70;
        }
        //      Log.i("SchoolWear", "onDraw In drawAsSymbol");
        SuperTimeWrapper.TimeWrapper begin = superTimeWrapper.getBegin();
        SuperTimeWrapper.TimeWrapper end = superTimeWrapper.getEnd();


        int minTotal = superTimeWrapper.getTotalMinutes(); // The current one!
        initDrawCalc(minTotal);

        System.out.println("minTotal: " + minTotal + " , begin:" + begin + " , end: " + end);

        if (minTotal < 130) {  // Too big
            System.out.println("tB = " + begin.getMinutes() + " tE = " + end.getMinutes() + " tM = " + minTotal);

            for (int j = 1; j <= begin.getMinutes(); j++) {

                drawLine(canvas, (j - 1), LINE_START, LINE_HEIGHT, greenPaint);

            }

            drawLine(canvas, begin.getMinutes(), LINE_START, LINE_HEIGHT,  yellowPaint);

            for (int j = (minTotal - end.getMinutes()) + 1; j <= minTotal; j++) {
                drawLine(canvas, (j - 1), LINE_START, LINE_HEIGHT, redPaint);
            }

            // Draw Seconds
            initDrawCalc(60);
            {
                for (int j = 1; j < (60 - end.getSeconds()); j++) {
                    drawLine(canvas, (j - 1), SECOND_START, SECOND_HEIGHT, greenPaint);
                }

                for (int j = (60 - end.getSeconds()); j <= 60; j++) {
                    drawLine(canvas, (j - 1), SECOND_START,SECOND_HEIGHT, redPaint);
                }
                drawLine(canvas, (60 - end.getSeconds() - 1), SECOND_START, SECOND_HEIGHT, yellowPaint);

            }

        } else throw new ScheduleDrawException("Time too big! Drawing failed!");
    }

    private int minTotal;
    private int lineWidth, lineWidthMod;
    private void initDrawCalc(int minTotal){
        this.minTotal = minTotal;
        lineWidth = width / (minTotal);
        lineWidthMod = width % minTotal;
    }

    /**
     * Paints the line to the clock
     * @param idNum The line from 0 on
     * @param paint The paint to draw with
     */
    private void drawLine(Canvas c, int idNum, float y, float height, Paint paint){

        int absoluteFromLeft = (lineWidthMod / 2) + (lineWidth / 2) + (lineWidth * idNum);
        c.drawLine(absoluteFromLeft, y, absoluteFromLeft, y + height, paint);

    }

}
