package net.parablack.clocktest.json;

import android.os.AsyncTask;
import android.util.Log;

import net.parablack.clocktest.watchface.WearEvent;
import net.parablack.clocktest.watchface.WearEvent.NothingUpEvent;
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
    private ArrayList<WearEvent> nextDayEvents = new ArrayList<>();


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
        nextDayEvents.clear();
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
                        Log.v("Clock", "JSONEvent added [today]: " + eve);
                    }
                });

                Collections.sort(events);

                for (JSONEvent jsE : events) Log.v("Clock", "Todays JSEs: " + jsE.toString());
            }

            // --------------------- TOMORROW ----------------
            {
                int weekDay = c.get(Calendar.DAY_OF_WEEK);
                nextDayEvents.clear();


                weekDay++;
                if (weekDay == 8) weekDay = 1;
                // 1 SUNDAY 2 MONDAY --> 7 SATURDAY
                Log.i("Clock", "Tomorrows day id: " + weekDay);
                String dayId = "d" + weekDay;

                JSONObject day = rootObject.getJSONObject(dayId);

                JSONArray schedule = day.getJSONArray("schedule");
                Log.v("Clock", "Schedule.length = " + schedule.length());

                JSONReaderUtil.fetchArray(schedule, new JSONReaderUtil.ScheduleInitCallback() {
                    @Override
                    public void callback(JSONObject object) throws JSONException {
                        JSONEvent eve = new JSONEvent(object, reader);
                        nextDayEvents.add(eve);
                        Log.v("Clock", "JSONEvent added [tomorrow]: " + eve);
                    }
                });

                Collections.sort(nextDayEvents);

//            if(nextDayEvents.size() == 0){
//                nextDayEvents.add(new WearEvent.NothingUpEvent("Ganzer Tag frei!"));
//            }

                for (WearEvent jsE : nextDayEvents)
                    Log.v("Clock", "Tommorows JSEs: " + jsE.toString());

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
                    // Das gefundene Event ist im aktuellen Zeitraum, es kann also angezeigt werden.
                    return each;
                }
            }
//            if (dayMillis < each.getTime().getBegin()) {
//                // Es gibt kein aktuelles Event, wir nehmen ein NothingUpEvent mit der Endzeit zu dem Zeitpunkt an dem das nächste reguläre Event beginnt.
//                return new NothingUpEvent(each.getTime().getBegin());
//            }
        }

        // Heute kein Event mehr

        if (_next instanceof JSONEvent) {
            JSONEvent ne = (JSONEvent) _next;
            return new NothingUpEvent(ne.getTime().getBegin()).setLabel("Frei bis morgen!");
        }

//        if (nextDayEvents.size() >= 1)
//            if (nextDayEvents.get(0) instanceof JSONEvent) {
//                return new NothingUpEvent(((JSONEvent) nextDayEvents.get(0)).getTime().getBegin(), "Frei bis morgen!");
//            } else return new NothingUpEvent("Ganzer Tag frei!");
//        else {
//
//        }
        return new NothingUpEvent().setLabel("Ganzer Tag frei!");
    }

    private WearEvent _loadNext() {
        long dayMillis = dayMillis();


        for (JSONEvent each : events) {
            if (dayMillis < each.getTime().getBegin()) {
                // No current event, this is the next one
                return each;
            }
        }

        if (nextDayEvents == null) {
            return new NothingUpEvent().setLabel("Error (nDE=null)!");
        }
        if (nextDayEvents.size() >= 1)
            return nextDayEvents.get(0);
        else {
            return new NothingUpEvent().setLabel("Morgen frei!");
        }
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
}
