package net.parablack.clocktest.schedule;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import net.parablack.clocktest.MainActivity;
import net.parablack.clocktest.R;
import net.parablack.clocktest.schedule.kvfgparser.KvFGParser;
import net.parablack.schedulelib.Schedule;
import net.parablack.schedulelib.ScheduleDay;
import net.parablack.schedulelib.ScheduleEvent;

import org.json.JSONException;

import java.util.HashMap;

public class ScheduleDrawer {

    private final ScheduleFragment fragment;
    private ArrayAdapter<String> adapter;
    private HashMap<Integer, ScheduleEvent> events = new HashMap<>();

    public ScheduleDrawer(ScheduleFragment fragment) {
        this.fragment = fragment;
        adapter = new ArrayAdapter<>(fragment.getActivity(), android.R.layout.simple_list_item_1);
    }

    public ArrayAdapter getAdapter(Schedule schedule){
        events.clear();
        adapter.clear();
        ScheduleDay[] days = schedule.getDays();

        for(ScheduleDay d : days){
            if(d == null) continue;
            for(ScheduleEvent e : d.getEvents()){
                String name = e.niceDay() + " " + e.niceStartTime() + "-" + e.niceEndTime() +": \n" + e.getName();
                adapter.add(name);
                int pos = adapter.getPosition(name);
                events.put(pos, e);
            }
        }
        return adapter;
    }

    public ScheduleEvent getEvent(int pos) {
        return events.get(pos);
    }

    public class ScheduleItemClickListener implements AdapterView.OnItemLongClickListener {

        @Override
        public boolean onItemLongClick(AdapterView<?> parent1, View view, int position, long id) {


            ScheduleEvent event = getEvent(position);
            EventChangeDialog dialog = new EventChangeDialog(event, fragment);
            dialog.display();

            return true;
        }
    }




}
