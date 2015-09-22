package net.parablack.clocktest.json.subjects;

import net.parablack.clocktest.json.subjects.WearEventSubject;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Simon on 16.09.2015.
 */
public class ScheduleSubject implements WearEventSubject{

    private String id;
    private String name;
    private String teacher;

    public ScheduleSubject(JSONObject obj) throws JSONException {
        this.id = obj.getString("id");
        name = obj.getString("name");
        teacher = obj.getString("teacher");
    }

    public String getId() {
        return id;
    }

    @Override
    public String getDisplayName() {
        return getName() + " bei " + getTeacher();
    }

    public String getName() {
        return name;
    }

    public String getTeacher() {
        return teacher;
    }
}
