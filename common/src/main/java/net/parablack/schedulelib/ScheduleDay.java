package net.parablack.schedulelib;

import android.util.Log;


import net.parablack.schedulelib.utils.JSONReaderUtil;
import net.parablack.schedulelib.utils.TimeUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class ScheduleDay {

    private int id;
    private Schedule mainSchedule;
    private ArrayList<ScheduleEvent> events = new ArrayList<>();


    public ScheduleDay(int id, Schedule mainSchedule) {
        this.id = id;
        this.mainSchedule = mainSchedule;
    }

    public void addEvent(ScheduleEvent event){
        events.add(event);
        Collections.sort(events);
    }

    public void removeEvent(ScheduleEvent scheduleEvent){
        events.remove(scheduleEvent);
        reloadEvents();
    }

    public ScheduleDay(JSONObject json, Schedule mainSchedule) throws JSONException {

        id = json.getInt("id");
        this.mainSchedule = mainSchedule;
        JSONArray schedule = json.getJSONArray("schedule");

        JSONReaderUtil.fetchArray(schedule, new JSONReaderUtil.ScheduleInitCallback() {
            @Override
            public void callback(JSONObject object) throws JSONException {
                ScheduleEvent eve = new ScheduleEvent(object);
                events.add(eve);
                Log.v("Clock", "ScheduleEvent added ["+id+"]: " + eve);
            }
        });

        Collections.sort(events);

    }

    public JSONObject toJSON(){

        JSONObject root = new JSONObject();
        try {
            root.put("id", id);
            JSONArray arr = new JSONArray();
            for(ScheduleEvent eve : events) arr.put(eve.toJSON());
            root.put("schedule", arr);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return root;

    }

    public int getId() {
        return id;
    }

    private WearEvent _current, _next;

    protected void reloadEvents() {
        _next = _loadNext();
        _current = _loadCurrent();
    }

    private boolean _nextIsToday = false;

    private WearEvent _loadCurrent() {
        long dayMillis = TimeUtils.dayMillis();

        for (ScheduleEvent each : events) {
            if (dayMillis < each.getEnd()) {
                if (dayMillis > each.getBegin()) { // Just to make sure its in the time window, shall never happen that not
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

        if (_next instanceof ScheduleEvent) {
            ScheduleEvent ne = (ScheduleEvent) _next;
            if(_nextIsToday) return new WearEvent.NothingUpEvent(ne.getBegin()).setLabel("Pause");
                else return new WearEvent.NothingUpEvent(ne.getBegin()).setLabel("Frei bis morgen!");
        }

//        if (nextDayEvents.size() >= 1)
//            if (nextDayEvents.get(0) instanceof JSONEvent) {
//                return new NothingUpEvent(((JSONEvent) nextDayEvents.get(0)).getTime().getBegin(), "Frei bis morgen!");
//            } else return new NothingUpEvent("Ganzer Tag frei!");
//        else {
//
//        }
        return new WearEvent.NothingUpEvent().setLabel("Ganzer Tag frei!");
    }

    private WearEvent _loadNext() {
        long dayMillis = TimeUtils.dayMillis();

        for (ScheduleEvent each : events) {
            if (dayMillis < each.getBegin()) {
                // No current event, this is the next one
                _nextIsToday = true;
                return each;
            }
        }

        if (mainSchedule.getDayAfter(id).events == null) {
            return new WearEvent.NothingUpEvent().setLabel("Error (nDE=null)!");
        }
        if (mainSchedule.getDayAfter(id).events.size() >= 1) {
            _nextIsToday = false;
            return mainSchedule.getDayAfter(id).events.get(0);
        }
            else {
            return new WearEvent.NothingUpEvent().setLabel("Morgen frei!");
        }
    }

    public WearEvent getCurrent() {
        if (_current == null || _current.getTimeTilEnd() < 0) reloadEvents();
        return _current;
    }

    public WearEvent getNext() {
        if (_next == null) reloadEvents();
        return _next;
    }

    public ArrayList<ScheduleEvent> getEvents() {
        return events;
    }
}
