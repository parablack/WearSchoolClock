package net.parablack.clocktest.schedule;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import net.parablack.clocktest.R;
import net.parablack.clocktest.schedule.kvfgparser.KvFGParser;
import net.parablack.schedulelib.Schedule;

import org.json.JSONException;

public class ScheduleFragment extends Fragment implements View.OnClickListener {

    private View view;

    public ScheduleFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_schedule, container, false);
        Button b = (Button) view.findViewById(R.id.upload_schedule);
        b.setOnClickListener(this);
        Button b1 = (Button) view.findViewById(R.id.parse_button);
        b1.setOnClickListener(this);


        return view;
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.upload_schedule) {

        }
        if (v.getId() == R.id.parse_button) {

            EditText ed = (EditText) view.findViewById(R.id.kvfgText);
            String json = ed.getText().toString();

            try {
                Schedule schedule = KvFGParser.getFromKvFGJSON(json);

                ed.getText().clear();
                ed.getText().insert(0, schedule.toJSON().toString());


            } catch (JSONException e) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Ungültiges Format")
                        .setMessage("Kein gültiges JSON gefunden!")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }


        }

    }
}
