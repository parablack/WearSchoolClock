package net.parablack.schedulelib;

import android.os.Vibrator;
import android.support.annotation.NonNull;


import net.parablack.schedulelib.utils.TimeUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class ScheduleEvent implements Comparable<WearEvent>, WearEvent{

    private static final String[] dayNames = {
            "",
            "Sonntag",
            "Montag",
            "Dienstag",
            "Mittwoch",
            "Donnerstag",
            "Freitag",
            "Samstag",
    };

    private String displayName;
    private int begin, end;   // Millis of day
    private int day;
    private int vibrateTime = -1;
    private boolean alreadyVibrated;

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

    public void setBegin(int begin) {
        this.begin = begin;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
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

    public long getTimeFromBeginning(){
        long dayMillis = TimeUtils.dayMillis();
        return dayMillis - getBegin();
    }

    public String niceStartTime(){
        return String.format(Locale.GERMANY, "%02d:%02d", getBeginHour(), getBeginMinute());
    }

    public String niceEndTime(){
        return String.format(Locale.GERMANY, "%02d:%02d", getEndHour(), getEndMinute());
    }

    public String niceDay(){
        return dayNames[getDay()];
    }

    public int getBeginMinute(){
        int mins = getBegin() / (1000 * 60);
        return mins % 60;
    }
    public int getBeginHour(){
        int mins = getBegin() / (1000 * 60);
        return (mins - (mins % 60)) / 60;
    }
    public int getEndMinute(){
        int mins = getEnd() / (1000 * 60);
        return mins % 60;
    }
    public int getEndHour(){
        int mins = getEnd() / (1000 * 60);
        return (mins - (mins % 60)) / 60;
    }

    @Override
    public int compareTo(@NonNull WearEvent another) {
        if(!(another instanceof ScheduleEvent)) return -1;
        if(getBegin() < ((ScheduleEvent) another).getBegin()) return -1;
        else return 1;
    }


    public boolean checkVibrate(Vibrator vibrator){
        if(!alreadyVibrated) {

            int minTilEnd = (int) ((getTimeTilEnd() / 1000) / 60);
            if (getVibrateTime() == minTilEnd) {
                long[] vibrationPattern = {0, 50, 50, 50, 50,
                        50, 50, 50};
                //-1 - don't repeat
                final int indexInPatternToRepeat = -1;
                vibrator.vibrate(vibrationPattern, indexInPatternToRepeat);
                alreadyVibrated = true;
                return true;
            }
        }
        return false;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public void setVibrateTime(int vibrateTime) {
        this.vibrateTime = vibrateTime;
    }

    @Override
    public String toString() {
        return "ScheduleEvent[" + niceStartTime() + " - " + niceEndTime() + " (" + getBegin() + ", " + getEnd() + "): " + getName() ;
    }
}
