package net.parablack.clocktest.json;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;

import org.json.JSONObject;

public class JSONColors {
    private JSONObject lastObject;

    private int secondsTime = Color.WHITE, subjectNext = Color.WHITE, subjectCurrent = Color.WHITE, mainTime = Color.WHITE, background = Color.BLACK;

    public JSONColors() {
    }

    public JSONColors(JSONObject lastObject) {
        this.lastObject = lastObject;
        reparse();
    }

    public void reload(JSONObject lastObject) {
        this.lastObject = lastObject;

        secondsTime = getColorByName("secondsTime");
        subjectNext = getColorByName("subjectNext");
        subjectCurrent = getColorByName("subjectCurrent");
        mainTime = getColorByName("mainTime");
        background = getColorByName("background");
    }

    public void reparse() {
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

    public int getBackground() {
        return background;
    }

    public int getColorByName(String name) {
        try {
            try {
                // TODO : DEPRECATED, now ints are used for color representation, no hex anymore
                return Color.parseColor(lastObject.getString(name));
            } catch (IllegalArgumentException e) {
                return lastObject.getInt(name);
            }
        } catch (Exception e) {
            Log.w("Schedule", "getColorByName: Color not found, returning black (Color: " + name + "), (" + e.getClass().getSimpleName() + ")");
            return Color.BLACK;
        }
    }

    public void save(SharedPreferences pref){
        pref.edit().putString("json_colors", lastObject.toString()).apply();
    }


}
