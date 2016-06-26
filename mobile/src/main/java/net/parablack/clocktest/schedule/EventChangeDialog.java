package net.parablack.clocktest.schedule;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import net.parablack.clocktest.R;
import net.parablack.schedulelib.ScheduleEvent;

public class EventChangeDialog {

    ScheduleEvent event;
    ScheduleFragment fragment;

    public EventChangeDialog(ScheduleEvent event, ScheduleFragment fragment) {
        this.event = event;
        this.fragment = fragment;
    }

    public void display() {
        LayoutInflater li = LayoutInflater.from(fragment.getActivity());
        View promptsView = li.inflate(R.layout.dialog_edit_event, null, false);//new EditText(fragment.getActivity());

        final Spinner sp = (Spinner) promptsView.findViewById(R.id.spinner);
        sp.setAdapter(ArrayAdapter.createFromResource(fragment.getActivity(),
                R.array.days_array, android.R.layout.simple_spinner_dropdown_item));
        sp.setSelection(event.getDay() - 1);


        final EditText ed = (EditText) promptsView.findViewById(R.id.dialog_displayname);
        ed.setText(event.getName());

        final TextView start = (TextView) promptsView.findViewById(R.id.edit_start);
        final TextView end = (TextView) promptsView.findViewById(R.id.edit_end);

        final CheckBox vibrate = (CheckBox) promptsView.findViewById(R.id.checkVibrate);
        vibrate.setChecked(event.getVibrateTime() != -1);

        start.setText("Start: " + event.niceStartTime());
        end.setText("Ende: " + event.niceEndTime());


        ScheduleDatePicker picker = new ScheduleDatePicker(event, new TimePickedCallback() {
            @Override
            public void timeSelected(int id, int millis) {
                switch (id) {
                    case R.id.edit_start:
                        event.setBegin(millis);
                        break;
                    case R.id.edit_end:
                        event.setEnd(millis);
                        break;
                }
                start.setText("Start: " + event.niceStartTime());
                end.setText("Ende: " + event.niceEndTime());
            }
        });

        start.setOnClickListener(picker);
        end.setOnClickListener(picker);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                fragment.getActivity());
        alertDialogBuilder.setView(promptsView);

        alertDialogBuilder
                .setCancelable(true)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (sp.getSelectedItemId() != event.getDay() - 1) {    // DAY UPDATED
                                    try {
                                        fragment.getSchedule().getDays()[event.getDay()].removeEvent(event);
                                    } catch (Exception e) {
                                        Log.d("Clock", "onClick: Event not found in schedule, new one?");
                                    }
                                    event.setDay(sp.getSelectedItemPosition() + 1);
                                    fragment.getSchedule().getDays()[event.getDay()].addEvent(event);

                                }
                                if (vibrate.isChecked()) {
                                    event.setVibrateTime(2);
                                } else event.setVibrateTime(-1);
                                event.setDisplayName(ed.getText().toString());
                                fragment.updateSchedule();
                            }
                        })
                .setNegativeButton("Löschen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        fragment.getSchedule().getDays()[event.getDay()].removeEvent(event);
                        fragment.updateSchedule();
                    }
                })
                .setNeutralButton("Abbruch",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    public class ScheduleDatePicker implements View.OnClickListener {

        private TimePickedCallback cb;
        private ScheduleEvent event;

        public ScheduleDatePicker(ScheduleEvent event, TimePickedCallback cb) {
            this.cb = cb;
            this.event = event;
        }


        @Override
        public void onClick(final View v) {

            LayoutInflater li = LayoutInflater.from(fragment.getActivity());
            final TimePicker promptsView = new TimePicker(fragment.getActivity());//li.inflate(R.layout.dialog_edit_event, null, false);//new EditText(fragment.getActivity());
            promptsView.setIs24HourView(true);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    fragment.getActivity());
            alertDialogBuilder.setView(promptsView);

            switch (v.getId()) {
                case R.id.edit_start:
                    promptsView.setCurrentHour(event.getBeginHour());
                    promptsView.setCurrentMinute(event.getBeginMinute());
                    break;
                case R.id.edit_end:
                    promptsView.setCurrentHour(event.getEndHour());
                    promptsView.setCurrentMinute(event.getEndMinute());
                    break;
            }

            alertDialogBuilder
                    .setCancelable(true)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    int millis = ((promptsView.getCurrentHour() * 60) + promptsView.getCurrentMinute()) * 60 * 1000;
                                    cb.timeSelected(v.getId(), millis);


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
    }

    protected interface TimePickedCallback {
        void timeSelected(int id, int millis);
    }

}
