package net.parablack.clocktest.color;

import android.content.Context;
import android.util.Log;

import net.parablack.schedulelib.utils.JSONReaderUtil;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

public class ColorDisplayNames {

    private static HashMap<String, String> displayNames = new HashMap<>();

    public static void loadDisplayNames(Context con) {
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

    public static String getDisplayName(String id) {
        return displayNames.get(id);
    }


}
