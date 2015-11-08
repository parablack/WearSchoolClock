package net.parablack.clocktest.json;

import android.graphics.Color;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONColors {
    private JSONObject rootObject;

    private int secondsTime = Color.WHITE, subjectNext = Color.WHITE, subjectCurrent = Color.WHITE, mainTime = Color.WHITE,
            full_done = Color.WHITE, full_percentage = Color.WHITE, full_todo = Color.WHITE;

    public JSONColors(JSONObject rootObject) {
        this.rootObject = rootObject;

        reload();

    }

    public void reload() {
        try {
            secondsTime = Color.parseColor(rootObject.getString("secondsTime"));
            subjectNext = Color.parseColor(rootObject.getString("subjectNext"));
            subjectCurrent = Color.parseColor(rootObject.getString("subjectCurrent"));
            mainTime = Color.parseColor(rootObject.getString("mainTime"));

//            full_done = Color.parseColor(rootObject.getString("full_done"));
//            full_percentage = Color.parseColor(rootObject.getString("full_percentage"));
//            full_todo = Color.parseColor(rootObject.getString("full_todo"));
        } catch (JSONException e) {
            e.printStackTrace();

            // It is already set to white...

        }
    }

    public JSONObject getRootObject() {
        return rootObject;
    }

    public int getSecondsTime() {
        return secondsTime;
    }

    public int getSubjectNext() {
        return subjectNext;
    }

    public int getSubjectCurrent() {
        return subjectCurrent;
    }

    public int getMainTime() {
        return mainTime;
    }

    public int getColorByName(String name) throws InvalidDataException {
        try {
            return Color.parseColor(rootObject.getString(name));
        } catch (JSONException e) {
            throw new InvalidDataException("Color not found");
        }
    }

//    public int getFull_done() {
//        return full_done;
//    }
//
//    public int getFull_percentage() {
//        return full_percentage;
//    }
//
//    public int getFull_todo() {
//        return full_todo;
//    }
}
