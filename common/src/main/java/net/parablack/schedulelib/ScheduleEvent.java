package net.parablack.schedulelib;

import android.support.annotation.NonNull;


import net.parablack.schedulelib.utils.TimeUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class ScheduleEvent implements Comparable<WearEvent>, WearEvent{

    private String displayName;
    private int begin, end;   // Millis of day
    private int day;
    private int vibrateTime = -1;

    public ScheduleEvent(String displayName, int begin, int end, int day, int vibrateTime) {
        this.displayName = displayName;
        this.begin = begin;
        this.end = end;
        this.day = day;
        this.vibrateTime = vibrateTime;
    }

    public ScheduleEvent(JSONObject object) throws JSONException  {
        displayName = object.getString("display_name");
        begin = object.getInt("begin");
        end = object.getInt("end");
        day = object.getInt("day");
        vibrateTime = object.optInt("vibrate_time");

    }

    public JSONObject toJSON(){
        JSONObject obj = new JSONObject();
        try {
            obj.put("display_name", displayName);
            obj.put("begin", begin);
            obj.put("end", end);
            obj.put("day", day);
            obj.put("vibrate_time", vibrateTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    public int getBegin() {
        return begin;
    }

    public int getEnd() {
        return end;
    }

    public int getDay() {
        return day;
    }

    public int getVibrateTime() {
        return vibrateTime;
    }

    public String getName() {
        return displayName;
    }

    @Override
    public long getTimeTilEnd() {
        long dayMillis = TimeUtils.dayMillis();

        return getEnd() - dayMillis;
    }

    public String niceStartTime(){
        int mins = (getBegin() / 1000) / 60;
        int hour = (mins - (mins % 60)) / 60;
        int min = mins % 60;

        return hour + ":" + min;
    }

    public String niceEndTime(){
        int mins = getEnd() / (1000 * 60);
        int hour = (mins - (mins % 60)) / 60;
        int min = mins % 60;

        return hour + ":" + min;
    }

    @Override
    public int compareTo(@NonNull WearEvent another) {
        if(!(another instanceof ScheduleEvent)) return -1;
        if(getBegin() < ((ScheduleEvent) another).getBegin()) return -1;
        else return 1;
    }


    @Override
    public String toString() {
        return "ScheduleEvent[" + niceStartTime() + " - " + niceEndTime() + " (" + getBegin() + ", " + getEnd() + "): " + getName() ;
    }
}
