package net.parablack.clocktest.schedule.kvfgparser;



import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import net.parablack.clocktest.R;
import net.parablack.clocktest.schedule.ScheduleFragment;
import net.parablack.schedulelib.Schedule;
import net.parablack.schedulelib.ScheduleDay;
import net.parablack.schedulelib.ScheduleEvent;

import org.json.JSONException;
import org.json.JSONObject;

public class KvFGParser {

    private static final String[] HOURS  =
            {
                        "8:00",
                        "8:45",
                        "9:50",
                        "10:40",
                        "11:45",
                        "12:35",
                        "13:20",
                        "14:10",
                        "15:00",
                        "15:55",
                        "16:40"
            };

    private static final int HOUR_DURATION = 2700000;


    private static int[] _hours = new int[11];


    private static void initArray(){
        for(int i = 0; i < 11; i++){
            String hS = HOURS[i];

            int hours = Integer.parseInt(hS.split(":")[0]);
            int min = Integer.parseInt(hS.split(":")[1]);

            int millis = ((hours * 60) + min) * 60 * 1000;
            _hours[i] = millis;
        }


    }

    public static void displayDialog(final ScheduleFragment con){
        LayoutInflater li = LayoutInflater.from(con.getActivity());
        View promptsView = li.inflate(R.layout.kvfg_text_input, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                con.getActivity());

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
                                    con.updateSchedule(schedule);

                                } catch (JSONException e) {
                                    new AlertDialog.Builder(con.getActivity())
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
    }

    public static Schedule getFromKvFGJSON(String json) throws JSONException {

        initArray();

        JSONObject obj = new JSONObject(json);
        Schedule schedule = new Schedule();

        schedule.addDay(new ScheduleDay(1, schedule)); //SUNDAY
        schedule.addDay(new ScheduleDay(7, schedule)); //SATURDAY


        for(int i = 0; i < 5; i++){

            JSONObject day = obj.getJSONObject(i + "");
            ScheduleDay sDay = new ScheduleDay(i + 2, schedule);

            for(int j = 0; j < 11; j++){

                JSONObject fach = day.getJSONObject(j + "");
                if(fach.getString("s").equals("")) continue;

                String displayName = fach.getString("s") + " bei " + fach.getString("t") + " (" + fach.getString("r") + ")";

                int begin = _hours[j];
                int end = begin + HOUR_DURATION;

                ScheduleEvent event = new ScheduleEvent(displayName, begin, end, i + 2, 2);
                sDay.addEvent(event);
            }
            schedule.addDay(sDay);
        }
        return schedule;
    }




}


