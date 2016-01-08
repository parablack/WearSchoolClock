package net.parablack.clocktest.json;

import android.os.AsyncTask;

import net.parablack.clocktest.watchface.WearEvent;
import net.parablack.clocktest.watchface.WearSchedule;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import static net.parablack.clocktest.watchface.TimeUtils.currentCalendar;
import static net.parablack.clocktest.watchface.TimeUtils.dayMillis;

// TODO well this is pretty ugly
public class JSONSchedule implements WearSchedule {

    private JSONObject rootObject;
    private JSONReader reader;

    private ArrayList<JSONEvent> events = new ArrayList<>();
    private ArrayList<JSONEvent> tomorrowEvents = new ArrayList<>();


    public JSONSchedule(JSONObject rootObject, JSONReader reader) {
        this.rootObject = rootObject;
        this.reader = reader;

        reload();
    }


    /**
     * Reloads the schedule by reparsing it from JSON
     * This method is executed async. If you want to reload
     */
    @Override
    public void reload() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                syncReload();
                return null;
            }
        }.doInBackground();
    }

    public void syncReload() {
        Calendar c = currentCalendar();
        tomorrowEvents.clear();
        events.clear();

        try {
            // --------------------- TODAY ----------------
            {
                int weekDay = c.get(Calendar.DAY_OF_WEEK);

                // 1 SUNDAY 2 MONDAY --> 7 SATURDAY
                String dayId = "d" + weekDay;


                JSONObject day = rootObject.getJSONObject(dayId);
                JSONArray schedule = day.getJSONArray("schedule");
                JSONReaderUtil.fetchArray(schedule, new JSONReaderUtil.ScheduleInitCallback() {
                    @Override
                    public void callback(JSONObject object) throws JSONException {
                        JSONEvent eve = new JSONEvent(object, reader);
                        events.add(eve);
                        System.out.println("JSONEvent added: " + eve);
                    }
                });

                Collections.sort(events);

                for (JSONEvent jsE : events) System.out.println("Todays JSEs: " + jsE.toString());
            }

            // --------------------- TOMORROW ----------------
            {
                int weekDay = c.get(Calendar.DAY_OF_WEEK);

                weekDay++;
                if (weekDay == 8) weekDay = 1;
                // 1 SUNDAY 2 MONDAY --> 7 SATURDAY
                String dayId = "d" + weekDay;

                JSONObject day = rootObject.getJSONObject(dayId);

                JSONArray schedule = day.getJSONArray("schedule");
                JSONReaderUtil.fetchArray(schedule, new JSONReaderUtil.ScheduleInitCallback() {
                    @Override
                    public void callback(JSONObject object) throws JSONException {
                        JSONEvent eve = new JSONEvent(object, reader);
                        tomorrowEvents.add(eve);
                        System.out.println("JSONEvent added: " + eve);
                    }
                });

                Collections.sort(tomorrowEvents);

                for (JSONEvent jsE : tomorrowEvents)
                    System.out.println("Tommorows JSEs: " + jsE.toString());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        reloadEvents();

    }

    private WearEvent _current, _next;

    protected void reloadEvents() {
        _next = _loadNext();
        _current = _loadCurrent();
    }

    private WearEvent _loadCurrent() {
        long dayMillis = dayMillis();


        for (JSONEvent each : events) {
            if (dayMillis < each.getTime().getEnd()) {
                if (dayMillis > each.getTime().getBegin()) { // Just to make sure its in the time window, shall never happen that not
                    //Event is now
                    return each;
                }
            }
            if (dayMillis < each.getTime().getBegin()) {
                // No current event, this is the next one
                return new WearEvent.NothingUpEvent(each.getTime().getBegin());
            }
        }
        if (tomorrowEvents.size() >= 1)
            return new WearEvent.NothingUpEvent(tomorrowEvents.get(0).getTime().getBegin());
        else {
            if (_next instanceof JSONEvent) {
                JSONEvent ne = (JSONEvent) _next;

                return new WearEvent.NothingUpEvent(ne.getTime().getBegin());
            }

        }
        return new WearEvent.NothingUpEvent();
    }

    private WearEvent _loadNext() {
        long dayMillis = dayMillis();


        for (JSONEvent each : events) {
            if (dayMillis < each.getTime().getBegin()) {
                // No current event, this is the next one
                return each;
            }
        }

        if (tomorrowEvents != null && tomorrowEvents.size() >= 1)
            return tomorrowEvents.get(0);
        else {
            int weekDay = currentCalendar().get(Calendar.DAY_OF_WEEK);
            weekDay = incrWeekNumber(weekDay);

            try {
                for (ArrayList ar = getForDayNumber(weekDay); ar.size() <= 0; ar = getForDayNumber(weekDay = incrWeekNumber(weekDay))) {
                    System.out.println("ar + ar.size() = " + ar + ar.size() + " " + weekDay);
                }
                ArrayList<JSONEvent> ar2 = getForDayNumber(weekDay);
                System.out.println("ar2 (" + weekDay + ")= " + ar2);
                int finalWeekDay = weekDay;
                return ar2.get(0);
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

        return new WearEvent.NothingUpEvent();
    }

    @Override
    public WearEvent getCurrent() {
        if (_current == null || _current.getTimeTilEnd() < 0) reloadEvents();
        return _current;

    }

    @Override
    public WearEvent getNext() {
        if (_next == null) reloadEvents();
        return _next;

    }

    private static int incrWeekNumber(int num) {
        return num + 1 == 8 ? 1 : num + 1;
    }


//
//
//    public ScheduleProvider.SchoolHour getLast() {
//        long dayMillis = (currentCalendar().getTimeInMillis() + currentCalendar().get(Calendar.DST_OFFSET) + currentCalendar().get(Calendar.ZONE_OFFSET)) % (24 * 60 * 60 * 1000);
//        System.out.println("dayMillis = " + dayMillis);
//        ScheduleProvider.SchoolHour hF = null;
//        long diff = Long.MAX_VALUE;
//        for (ScheduleProvider.SchoolHour h : hours) {
//            if (dayMillis > h.beginMillis) {
//                long cDiff = dayMillis - h.beginMillis;
//                if (cDiff < diff) {
//                    hF = h;
//                    diff = cDiff;
//                }
//            }
//        }
//        if (hF.getTimeTillEnd() < 0) {
//            // No valid entry there?
//            return ScheduleProvider.SchoolHour.NO_SCHOOL;
//        }
//        return hF;
//    }


    private ArrayList<JSONEvent> getForDayNumber(int number) throws JSONException {

        final ArrayList<JSONEvent> toR = new ArrayList<>();
        // 1 SUNDAY 2 MONDAY --> 7 SATURDAY
        String dayId = "d" + number;

        JSONObject day = rootObject.getJSONObject(dayId);

        JSONArray schedule = day.getJSONArray("schedule");
        JSONReaderUtil.fetchArray(schedule, new JSONReaderUtil.ScheduleInitCallback() {
            @Override
            public void callback(JSONObject object) throws JSONException {
                JSONEvent eve = new JSONEvent(object, reader);
                toR.add(eve);
                System.out.println("JSONEvent added: " + eve);
            }
        });

        Collections.sort(toR);

        for (JSONEvent jsE : toR) System.out.println("Tommorows JSEs: " + jsE.toString());
        return toR;
    }
}
