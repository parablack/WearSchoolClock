package net.parablack.clocktest.schedule.kvfgparser;



import net.parablack.schedulelib.Schedule;
import net.parablack.schedulelib.ScheduleDay;
import net.parablack.schedulelib.ScheduleEvent;

import org.json.JSONException;
import org.json.JSONObject;

public class KvFGParser {

    private static final String[] HOURS  =
            {
                        "8:00",
                        "8:45",
                        "9:50",
                        "10:40",
                        "11:45",
                        "12:35",
                        "13:20",
                        "14:10",
                        "15:00",
                        "15:55",
                        "16:40"
            };

    private static final int HOUR_DURATION = 2700000;


    private static int[] _hours = new int[11];


    private static void initArray(){
        for(int i = 0; i < 11; i++){
            String hS = HOURS[i];

            int hours = Integer.parseInt(hS.split(":")[0]);
            int min = Integer.parseInt(hS.split(":")[1]);

            int millis = ((hours * 60) + min) * 60 * 1000;
            _hours[i] = millis;
        }


    }

    public static Schedule getFromKvFGJSON(String json) throws JSONException {

        initArray();

        JSONObject obj = new JSONObject(json);
        Schedule schedule = new Schedule();

        schedule.addDay(new ScheduleDay(1)); //SUNDAY
        schedule.addDay(new ScheduleDay(7)); //SATURDAY


        for(int i = 0; i < 5; i++){

            JSONObject day = obj.getJSONObject(i + "");
            ScheduleDay sDay = new ScheduleDay(i + 2);

            for(int j = 0; j < 11; j++){

                JSONObject fach = day.getJSONObject(j + "");
                if(fach.getString("s").equals("")) continue;

                String displayName = fach.getString("s") + " bei " + fach.getString("t") + " (" + fach.getString("r") + ")";

                int begin = _hours[j];
                int end = begin + HOUR_DURATION;

                ScheduleEvent event = new ScheduleEvent(displayName, begin, end, i + 2, 3);
                sDay.addEvent(event);
            }
            schedule.addDay(sDay);
        }
        return schedule;
    }




}


