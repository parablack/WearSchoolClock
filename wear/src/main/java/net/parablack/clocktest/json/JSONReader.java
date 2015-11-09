package net.parablack.clocktest.json;

import android.content.res.AssetManager;

import com.google.android.gms.wearable.Asset;

import net.parablack.clocktest.json.subjects.ScheduleSubject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.HashMap;

import static net.parablack.clocktest.json.JSONReaderUtil.*;


public class JSONReader {

    private HashMap<String, ScheduleMeta> metas = new HashMap<>();
    private HashMap<String, ScheduleTime> times = new HashMap<>();
    private HashMap<String, ScheduleSubject> subjects = new HashMap<>();
    private JSONSchedule schedule;
 //   private JSONColors colors;



    public JSONReader()  {

    }

    public void parseData(AssetManager assets) throws InvalidDataException{
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

    /**
     * Get the meta with the specified name
     * @param name The name of the meta, specified in the metas.json file
     * @return The meta according to the name
     */
    public ScheduleMeta getMeta(String name) {
        return metas.get(name);
    }

    /**
     * Get the time preset with the specified name
     * @param name The name of the preset, specified in the timePreset.json file
     * @return The preset according to the name
     */
    public ScheduleTime getTime(String name) {
        return times.get(name);
    }

    public ScheduleSubject getSubject(String name) {
        return subjects.get(name);
    }

    /**
     * Get the current loaded schedule from the files
     * @return THe loaded schedule
     */
    public JSONSchedule getSchedule() {
        return schedule;
    }

    /**
     * Get the current loaded colors from the assets
     * @return The colors object
     */
    public JSONColors getColors(InputStream a) {
        JSONObject colors = byAsset(a);
        verifyMeta(colors, "$colorPreset_TkIhUZ28");
        return new JSONColors(colors);
    }

}
