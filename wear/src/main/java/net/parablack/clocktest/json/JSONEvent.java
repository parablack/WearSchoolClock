package net.parablack.clocktest.json;

import android.os.Vibrator;
import android.support.annotation.NonNull;

import net.parablack.clocktest.watchface.TimeUtils;
import net.parablack.clocktest.watchface.WearEvent;
import net.parablack.clocktest.json.subjects.SimpleSubject;
import net.parablack.clocktest.json.subjects.WearEventSubject;

import org.json.JSONException;
import org.json.JSONObject;


public class JSONEvent implements WearEvent{

    private int dayId;

    private WearEventSubject subject;
    private ScheduleTime time;
    private ScheduleMeta meta;

    private boolean shown;

    boolean alreadyVibrated = false;

    private String displayName = "";

    private EventAdditionalInformation addInfos;

    public JSONEvent(JSONObject object, JSONReader reader) throws JSONException {
    //    dayId = object.getInt("id");

        String subjectString = object.getString("subject_preset");
        if(subjectString.equals("NONE")){
            subject = new SimpleSubject("");
        }else{
            subject = reader.getSubject(subjectString);
        }

        String timePreset = object.getString("time_preset");
        if(timePreset != null && !timePreset.equals("")) time = reader.getTime(timePreset);
        else time = new ScheduleTime(object.getJSONObject("time"));

        meta = reader.getMeta(object.getString("meta"));

        shown = object.getBoolean("shown");

        displayName = object.optString("display_name");

        addInfos = new EventAdditionalInformation(object.optJSONObject("additional"));

    }

    @Override
    public long getTimeTilEnd() {
        long dayMillis = TimeUtils.dayMillis();

        return time.getEnd() - dayMillis;
    }

    public long getTimeFromBeginning(){
        long dayMillis = TimeUtils.dayMillis();
        return dayMillis - time.getBegin();
    }

    @Override
    public String getName() {
        String rDisp = "";

        rDisp += displayName == null ? "" : displayName;
        rDisp += subject.getDisplayName();
        rDisp += addInfos.room == null ? "" : " (" + addInfos.room +")";
        return rDisp;
    }

    @Override
    public int compareTo(@NonNull WearEvent another) {
        if(!(another instanceof JSONEvent)) return 1;
        if(time.getBegin() < ((JSONEvent) another).time.getBegin()) return -1;
        else return 1;

    }

    public ScheduleTime getTime() {
        return time;
    }

    public ScheduleMeta getMeta() {
        return meta;
    }

    public boolean hasAlreadyVibrated() {
        return alreadyVibrated;
    }

    public boolean checkVibrate(Vibrator vibrator){
        if(!alreadyVibrated) {

            int minTilEnd = (int) ((getTimeTilEnd() / 1000) / 60);
            if (getMeta().getVibration().getTimeBefore() == minTilEnd) {
                long[] vibrationPattern = {0, getMeta().getVibration().getDuration(), 50, getMeta().getVibration().getDuration(), 50,
                        getMeta().getVibration().getDuration(), 50, getMeta().getVibration().getDuration()};
                //-1 - don't repeat
                final int indexInPatternToRepeat = -1;
                vibrator.vibrate(vibrationPattern, indexInPatternToRepeat);
                alreadyVibrated = true;
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "JSONEvent[begin=" + time.getBegin() + ",end="+time.getEnd()+",timeTilEnd="+getTimeTilEnd()+",name="+displayName+subject.getDisplayName()+"]";
    }


    public class EventAdditionalInformation {
        private String room;
        private String comment;

        public EventAdditionalInformation(JSONObject object) {
            if(object == null) return;
            this.room = object.optString("room");
            this.comment = object.optString("comment");
        }
    }
}

