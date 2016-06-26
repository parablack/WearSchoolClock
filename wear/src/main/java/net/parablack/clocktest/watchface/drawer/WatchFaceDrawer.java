package net.parablack.clocktest.watchface.drawer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Vibrator;
import android.util.Log;

import net.parablack.clocktest.watchface.SchoolWatchFaceService;
import net.parablack.clocktest.watchface.drawer.mode.ModeFaceDrawer;
import net.parablack.clocktest.watchface.drawer.mode.ScheduleDrawException;
import net.parablack.clocktest.watchface.drawer.mode.TextDrawer;
import net.parablack.clocktest.watchface.drawer.mode.wrapper.SuperTimeWrapper;
import net.parablack.clocktest.watchface.drawer.mode.wrapper.TimeException;
import net.parablack.schedulelib.ScheduleEvent;
import net.parablack.schedulelib.WearEvent;
import net.parablack.schedulelib.color.ScheduleColors;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static net.parablack.clocktest.watchface.drawer.mode.wrapper.SuperTimeWrapper.TimeWrapper;


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

    private ScheduleColors colors;

    private ModeFaceDrawer<SuperTimeWrapper> modeDrawer;


    public WatchFaceDrawer(SchoolWatchFaceService service) {
        engine = service.getWatchEngine();
        colors = new ScheduleColors();
        vibrator = (Vibrator) service.getSystemService(Context.VIBRATOR_SERVICE);
        modeDrawer = new TextDrawer(this);
    }


    private void loadColors() {
        hourPaint.setColor(colors.getColor("mainTime"));
        minutePaint.setColor(colors.getColor("mainTime"));
        doubleColonPaintHours.setColor(colors.getColor("mainTime"));

        secondPaint.setColor(colors.getColor("secondsTime")); // 0A63F2 is das von ir 09ED0D         TOBI #FF9C9C

        scheduleSubjectPaint.setColor(colors.getColor("subjectCurrent")); // #F2D70A        // #A1A9FF tobi
        scheduleNextPaint.setColor(colors.getColor("subjectNext")); // FF9100 ist das orange von mir       // #A1A9FF tobi

        datePaint.setColor(colors.getColor("date"));

    }


    public void updateColors(ScheduleColors colors){
        this.colors = colors;

        modeDrawer.reloadColors();
        loadColors();
    }

    public void onDraw(Canvas canvas, Rect bounds) {
            /* draw your watch face */

        engine.getCalendar().setTimeInMillis(System.currentTimeMillis());

        int hourOfDay = engine.getCalendar().get(Calendar.HOUR_OF_DAY), minute = engine.getCalendar().get(Calendar.MINUTE);

        String hourString = String.format(Locale.GERMANY,"%02d", hourOfDay);
        String minuteString = String.format(Locale.GERMANY, "%02d", minute);
        String secondString = String.format(Locale.GERMANY, "%02d", engine.getCalendar().get(Calendar.SECOND));

        // Doesn't work?!
//        if(hourOfDay == 0 && minute == 1){
//            engine.getMainSchedule().reload();
//        }


        String dateString = new SimpleDateFormat("EEE, dd. MMM", Locale.GERMANY).format(engine.getCalendar().getTime());
        //      String dateString2 = new SimpleDateFormat("MMMM yyyy").format(calendar.getTime());

        canvas.drawColor(colors.getColor("background"));

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

            if (currentEvent instanceof ScheduleEvent) {
                ScheduleEvent e = (ScheduleEvent) currentEvent;
                e.checkVibrate(vibrator);


            }

        }

        // --------------- DRAW TIME TIL END ---------------------
        if (engine.shouldTimerBeRunning()) {

            canvas.drawText(secondString, centerX + 80, centerY + 5, secondPaint);
        }

            if (engine.isScheduleEnabled()) {

                long toEnd = engine.getMainSchedule().getCurrent().getTimeTilEnd();
                if (toEnd < 0) Log.w("Clock","Woa, toEnd < 0, this should never happen!");

                try{

                    TimeWrapper timeWrapperToEnd =TimeWrapper.fromMillis(toEnd);
                    TimeWrapper fromBegin = new SuperTimeWrapper.ErrorTimeWrapper();

                    if (engine.getMainSchedule().getCurrent() instanceof ScheduleEvent) {
                        ScheduleEvent jse = (ScheduleEvent) engine.getMainSchedule().getCurrent();

                        long begin = jse.getTimeFromBeginning();
                        fromBegin = TimeWrapper.fromMillis(begin);

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
        datePaint.setAntiAlias(antiAlias);
        scheduleNextPaint.setAntiAlias(antiAlias);
        scheduleSubjectPaint.setAntiAlias(antiAlias);
    }

    public void setCurrentDrawer(ModeFaceDrawer<SuperTimeWrapper> drawer) {
        this.modeDrawer = drawer;
    }

    public ScheduleColors getColors() {
        return colors;
    }
}
