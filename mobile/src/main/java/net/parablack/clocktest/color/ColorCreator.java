package net.parablack.clocktest.color;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;

import net.parablack.clocktest.json.JSONReaderUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class ColorCreator {

    private String json;
    private JSONObject rootObject;

    private HashMap<String, Integer> colors = new HashMap<>();
    private static HashMap<String, String> displayNames = new HashMap<>();

    private void loadDefaultValues(Context con) {
        Log.e("Clock", "Invalid data found. Falling back to default data!");

        JSONObject rootObject = JSONReaderUtil.byAsset(con.getAssets(), "colors.json");
        assert rootObject != null;
        Iterator<String> keys = rootObject.keys();
        while (keys.hasNext()) {
            String key = keys.next();


            try {
                colors.put(key, Color.parseColor(rootObject.getJSONObject(key).getString("defaultValue")));
            } catch (Exception e) {
                Log.e("Clock", "Default JSON is corrupted. Aborting whole process. (key=" + key + ")");
                e.printStackTrace();
            }
        }
        saveToPreferences(con);

    }

    private static void loadDisplayNames(Context con) {
        Log.i("Clock", "Loading display names.");

        JSONObject rootObject = JSONReaderUtil.byAsset(con.getAssets(), "colors.json");

        assert rootObject != null;
        Iterator<String> keys = rootObject.keys();

        while (keys.hasNext()) {
            String key = keys.next();


            try {
                displayNames.put(key, rootObject.getJSONObject(key).getString("displayName"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public ColorCreator(Context con) {
        this(con, false);
    }

    public ColorCreator(Context con, boolean defaultValue) {
        loadDisplayNames(con);
        SharedPreferences pref = con.getSharedPreferences("SchoolClock_Colors", Context.MODE_PRIVATE);
        String json = pref.getString("colors_json", "Error");
        Log.d("Clock", "Preferences loaded: [" + json + "]");
        try {
            if(defaultValue) loadDefaultValues(con);
                else loadFromJSON(json);
        } catch (JSONException e) {
            loadDefaultValues(con);

            //e.printStackTrace();
        }

    }


    private void loadFromJSON(String json) throws JSONException {
        rootObject = new JSONObject(json);

        Iterator<String> keys = rootObject.keys();

        while (keys.hasNext()) {
            String key = keys.next();


            try {
                colors.put(key, rootObject.getInt(key));
            } catch (Exception e) {
                Log.e("Clock", key + " has invalid color: " + rootObject.getString(key));
            }
        }
        Log.d("Clock", "Loaded " + colors.size() + " entries to ColorCreator");
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
        return colors.get(key);
    }

    public static String getDisplayName(String id) {
        return displayNames.get(id);
    }

    public void saveToPreferences(Context con) {
        SharedPreferences pref = con.getSharedPreferences("SchoolClock_Colors", Context.MODE_PRIVATE);
        pref.edit().putString("colors_json", toJSON()).apply();
        Log.i("Clock", "saveToPreferences: Preferences saved!");
    }

}
