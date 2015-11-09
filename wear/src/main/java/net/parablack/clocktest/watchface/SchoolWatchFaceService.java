package net.parablack.clocktest.watchface;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.util.Log;
import android.view.SurfaceHolder;

import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;

import net.parablack.clocktest.json.InvalidDataException;
import net.parablack.clocktest.json.JSONReader;
import net.parablack.clocktest.json.JSONSchedule;
import net.parablack.clocktest.watchface.drawer.WatchFaceDrawer;
import net.parablack.clocktest.watchface.drawer.mode.ModeFaceDrawer;

import java.io.IOException;
import java.util.Calendar;
import java.util.TimeZone;

import static android.support.wearable.watchface.WatchFaceStyle.BACKGROUND_VISIBILITY_INTERRUPTIVE;
import static android.support.wearable.watchface.WatchFaceStyle.Builder;
import static android.support.wearable.watchface.WatchFaceStyle.PEEK_MODE_SHORT;


public class SchoolWatchFaceService extends CanvasWatchFaceService {

    private Engine watchEngine;

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

        private JSONReader mainReader;
        private JSONSchedule mainSchedule;
        private boolean scheduleEnabled = true;

        private boolean reloading;

        private WatchFaceDrawer drawer;

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

            mainReader = new JSONReader();

            // Has to be instantiated later than main reader, because it references to mainReader
            drawer = new WatchFaceDrawer(SchoolWatchFaceService.this);

            scheduleEnabled = false;

            AsyncTask<Void, Void, Void> as = new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        mainReader.parseData(getAssets());
                        mainSchedule = mainReader.getSchedule();
                        scheduleEnabled = true;
                        drawer.updateColors(getAssets().open("colors.json"));
                    } catch (InvalidDataException e) {
                        e.printStackTrace();
                        scheduleEnabled = false;
                    } catch (IOException e){
                        e.printStackTrace();
                        Log.w("Schedule", "Falling back into default colors!");
                    }

                    return null;
                }
            }.execute();


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
            System.out.println("burnInProtection = " + burnInProtection + "; lowBitAmbient = " + lowBitAmbient);
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
            /* the wearable switched between modes */

            if (lowBitAmbient) {
                boolean antiAlias = !inAmbientMode;
                drawer.setAntiAlias(antiAlias);
            }
//            if (inAmbientMode) {
//
//            }
            alreadyTapped = 0;
            invalidate();
            updateTimer();

        }

        //   boolean alreadyVibrated = false;

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            /* draw your watch face */

            if(drawer != null)
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
         *
         * @return The Main-Schedule of the clock
         */
        public JSONSchedule getMainSchedule() {
            return mainSchedule;
        }

        public boolean shouldTimerBeRunning() {
            return isVisible() && !isInAmbientMode();
        }

        private void updateTimer() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            if (shouldTimerBeRunning()) {
                mUpdateTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME);
            }
        }

        int alreadyTapped = 0;
        int alreadyDownRightTapped = 0;
        int colors_currLoaded = 1;
        // Always called twice?!
        @Override
        public void onTapCommand(int tapType, int x, int y, long eventTime) {
            super.onTapCommand(tapType, x, y, eventTime);


            if (x < 100 && y < 100) {
                alreadyTapped++;
            } else if (alreadyTapped >= 20) {
                if (x > 200 && y > 200) {
                    System.out.println("Forced reload --> Applying");
                    Reloader r = new Reloader();
                    r.execute();
                    reloading = true;
                    alreadyTapped = 0;
                    invalidate();
                }
            } else if (alreadyTapped >= 3) {
                if (x > 200 && y > 200) {
                    drawer.setCurrentDrawer(ModeFaceDrawer.ModeFaceDrawers.SINGLE_LINE);
                    alreadyTapped = 0;
                }
                if (x > 200 && y < 100) {
                    drawer.setCurrentDrawer(ModeFaceDrawer.ModeFaceDrawers.FULL_LINE);
                    alreadyTapped = 0;
                }
                if (x < 100 && y > 200) {
                    drawer.setCurrentDrawer(ModeFaceDrawer.ModeFaceDrawers.TEXT);
                    alreadyTapped = 0;
                }
                invalidate();
            }

            if(x > 250 && y > 250){
                alreadyDownRightTapped++;
            }else if(alreadyDownRightTapped >= 20){
                if(x < 100 && y < 100){
                    reloading = true;
                    new AsyncTask<Void, Void, Void>(){
                        @Override
                        protected Void doInBackground(Void... params) {
                            try {
                                if(colors_currLoaded == 1){
                                    drawer.updateColors(SchoolWatchFaceService.this.getAssets().open("colors2.json"));
                                    Log.i("Schedule", "Loading colors2");
                                    colors_currLoaded = 2;
                                }
                                else {
                                    drawer.updateColors(SchoolWatchFaceService.this.getAssets().open("colors.json"));
                                    colors_currLoaded = 1;
                                    Log.i("Schedule", "Loading colors");

                                }
                                invalidate();
                                alreadyDownRightTapped = 0;
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return null;
                        }
                    }.execute();
                }

            }


        }

        /**
         *
         * @return Calendar-Object of the current class
         */
        public Calendar getCalendar() {
            return calendar;
        }

        /**
         *
         * @return Wether schedule is enabled or not
         */
        public boolean isScheduleEnabled() {
            return scheduleEnabled;
        }

        private class Reloader extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... params) {
                getMainSchedule().reload();
                return null;
            }
        }

        /**
         * Notifys if a reload has (or is) taking place, if yes, it will be automatically set to false for the next check
         * @return Reload has taken place since the last calls
         */
        public boolean notifyReload() {
            if (reloading) {
                reloading = false;
                return true;
            }
            return false;
        }

        public JSONReader getMainReader() {
            return mainReader;
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
