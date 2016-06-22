package net.parablack.clocktest.schedule;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import net.parablack.clocktest.R;
import net.parablack.clocktest.schedule.kvfgparser.KvFGParser;
import net.parablack.schedulelib.Schedule;
import net.parablack.schedulelib.ScheduleDay;

import org.json.JSONException;
import org.json.JSONObject;

public class ScheduleFragment extends Fragment implements View.OnClickListener {

    private View view;
    private Schedule schedule;
    private ListView scheduleList;
    private ArrayAdapter adapter;
    private ScheduleDrawer drawer;
    private SharedPreferences pref;

    public ScheduleFragment() {

    }

    public void updateSchedule(Schedule s){
        this.schedule = s;
        adapter = drawer.getAdapter(s);
        if(getView() != null)
        getView().invalidate();
        save();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        drawer = new ScheduleDrawer(this);
        pref = getActivity().getSharedPreferences("SchoolClock_Schedule", Context.MODE_PRIVATE);
        String json = pref.getString("schedule_json", "Error");
        Log.d("Clock", "[Schedule] Preferences loaded: [" + json + "]");

        try {
            schedule = new Schedule(new JSONObject(json));
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("Clock", "No saved schedule found!");
            Schedule schedule = new Schedule();

            schedule.addDay(new ScheduleDay(1)); //SUNDAY
            schedule.addDay(new ScheduleDay(2));
            schedule.addDay(new ScheduleDay(3));
            schedule.addDay(new ScheduleDay(4));
            schedule.addDay(new ScheduleDay(5));
            schedule.addDay(new ScheduleDay(6));
            schedule.addDay(new ScheduleDay(7));
            this.schedule = schedule;
        }
        updateSchedule(schedule);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_schedule, container, false);
        Button b = (Button) view.findViewById(R.id.upload_schedule);
        b.setOnClickListener(this);

        scheduleList = (ListView) view.findViewById(R.id.scheduleList);
        if(adapter == null) updateSchedule(schedule);
        scheduleList.setAdapter(adapter);


        return view;
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.upload_schedule) {
            // Upload
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu_schedule, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.parse_kvfg) {
            LayoutInflater li = LayoutInflater.from(getActivity());
            View promptsView = li.inflate(R.layout.kvfg_text_input, null);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    getActivity());

            alertDialogBuilder.setView(promptsView);

            final EditText userInput = (EditText) promptsView
                    .findViewById(R.id.editTextDialogUserInput);

            alertDialogBuilder
                    .setCancelable(true)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    String json = userInput.getText().toString();

                                    try {
                                        Schedule schedule = KvFGParser.getFromKvFGJSON(json);
                                        updateSchedule(schedule);

                                    } catch (JSONException e) {
                                        new AlertDialog.Builder(getActivity())
                                                .setTitle("Ungültiges Format")
                                                .setMessage("Kein gültiges JSON gefunden!")
                                                .setIcon(android.R.drawable.ic_dialog_alert)
                                                .show();
                                    }

                                }
                            })
                    .setNegativeButton("Abbruch",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    dialog.cancel();
                                }
                            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();


            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    public void save(){
        pref.edit().putString("schedule_json", schedule.toJSON().toString()).apply();
    }

}
