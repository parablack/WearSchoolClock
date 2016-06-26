package net.parablack.schedulelib;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import net.parablack.schedulelib.utils.JSONReaderUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import static net.parablack.schedulelib.utils.TimeUtils.currentCalendar;

public class Schedule implements WearSchedule {

    private JSONObject obj;

    private ScheduleDay[] days = new ScheduleDay[8];

    public Schedule() {
    }

    public void addDay(ScheduleDay day) {
        days[day.getId()] = day;
    }


    public Schedule(JSONObject object) throws JSONException {
        obj = object;
        reload();
    }


    public JSONObject toJSON() {
        JSONArray arr = new JSONArray();
        for (int i = 1; i < 8; i++)
            if (days[i] != null)
                arr.put(days[i].toJSON());

        JSONObject root = new JSONObject();
        try {
            root.put("schedule", arr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return root;

    }

    @Override
    public void reload() throws JSONException {
        JSONArray array = obj.getJSONArray("schedule");

        JSONReaderUtil.fetchArray(array, new JSONReaderUtil.ScheduleInitCallback() {
            @Override
            public void callback(JSONObject object) throws JSONException {
                ScheduleDay day = new ScheduleDay(object, Schedule.this);
                days[day.getId()] = day;
            }
        });
    }


    public ScheduleDay getDayAfter(int id) {
        if (id < 0) return null;
        if (id >= 7) return days[1];
        return days[++id];


    }

    @Override
    public WearEvent getCurrent() {
        return days[weekDay()].getCurrent();
    }

    @Override
    public WearEvent getNext() {
        return days[weekDay()].getNext();
    }

    public ScheduleDay[] getDays() {
        return days;
    }

    public void toPrefs(Context con){
        SharedPreferences pref = con.getSharedPreferences("SchoolClock_schedule", Context.MODE_PRIVATE);
        pref.edit().putString("schedule_json", toJSON().toString()).apply();
    }

    private static int weekDay() {
        return currentCalendar().get(Calendar.DAY_OF_WEEK);
    }

    public static Schedule fromSharedPrefs(Context con) {
        SharedPreferences pref = con.getSharedPreferences("SchoolClock_schedule", Context.MODE_PRIVATE);
        Schedule _sched;
        String json = pref.getString("schedule_json", "Error");
        Log.d("Clock", "[Schedule] Preferences loaded: [" + json + "]");

        try {
            _sched = new Schedule(new JSONObject(json));
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("Clock", "No saved schedule found!");

            _sched = emptySchedule();
        }
        return _sched;
    }

    public static Schedule emptySchedule(){
        Schedule schedule = new Schedule();

        schedule.addDay(new ScheduleDay(1, schedule)); //SUNDAY
        schedule.addDay(new ScheduleDay(2, schedule));
        schedule.addDay(new ScheduleDay(3, schedule));
        schedule.addDay(new ScheduleDay(4, schedule));
        schedule.addDay(new ScheduleDay(5, schedule));
        schedule.addDay(new ScheduleDay(6, schedule));
        schedule.addDay(new ScheduleDay(7, schedule));
        return schedule;
    }

}
