package net.parablack.clocktest.watchface.drawer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Vibrator;
import android.util.Log;

import com.google.android.gms.wearable.Asset;

import net.parablack.clocktest.json.JSONColors;
import net.parablack.clocktest.json.JSONEvent;
import net.parablack.clocktest.watchface.SchoolWatchFaceService;
import net.parablack.clocktest.watchface.WearEvent;
import net.parablack.clocktest.watchface.drawer.mode.FullLineDrawer;
import net.parablack.clocktest.watchface.drawer.mode.ModeFaceDrawer;
import net.parablack.clocktest.watchface.drawer.mode.PixelDrawer;
import net.parablack.clocktest.watchface.drawer.mode.ScheduleDrawException;
import net.parablack.clocktest.watchface.drawer.mode.SingeLineDrawer;
import net.parablack.clocktest.watchface.drawer.mode.TextDrawer;
import net.parablack.clocktest.watchface.drawer.mode.wrapper.SuperTimeWrapper;
import net.parablack.clocktest.watchface.drawer.mode.wrapper.TimeException;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static net.parablack.clocktest.watchface.drawer.mode.wrapper.SuperTimeWrapper.*;


public class WatchFaceDrawer {


    private static final String DOUBLE_COLON = ":";


    private static final Paint hourPaint = new Paint(), minutePaint = new Paint(), secondPaint = new Paint(), doubleColonPaintHours = new Paint();

    private static final Paint datePaint = new Paint();


    private static final Paint scheduleSubjectPaint = new Paint(), scheduleNextPaint = new Paint();


    private static final float doubleColonWidthHours;


    static {
        hourPaint.setTextSize(100);

        doubleColonPaintHours.setTextSize(100);

        minutePaint.setTextSize(100);

        secondPaint.setTextSize(60);

        datePaint.setTextSize(35);

        scheduleSubjectPaint.setTextSize(28);

        scheduleNextPaint.setTextSize(22);

        doubleColonWidthHours = doubleColonPaintHours.measureText(DOUBLE_COLON);
    }

    private SchoolWatchFaceService.Engine engine;

    private final Vibrator vibrator;

    private JSONColors colors;

    private ModeFaceDrawer<SuperTimeWrapper> modeDrawer;


    public WatchFaceDrawer(SchoolWatchFaceService service) {
        engine = service.getWatchEngine();
        colors = new JSONColors();
        vibrator = (Vibrator) service.getSystemService(Context.VIBRATOR_SERVICE);
        modeDrawer = new TextDrawer(this);
    }


    private void loadColors() {
        hourPaint.setColor(colors.getMainTime());
        minutePaint.setColor(colors.getMainTime());
        doubleColonPaintHours.setColor(colors.getMainTime());

        secondPaint.setColor(colors.getSecondsTime()); // 0A63F2 is das von ir 09ED0D         TOBI #FF9C9C

        scheduleSubjectPaint.setColor(getColors().getSubjectCurrent()); // #F2D70A        // #A1A9FF tobi
        scheduleNextPaint.setColor(getColors().getSubjectNext()); // FF9100 ist das orange von mir       // #A1A9FF tobi

        datePaint.setColor(colors.getMainTime());

    }

    public void updateColors(InputStream stream) {

        colors = engine.getMainReader().getColors(stream);

        modeDrawer.reloadColors();

        loadColors();
    }

    public void updateColors(JSONColors colors){
        this.colors = colors;

        modeDrawer.reloadColors();
        loadColors();
    }

    public void onDraw(Canvas canvas, Rect bounds) {
            /* draw your watch face */


        engine.getCalendar().setTimeInMillis(System.currentTimeMillis());

        int hourOfDay = engine.getCalendar().get(Calendar.HOUR_OF_DAY), minute = engine.getCalendar().get(Calendar.MINUTE);

        String hourString = String.format("%02d", hourOfDay);
        String minuteString = String.format("%02d", minute);
        String secondString = String.format("%02d", engine.getCalendar().get(Calendar.SECOND));

        // Doesn't work?!
//        if(hourOfDay == 0 && minute == 1){
//            engine.getMainSchedule().reload();
//        }


        String dateString = new SimpleDateFormat("EEE, dd. MMM", Locale.GERMANY).format(engine.getCalendar().getTime());
        //      String dateString2 = new SimpleDateFormat("MMMM yyyy").format(calendar.getTime());

        canvas.drawColor(engine.notifyReload() ? Color.BLUE : Color.BLACK);

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

            float halfW = centerX - (scheduleNextPaint.measureText(engine.getMainSchedule().getNext().getName()) / 2);
            canvas.drawText(engine.getMainSchedule().getNext().getName(), halfW, centerY + 150, scheduleNextPaint);

            if (currentEvent instanceof JSONEvent) {
                JSONEvent e = (JSONEvent) currentEvent;

                e.checkVibrate(vibrator);


            }

        }

        // --------------- DRAW TIME TIL END ---------------------
        if (engine.shouldTimerBeRunning()) {

            canvas.drawText(secondString, centerX + 80, centerY + 5, secondPaint);
        }

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


                try{

                    TimeWrapper timeWrapperToEnd = new TimeWrapper(h, min, sec);
                    TimeWrapper fromBegin = new SuperTimeWrapper.ErrorTimeWrapper();

                    if (engine.getMainSchedule().getCurrent() instanceof JSONEvent) {
                        JSONEvent jse = (JSONEvent) engine.getMainSchedule().getCurrent();

                        long begin = jse.getTimeFromBeginning();
                        int minutesFromBegin = (int) ((begin / 1000) / 60);

                        fromBegin = new TimeWrapper(-1, minutesFromBegin, -1);  // Hours and seconds are never needed

                    }

                        modeDrawer.draw(canvas, bounds, engine.shouldTimerBeRunning(), new SuperTimeWrapper(fromBegin, timeWrapperToEnd));

                }catch(TimeException e){
                    modeDrawer = new TextDrawer(this);
                } catch (ScheduleDrawException e) {
                    e.printStackTrace();

                }

        }
        // ---------------------------------------------------------
    }

    public void onAmbientModeChanged(boolean inAmbient){
        setAntiAlias(!inAmbient);
    }
    /**
     * @param antiAlias Wether anti-aliasing should be applied or not
     */
    private void setAntiAlias(boolean antiAlias) {
        hourPaint.setAntiAlias(antiAlias);
        minutePaint.setAntiAlias(antiAlias);
        secondPaint.setAntiAlias(antiAlias);
        scheduleNextPaint.setAntiAlias(antiAlias);
        scheduleSubjectPaint.setAntiAlias(antiAlias);
    }

    public void setCurrentDrawer(ModeFaceDrawer<SuperTimeWrapper> drawer) {
        this.modeDrawer = drawer;
    }

    public JSONColors getColors() {
        return colors;
    }
}
