package net.parablack.clocktest.json;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Simon on 16.09.2015.
 */
public class ScheduleTime {

    private String id;
    private long begin;
    private long end;

    public ScheduleTime(JSONObject obj) throws JSONException {
        this.id = obj.getString("id");
        this.begin = obj.getLong("begin");
        this.end = obj.getLong("end");
    }

    public String getId() {
        return id;
    }

    public long getBegin() {
        return begin;
    }

    public long getEnd() {
        return end;
    }
}
