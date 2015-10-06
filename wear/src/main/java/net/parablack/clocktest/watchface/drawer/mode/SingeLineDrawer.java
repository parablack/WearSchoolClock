package net.parablack.clocktest.watchface.drawer.mode;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import net.parablack.clocktest.watchface.drawer.WatchFaceDrawer;
import net.parablack.clocktest.watchface.drawer.mode.wrapper.SuperTimeWrapper;

public class SingeLineDrawer extends ModeFaceDrawer<SuperTimeWrapper> {

    private static final int LINE_OFFSET_BEGIN = 80;
    private static final int LINE_OFFSET_END = 120;

    private static final int LINE_SECOND_OFFSET_BEGIN = 120;
    private static final int LINE_SECOND_OFFSET_END = 130;

    private static final long SECOND_LINE_WIDTH = 320 / 60;

    static Paint greenPaint = new Paint(), redPaint = new Paint(), secondPaint = new Paint();

    static {
        greenPaint.setColor(Color.GREEN);
        redPaint.setColor(Color.RED);
        secondPaint.setColor(Color.YELLOW);
    }


    public SingeLineDrawer(WatchFaceDrawer drawer) {
        super(drawer);
    }


    @Override
    protected void onDraw(Canvas canvas, SuperTimeWrapper args) throws ScheduleDrawException {
        //      Log.i("SchoolWear", "onDraw In drawAsSymbol");
        SuperTimeWrapper.TimeWrapper begin = args.getBegin();
        SuperTimeWrapper.TimeWrapper end = args.getEnd();


        int minTotal = begin.getMinutes() + end.getMinutes();


        //       System.out.println("minTotal = " + minTotal);
        if (minTotal < 130) {  // Too big
            //            System.out.println("minTotal #2 = " + minTotal);
            long lineWidth = width / (minTotal);
            //            System.out.println("lineWidth = " + lineWidth);
            System.out.println("tB = " + begin.getMinutes() + " tE = " + end.getMinutes() + " tM = " + minTotal);

            for (int j = 1; j <= begin.getMinutes(); j++) {
                long x = ((j - 1) * lineWidth) + lineWidth / 2;
                canvas.drawLine(x, centerX + LINE_OFFSET_BEGIN, x, centerX + LINE_OFFSET_END, greenPaint);
            }
            long xy = ((begin.getMinutes()) * lineWidth) + lineWidth / 2; // (minutesFromBegin + 1) * lineWidth
            canvas.drawLine(xy, centerX + LINE_OFFSET_BEGIN, xy, centerX + LINE_OFFSET_END, secondPaint); // well secondPaint is yellow
            for (int j = minTotal - end.getMinutes(); j <= minTotal; j++) {
                long x = ((j - 1) * lineWidth) + lineWidth / 2;
                canvas.drawLine(x, centerX + LINE_OFFSET_BEGIN, x, centerX + LINE_OFFSET_END, redPaint);
            }

            // Draw Seconds
            {
                for (int j = 1; j < (60 - end.getSeconds()); j++) {
                    canvas.drawLine(j * SECOND_LINE_WIDTH, centerX + LINE_SECOND_OFFSET_BEGIN, j * SECOND_LINE_WIDTH, centerX + LINE_SECOND_OFFSET_END, greenPaint);
                }
                long temp_1 = (60 - end.getSeconds()) * SECOND_LINE_WIDTH;
                canvas.drawLine(temp_1, centerX + LINE_SECOND_OFFSET_BEGIN, temp_1, centerX + LINE_SECOND_OFFSET_END, secondPaint); // well secondPaint is yellow
                for (int j = (60 - end.getSeconds()); j <= 60; j++) {
                    canvas.drawLine(j * SECOND_LINE_WIDTH, centerX + LINE_SECOND_OFFSET_BEGIN, j * SECOND_LINE_WIDTH, centerX + LINE_SECOND_OFFSET_END, redPaint);
                }

            }

        } else throw new ScheduleDrawException("Time too big! Drawing failed!");
    }

}
