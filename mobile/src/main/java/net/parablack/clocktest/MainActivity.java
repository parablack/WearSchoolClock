package net.parablack.clocktest;

import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import net.parablack.clocktest.color.ColorChanger;
import net.parablack.clocktest.color.ColorCreator;
import net.parablack.clocktest.color.ColorListAdapter;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private ClockCommunicator communicator;
    private ColorCreator creator;
    private ColorListAdapter<String> adapter;

    public static MainActivity instance;

    public static HashMap<Integer, String> posToName = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        setContentView(R.layout.activity_main);

        communicator = new ClockCommunicator(this);
        creator = new ColorCreator(this);

        View v1 = findViewById(R.id.colorList);
        ListView v = (ListView) v1;

        adapter = new ColorListAdapter<>(this, android.R.layout.simple_list_item_1);
        for(String s : creator.getColorNames()){
            adapter.add(ColorCreator.getDisplayName(s));
            posToName.put(adapter.getPosition(ColorCreator.getDisplayName(s)), s);
        }
        assert v != null;
        v.setAdapter(adapter);
        v.setLongClickable(true);
        v.setOnItemLongClickListener(new ColorChanger());
    }


    public ColorCreator getColorCreator() {
        return creator;
    }

    public ColorListAdapter<String> getColorListAdapter() {
        return adapter;
    }

    public void testButtonClicked(View v){
        System.out.println("Test Buttoni Rigatoni #2");


        int notificationId = 1;
        Intent viewIntent = new Intent(this, MainActivity.class);

        PendingIntent viewPendingIntent =
                PendingIntent.getActivity(this, 0, viewIntent, 0);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_notification_test)
                        .setContentTitle("Hallo")
                        .setContentText("Testo")
                        .setContentIntent(viewPendingIntent)
                        .addAction(R.drawable.ic_setting_dark, "Aha", viewPendingIntent);


        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(this);

        notificationManager.notify(notificationId, notificationBuilder.build());


        // Send to clock
        communicator.sendNewColorPreset(creator.toJSON() + System.currentTimeMillis());

        // Send message
        communicator.requestTranscription("/message/reload", "RELOAD_COLORS".getBytes());

    }


    // ?! What the heck!
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



}
