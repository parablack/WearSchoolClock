package net.parablack.clocktest.schedule;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import net.parablack.clocktest.MainActivity;
import net.parablack.clocktest.R;
import net.parablack.clocktest.schedule.kvfgparser.KvFGParser;
import net.parablack.schedulelib.Schedule;
import net.parablack.schedulelib.ScheduleEvent;

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

    public void updateSchedule(Schedule s) {
        this.schedule = s;
        adapter = drawer.getAdapter(s);
        if (getView() != null)
            getView().invalidate();
        save();
    }

    public void updateSchedule() {
        updateSchedule(this.schedule);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        drawer = new ScheduleDrawer(this);
        schedule = Schedule.fromSharedPrefs(getActivity());

        updateSchedule(schedule);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_schedule, container, false);

        Button b = (Button) view.findViewById(R.id.upload_schedule);
        b.setOnClickListener(this);
        Button b1 = (Button) view.findViewById(R.id.schedule_create_new);
        b1.setOnClickListener(this);

        scheduleList = (ListView) view.findViewById(R.id.scheduleList);
        if (adapter == null) updateSchedule(schedule);
        scheduleList.setAdapter(adapter);
        scheduleList.setLongClickable(true);
        scheduleList.setOnItemLongClickListener(drawer.new ScheduleItemClickListener());

        return view;
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.upload_schedule) {
            String json = schedule.toJSON().toString();
            MainActivity.instance.getCommunicator().sendNewSchedule(json);
        }
        if (v.getId() == R.id.schedule_create_new) {
            ScheduleEvent event = new ScheduleEvent("", -1, -1, -1, -1);

            new EventChangeDialog(event, this).display();

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
            KvFGParser.displayDialog(this);
            return true;
        }
        if (id == R.id.schedule_clear) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Stundenplan löschen?").
                    setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            updateSchedule(Schedule.emptySchedule());
                        }
                    })
                    .setNegativeButton("Nein", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    }).show();
        }
        if (id == R.id.schedule_export) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, schedule.toJSON().toString());
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        }
        if (id == R.id.schedule_import) {
            final EditText promptsView = new EditText(getActivity());
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    getActivity());
            alertDialogBuilder.setView(promptsView);
            alertDialogBuilder
                    .setCancelable(true)
                    .setPositiveButton("Importieren",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    String json = promptsView.getText().toString();
                                    try {
                                        Schedule schedule = new Schedule(new JSONObject(json));
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
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }

        return super.onOptionsItemSelected(item);
    }

    public void save() {
        schedule.toPrefs(getActivity());
    }

    public Schedule getSchedule() {
        return schedule;
    }
}
