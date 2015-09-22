package net.parablack.clocktest.watchface.drawer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Vibrator;
import android.util.Log;

import net.parablack.clocktest.watchface.SchoolWatchFaceService;
import net.parablack.clocktest.watchface.WearEvent;
import net.parablack.clocktest.json.JSONEvent;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class WatchFaceDrawer {

    private static final String DOUBLE_COLON = ":";

    private boolean drawAsText = false;

    Paint hourPaint = new Paint(), minutePaint = new Paint(), secondPaint = new Paint();

    Paint datePaint = new Paint();

    Paint doubleColonPaintHours = new Paint();
    float doubleColonWidthHours;

    Paint scheduleTextPaint = new Paint(), scheduleSubjectPaint = new Paint(), scheduleTimePaint = new Paint(), scheduleNextPaint = new Paint();

    private SchoolWatchFaceService.Engine engine;

    private Vibrator vibrator;

    public WatchFaceDrawer(SchoolWatchFaceService service) {
        engine = service.getWatchEngine();

        vibrator = (Vibrator) service.getSystemService(Context.VIBRATOR_SERVICE);


        hourPaint.setColor(Color.WHITE);
        hourPaint.setTextSize(100);

        doubleColonPaintHours.setTextSize(100);
        doubleColonPaintHours.setColor(Color.WHITE);

        minutePaint.setColor(Color.WHITE);
        minutePaint.setTextSize(100);

        secondPaint.setColor(Color.YELLOW);
        secondPaint.setTextSize(60);

        datePaint.setColor(Color.WHITE);
        datePaint.setTextSize(35);


        scheduleTextPaint.setColor(Color.GREEN);
        scheduleTextPaint.setTextSize(25);

        scheduleTimePaint.setColor(Color.RED);
        scheduleTimePaint.setTextSize(60);

        scheduleSubjectPaint.setColor(Color.GREEN);
        scheduleSubjectPaint.setTextSize(40);

        scheduleNextPaint.setColor(Color.MAGENTA);
        scheduleNextPaint.setTextSize(20);

        doubleColonWidthHours = doubleColonPaintHours.measureText(DOUBLE_COLON);

    }

    public void onDraw(Canvas canvas, Rect bounds) {
            /* draw your watch face */


        engine.getCalendar().setTimeInMillis(System.currentTimeMillis());

        int hourOfDay = engine.getCalendar().get(Calendar.HOUR_OF_DAY), minute = engine.getCalendar().get(Calendar.MINUTE);

        String hourString = String.format("%02d", hourOfDay);
        String minuteString = String.format("%02d", minute);
        String secondString = String.format("%02d", engine.getCalendar().get(Calendar.SECOND));

        String dateString = new SimpleDateFormat("EEE, dd. MMM", Locale.GERMANY).format(engine.getCalendar().getTime());
        //      String dateString2 = new SimpleDateFormat("MMMM yyyy").format(calendar.getTime());

        canvas.drawColor(Color.BLACK);

        int width = bounds.width();
        int height = bounds.height();

        float centerX = width / 2f;
        float centerY = height / 2f;

        canvas.drawText(DOUBLE_COLON, centerX - (doubleColonWidthHours / 2), centerY - 60, doubleColonPaintHours);
        canvas.drawText(hourString, centerX - (hourPaint.measureText(hourString) + (doubleColonWidthHours / 2)), centerY - 60, hourPaint);
        canvas.drawText(minuteString, centerX + (doubleColonWidthHours / 2), centerY - 60, minutePaint);

        canvas.drawText(dateString, 10, centerY - 10, datePaint);
        // canvas.drawText(dateString2, 10, centerY + 20, datePaint);

        if (engine.isScheduleEnabled()) {
            WearEvent currentEvent = engine.getMainSchedule().getCurrent();

            canvas.drawText("Aktuelle Stunde:", 10, centerY + 35, scheduleTextPaint);
            canvas.drawText(currentEvent.getName(), 10, centerY + 70, scheduleSubjectPaint);

            canvas.drawText("Danach: " + engine.getMainSchedule().getNext().getName(), 10, centerY + 150, scheduleNextPaint);

            if (currentEvent instanceof JSONEvent) {
                JSONEvent e = (JSONEvent) currentEvent;

                e.checkVibrate(vibrator);


            }

        }

        // --------------- DRAW TIME TIL END ---------------------
        if (engine.shouldTimerBeRunning()) {

            canvas.drawText(secondString, centerX + 80, centerY + 5, secondPaint);

            if (engine.isScheduleEnabled()) {

                long toEnd = engine.getMainSchedule().getCurrent().getTimeTilEnd();
                if (toEnd < 0) System.out.println("Woa, toEnd < 0, this should never happen!");

                toEnd /= 1000;
                long s = toEnd % 60;
                toEnd -= s;
                toEnd /= 60;
                long min = toEnd % 60;
                toEnd -= min;
                long h = toEnd / 60;

                if (drawAsText) {
                    String text = String.format("%01d:%02d:%02d", h, min, s);
                    canvas.drawText(text, width - (scheduleTimePaint.measureText(text) + 10), centerX + 120, scheduleTimePaint);
                }
                else{
                    // Draw as matches
                    if(engine.getMainSchedule().getCurrent() instanceof  JSONEvent){
                        Log.i("SchoolWear", "onDraw In drawAsSymbol");
                        JSONEvent jse = (JSONEvent) engine.getMainSchedule().getCurrent();
                        long begin = jse.getTimeFromBeginning();
                        long minutesFromBegin = (begin / 1000) / 60;
                        long minTotal = minutesFromBegin + min;
                        System.out.println("minTotal = " + minTotal);
                        if(minTotal < 100){  // Too big
                            System.out.println("minTotal #2 = " + minTotal);
                            long lineWidth = width / (minTotal);
                            System.out.println("lineWidth = " + lineWidth);

                            for(int j = 0; j < minTotal; j++) {
                                canvas.drawLine(j * lineWidth, centerX + 120, j * lineWidth, centerX + 160, secondPaint);
                            }

                        } else drawAsText = true;
                    }
                    else drawAsText = true;

                }

            }
        }
        // ---------------------------------------------------------

    }

    public void setAntiAlias(boolean antiAlias) {
        hourPaint.setAntiAlias(antiAlias);
        minutePaint.setAntiAlias(antiAlias);
        secondPaint.setAntiAlias(antiAlias);
        scheduleNextPaint.setAntiAlias(antiAlias);
        scheduleSubjectPaint.setAntiAlias(antiAlias);
        scheduleTextPaint.setAntiAlias(antiAlias);
        scheduleTimePaint.setAntiAlias(antiAlias);
    }


}
