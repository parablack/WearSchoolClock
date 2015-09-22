package net.parablack.clocktest.json;

import android.content.res.AssetManager;

import net.parablack.clocktest.json.subjects.ScheduleSubject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static net.parablack.clocktest.json.JSONReaderUtil.*;

/**
 * Created by Simon on 16.09.2015.
 */
public class JSONReader {

    private HashMap<String, ScheduleMeta> metas = new HashMap<>();
    private HashMap<String, ScheduleTime> times = new HashMap<>();
    private HashMap<String, ScheduleSubject> subjects = new HashMap<>();
    private JSONSchedule schedule;


    public JSONReader(AssetManager assets) throws InvalidDataException {
        initByArray(assets, "meta.json", "$metas_TkIhUZ28", "metas", new ScheduleInitCallback() {
            @Override
            public void callback(JSONObject object) throws JSONException {
                ScheduleMeta sm = new ScheduleMeta(object);
                metas.put(sm.getId(), sm);
            }
        });

        initByArray(assets, "timePreset.json", "$timePreset_TkIhUZ28", "times", new ScheduleInitCallback() {
            @Override
            public void callback(JSONObject object) throws JSONException {
                ScheduleTime sm = new ScheduleTime(object);
                times.put(sm.getId(), sm);
            }
        });

        initByArray(assets, "subjectPreset.json", "$subjectPreset_TkIhUZ28", "subjects", new ScheduleInitCallback() {
            @Override
            public void callback(JSONObject object) throws JSONException {
                ScheduleSubject sm = new ScheduleSubject(object);
                subjects.put(sm.getId(), sm);
            }
        });

        JSONObject mainSchedule = byAsset(assets, "mainSchedule.json");
        verifyMeta(mainSchedule, "$mainSchedule_TkIhUZ28");

        schedule = new JSONSchedule(mainSchedule, this);

    }


    public ScheduleMeta getMeta(String name) {
        return metas.get(name);
    }

    public ScheduleTime getTime(String name) {
        return times.get(name);
    }

    public ScheduleSubject getSubject(String name) {
        return subjects.get(name);
    }

    public JSONSchedule getSchedule() {
        return schedule;
    }
}
