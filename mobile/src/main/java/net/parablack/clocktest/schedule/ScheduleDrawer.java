package net.parablack.clocktest.schedule;

import android.widget.ArrayAdapter;

import net.parablack.schedulelib.Schedule;
import net.parablack.schedulelib.ScheduleDay;
import net.parablack.schedulelib.ScheduleEvent;

public class ScheduleDrawer {

    private ScheduleFragment fragment;
    private ArrayAdapter<String> adapter;


    public ScheduleDrawer(ScheduleFragment fragment) {
        this.fragment = fragment;
        adapter = new ArrayAdapter<>(fragment.getActivity(), android.R.layout.simple_list_item_1);
    }

    public ArrayAdapter getAdapter(Schedule schedule){

        adapter.clear();
        ScheduleDay[] days = schedule.getDays();

        for(ScheduleDay d : days){
            if(d == null) continue;
            for(ScheduleEvent e : d.getEvents()){
                adapter.add(d.getId() + " " + e.niceStartTime() + "-" + e.niceEndTime() +":" + e.getName());
            }
        }
        return adapter;
    }

}
