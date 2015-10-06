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
import net.parablack.clocktest.watchface.drawer.mode.ModeFaceDrawer;
import net.parablack.clocktest.watchface.drawer.mode.ScheduleDrawException;
import net.parablack.clocktest.watchface.drawer.mode.SingeLineDrawer;
import net.parablack.clocktest.watchface.drawer.mode.TextDrawer;
import net.parablack.clocktest.watchface.drawer.mode.wrapper.SuperTimeWrapper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class WatchFaceDrawer {



    private static final String DOUBLE_COLON = ":";


    private static final Paint hourPaint = new Paint(), minutePaint = new Paint(), secondPaint = new Paint(), doubleColonPaintHours = new Paint();

    private static final Paint datePaint = new Paint();


    private static final Paint scheduleSubjectPaint = new Paint(),  scheduleNextPaint = new Paint();


    private static final float doubleColonWidthHours;


    static{
        hourPaint.setColor(Color.WHITE);
        hourPaint.setTextSize(100);

        doubleColonPaintHours.setTextSize(100);
        doubleColonPaintHours.setColor(Color.WHITE);

        minutePaint.setColor(Color.WHITE);
        minutePaint.setTextSize(100);

        secondPaint.setColor(Color.parseColor("#0A63F2")); // 09ED0D
        secondPaint.setTextSize(60);

        datePaint.setColor(Color.WHITE);
        datePaint.setTextSize(35);

        scheduleSubjectPaint.setColor(Color.parseColor("#FF9100")); // #F2D70A
        scheduleSubjectPaint.setTextSize(32);

        scheduleNextPaint.setColor(Color.parseColor("#FF9100"));
        scheduleNextPaint.setTextSize(22);

        doubleColonWidthHours = doubleColonPaintHours.measureText(DOUBLE_COLON);
    }



    private boolean drawAsText = false;

    private SchoolWatchFaceService.Engine engine;

    private final Vibrator vibrator;

    private final ModeFaceDrawer<String> textDrawer = new TextDrawer(this);
    private final ModeFaceDrawer<SuperTimeWrapper> singleLineDrawer = new SingeLineDrawer(this);


    public WatchFaceDrawer(SchoolWatchFaceService service) {
        engine = service.getWatchEngine();

        vibrator = (Vibrator) service.getSystemService(Context.VIBRATOR_SERVICE);




    }

    public void onDraw(Canvas canvas, Rect bounds) {
            /* draw your watch face */


        engine.getCalendar().setTimeInMillis(System.currentTimeMillis());

        int hourOfDay = engine.getCalendar().get(Calendar.HOUR_OF_DAY), minute = engine.getCalendar().get(Calendar.MINUTE);

        String hourString = String.format("%02d", hourOfDay);
        String minuteString = String.format("%02d", minute);
        String secondString = String.format("%02d", engine.getCalendar().get(Calendar.SECOND));

        if(hourOfDay == 0 && minute == 1){
            engine.getMainSchedule().reload();

        }

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

          //  canvas.drawText("Aktuelle Stunde:", 10, centerY + 35, scheduleTextPaint);
            float halfCurrentWidth = scheduleSubjectPaint.measureText(currentEvent.getName()) / 2;
            canvas.drawText(currentEvent.getName(), centerX - halfCurrentWidth, centerY + 60, scheduleSubjectPaint);

            float halfW =  centerX - (scheduleNextPaint.measureText(engine.getMainSchedule().getNext().getName()) / 2);
            canvas.drawText(engine.getMainSchedule().getNext().getName(), halfW, centerY + 150, scheduleNextPaint);

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
                int sec = (int) (toEnd % 60);
                toEnd -= sec;
                toEnd /= 60;
                int min = (int) (toEnd % 60);
                toEnd -= min;
                int h = (int) (toEnd / 60);

                if (drawAsText) {
                    String text = String.format("%01d:%02d:%02d", h, min, sec);
                    try {
                        textDrawer.draw(canvas, bounds, text);
                    } catch (ScheduleDrawException e) {
                        e.printStackTrace();
                        // This can never happen!
                    }

                }
                else{
                    // Draw as matches

                    if(engine.getMainSchedule().getCurrent() instanceof  JSONEvent){
                        JSONEvent jse = (JSONEvent) engine.getMainSchedule().getCurrent();

                        SuperTimeWrapper.TimeWrapper ttE = new SuperTimeWrapper.TimeWrapper(h, min, sec);
                        long begin = jse.getTimeFromBeginning();
                        int minutesFromBegin = (int) ((begin / 1000) / 60);

                        SuperTimeWrapper.TimeWrapper fromBegin = new SuperTimeWrapper.TimeWrapper(-1, minutesFromBegin, -1);  // Hours and seconds are never needed

                        try {
                            singleLineDrawer.draw(canvas, bounds, new SuperTimeWrapper(fromBegin, ttE));
                        } catch (ScheduleDrawException e) {
                            Log.i("Schedule", "ScheduleDrawException : " + e.getMessage());
                            drawAsText = true; // Can happen, simply change to default view!
                        }


                    }
                    else{
                        Log.i("Schedule", "ScheduleDrawException : " + "No JSON Event");
                        drawAsText = true; // Can happen, simply change to default view!
                    }

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
    }


    public void setDrawAsText(boolean drawAsText) {
        this.drawAsText = drawAsText;
    }

    public boolean isDrawAsText() {
        return drawAsText;
    }
}
