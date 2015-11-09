package net.parablack.clocktest.json;

import android.graphics.Color;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONColors {
    private JSONObject lastObject;

    private int secondsTime = Color.WHITE, subjectNext = Color.WHITE, subjectCurrent = Color.WHITE, mainTime = Color.WHITE,
            full_done = Color.WHITE, full_percentage = Color.WHITE, full_todo = Color.WHITE;

    public JSONColors() {
    }

    public JSONColors(JSONObject lastObject) {
        this.lastObject = lastObject;
        reparse();
    }

    public void reload(JSONObject lastObject) {
        this.lastObject = lastObject;
        try {

            secondsTime = Color.parseColor(lastObject.getString("secondsTime"));
            subjectNext = Color.parseColor(lastObject.getString("subjectNext"));
            subjectCurrent = Color.parseColor(lastObject.getString("subjectCurrent"));
            mainTime = Color.parseColor(lastObject.getString("mainTime"));

//            full_done = Color.parseColor(rootlast.getString("full_done"));
//            full_percentage = Color.parseColor(rootlast.getString("full_percentage"));
//            full_todo = Color.parseColor(rootlast.getString("full_todo"));
        } catch (JSONException e) {
            e.printStackTrace();

            // It is already set to white...

        }
    }

    public void reparse(){
        reload(lastObject);
    }


    public JSONObject getLastObject() {
        return lastObject;
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
            return Color.parseColor(lastObject.getString(name));
        } catch (Exception e) {
            Log.w("Schedule", "getColorByName: Color not found, returning white (Color: "+name+"), ("+e.getClass().getSimpleName()+")");
            return Color.WHITE;
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
