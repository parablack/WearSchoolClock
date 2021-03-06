package net.parablack.clocktest.watchface;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.util.Log;
import android.view.SurfaceHolder;

import net.parablack.clocktest.transfer.ScheduleAssetListener;
import net.parablack.clocktest.watchface.drawer.WatchFaceDrawer;
import net.parablack.schedulelib.Schedule;
import net.parablack.schedulelib.color.ScheduleColors;

import org.json.JSONException;

import java.util.Calendar;
import java.util.TimeZone;

import static android.support.wearable.watchface.WatchFaceStyle.BACKGROUND_VISIBILITY_INTERRUPTIVE;
import static android.support.wearable.watchface.WatchFaceStyle.Builder;
import static android.support.wearable.watchface.WatchFaceStyle.PEEK_MODE_SHORT;


public class SchoolWatchFaceService extends CanvasWatchFaceService {

    private static SchoolWatchFaceService instance;

    private Engine watchEngine;


    public static SchoolWatchFaceService getInstance() {
        return instance;
    }

    public SchoolWatchFaceService() {
        instance = this;
    }

    @Override
    public Engine onCreateEngine() {
        /* provide your watch face implementation */
        watchEngine = new Engine();
        return watchEngine;
    }

    public Engine getWatchEngine() {
        return watchEngine;
    }

    /* implement service callback methods */
    public class Engine extends CanvasWatchFaceService.Engine {

        static final int MSG_UPDATE_TIME = 0;
        private static final int INTERACTIVE_UPDATE_RATE_MS = 1000;

        private ScheduleAssetListener assetListener;

        //     private JSONReader mainReader;
        //    private JSONSchedule mainSchedule;
        private Schedule mainSchedule;

        private boolean scheduleEnabled = true;

        private boolean reloading;

        private WatchFaceDrawer drawer;

        private WatchFaceTapper tapper;


        Calendar calendar;


        boolean burnInProtection, lowBitAmbient;

        final Handler mUpdateTimeHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_UPDATE_TIME:
                        invalidate();
                        if (shouldTimerBeRunning()) {
                            long timeMs = System.currentTimeMillis();
                            long delayMs = INTERACTIVE_UPDATE_RATE_MS
                                    - (timeMs % INTERACTIVE_UPDATE_RATE_MS);
                            mUpdateTimeHandler
                                    .sendEmptyMessageDelayed(MSG_UPDATE_TIME, delayMs);
                        }
                        break;
                }
                return true;
            }
        }
        );


        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);
            /* initialize your watch face */

            //mainReader = new JSONReader();

            tapper = new WatchFaceTapper(this);

            // Has to be instantiated later than main reader, because it references to mainReader
            drawer = new WatchFaceDrawer(SchoolWatchFaceService.this);

            scheduleEnabled = false;

            AsyncTask<Void, Void, Void> as = new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    mainSchedule = Schedule.fromSharedPrefs(SchoolWatchFaceService.this);
                    scheduleEnabled = true;

                    ScheduleColors colors = new ScheduleColors();
                    colors.loadFromPreferences(SchoolWatchFaceService.this);
                    drawer.updateColors(colors);

                    return null;
                }
            }.execute();

            assetListener = new ScheduleAssetListener();

            calendar = Calendar.getInstance();

            setWatchFaceStyle(new Builder(SchoolWatchFaceService.this)
                    .setCardPeekMode(PEEK_MODE_SHORT)
                    .setBackgroundVisibility(
                            BACKGROUND_VISIBILITY_INTERRUPTIVE)
                    .setShowSystemUiTime(false)
                    .setStatusBarGravity(4)
                    .setAcceptsTapEvents(true)
                    .build());


        }

//        public ScheduleProvider.SchoolHour currentScheduleHour(){
//            Calendar c = WeeklySchedule.currentCalendar();
//            String wName = new SimpleDateFormat("EEEE").format(c.getTime());
//            wName = wName.toUpperCase();
//            WeeklySchedule ws = WeeklySchedule.valueOf(wName);
//            return ws.getLast();
//        }

        @Override
        public void onPropertiesChanged(Bundle properties) {

            super.onPropertiesChanged(properties);
            /* get device features (burn-in, low-bit ambient) */

            burnInProtection = properties.getBoolean(PROPERTY_BURN_IN_PROTECTION,
                    false);
            lowBitAmbient = properties.getBoolean(PROPERTY_LOW_BIT_AMBIENT, false);
            Log.v("Clock","burnInProtection = " + burnInProtection + "; lowBitAmbient = " + lowBitAmbient);
        }


        @Override
        public void onTimeTick() {
            super.onTimeTick();
            /* the time changed */

            invalidate();
        }

        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);

            tapper.reset();


            drawer.onAmbientModeChanged(inAmbientMode);

            invalidate();
            updateTimer();

        }

        //   boolean alreadyVibrated = false;

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            /* draw your watch face */

            if (drawer != null)
                drawer.onDraw(canvas, bounds);


        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            /* the watch face became visible or invisible */


            if (visible) {

                // Update time zone in case it changed while we weren't visible.
                calendar.setTimeZone(TimeZone.getDefault());
            }
//            else {
//                //           unregisterReceiver();
//            }

            // Whether the timer should be running depends on whether we're visible and
            // whether we're in ambient mode, so we may need to start or stop the timer
            updateTimer();

        }

        /**
         * @return The Main-Schedule of the clock
         */
        public Schedule getMainSchedule() {
            return mainSchedule;
        }

        public void setMainSchedule(Schedule mainSchedule) {
            this.mainSchedule = mainSchedule;
        }

        public boolean shouldTimerBeRunning() {
            return isVisible() && !isInAmbientMode();
        }

        public void reload() {
            reloading = true;
            try {
                mainSchedule.reload();
            } catch (JSONException e) {
                e.printStackTrace();
                scheduleEnabled = false;
            }
        }

        private void updateTimer() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            if (shouldTimerBeRunning()) {
                mUpdateTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME);
            }
        }


        // Always called twice?!
        @Override
        public void onTapCommand(int tapType, int x, int y, long eventTime) {
            super.onTapCommand(tapType, x, y, eventTime);
            tapper.onClick(tapType, x, y, eventTime);

        }

        /**
         * @return Calendar-Object of the current class
         */
        public Calendar getCalendar() {
            return calendar;
        }

        /**
         * @return Wether schedule is enabled or not
         */
        public boolean isScheduleEnabled() {
            return scheduleEnabled;
        }


        /**
         * Notifys if a reload has (or is) taking place, if yes, it will be automatically set to false for the next check
         *
         * @return Reload has taken place since the last calls
         */
        public boolean notifyReload() {
            if (reloading) {
                reloading = false;
                return true;
            }
            return false;
        }

//        public JSONReader getMainReader() {
//            return mainReader;
//        }

        public WatchFaceDrawer getDrawer() {
            return drawer;
        }

        public void setDrawer(WatchFaceDrawer drawer) {
            this.drawer = drawer;
        }
        // handler to update the time once a second in interactive mode

        // receiver to update the time zone
//        final BroadcastReceiver mTimeZoneReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                calendar.setTimeZone(TimeZone.getDefault());
//                invalidate();
//            }
//        };

    }


}
