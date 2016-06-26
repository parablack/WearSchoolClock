package net.parablack.schedulelib.color;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;

import net.parablack.schedulelib.utils.JSONReaderUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class ScheduleColors {

    private JSONObject rootObject;
    private HashMap<String, Integer> colors = new HashMap<>();


    public ScheduleColors() {
    }

    public ScheduleColors(JSONObject object) {
        this.rootObject = object;
        try {
            reload();
        } catch (JSONException e) {
            Log.e("Clock", "ScheduleColors: Invalid JSON String. Falling back to random colors");
            e.printStackTrace();
        }
    }


    public void reload() throws JSONException {
        Iterator<String> keys = rootObject.keys();

        while (keys.hasNext()) {
            String key = keys.next();


            try {
                colors.put(key, rootObject.getInt(key));
            } catch (Exception e) {
                Log.w("Clock", key + " has invalid color: " + rootObject.getString(key));
                try{
                    colors.put(key, Color.parseColor(rootObject.getJSONObject(key).getString("defaultValue")));
                }catch (Exception e1){
                    Log.e("Clock", key + " has invalid color: (no hex too & no default) " + rootObject.getString(key));
                    e1.printStackTrace();
                }
            }
        }
        Log.d("Clock", "Loaded " + colors.size() + " entries to ColorCreator");
    }


    private void loadDefaultValues(Context con) {
        Log.e("Clock", "Invalid data found. Falling back to default data!");

        JSONObject rootObject = JSONReaderUtil.byAsset(con.getAssets(), "colors.json");
        assert rootObject != null;
    }




    public String toJSON() {
        JSONObject obj = new JSONObject();
        for (String key : colors.keySet()) {
            try {
                obj.put(key, colors.get(key));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return obj.toString();
    }

    public Set<String> getColorNames() {
        return colors.keySet();
    }

    public void setColor(String key, int value) {
        colors.put(key, value);
    }

    public int getColor(String key) {
        if(colors.get(key) != null) return colors.get(key);
        Log.w("Clock", "getColor: Color not found! Returning random!");
        return Color.rgb((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255));
    }


    public void loadFromPreferences(Context con){
        SharedPreferences pref = con.getSharedPreferences("SchoolClock_Colors", Context.MODE_PRIVATE);
        try {
            JSONObject ob = new JSONObject(pref.getString("colors_json", "Error"));
            this.rootObject = ob;
            reload();
        } catch (JSONException e) {
            Log.e("Clock", "loadFromPreferences: Loading values from Preferences failed!");
        }
    }

    public void saveToPreferences(Context con) {
        SharedPreferences pref = con.getSharedPreferences("SchoolClock_Colors", Context.MODE_PRIVATE);
        pref.edit().putString("colors_json", toJSON()).apply();
        Log.i("Clock", "saveToPreferences: Preferences saved!");
    }


}

