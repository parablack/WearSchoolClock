package net.parablack.clocktest.json;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Simon on 16.09.2015.
 */
public class ScheduleMeta {

    private String id;
    private String name;
    private ScheduleVibration vibration;

    public ScheduleMeta(JSONObject obj) throws JSONException {
        this.id = obj.getString("id");
        this.name = obj.getString("name");
        this.vibration = new ScheduleVibration(obj.getJSONObject("vibration"));
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public ScheduleVibration getVibration() {
        return vibration;
    }

    public class ScheduleVibration {

        private int count;
        private int duration;
        private int timeBefore;

        protected ScheduleVibration(JSONObject obj) throws JSONException {
            this.count = obj.getInt("count");
            this.duration = obj.getInt("duration");
            this.timeBefore = obj.getInt("time");
        }

        public int getCount() {
            return count;
        }

        public int getDuration() {
            return duration;
        }

        public int getTimeBefore() {
            return timeBefore;
        }
    }

}
