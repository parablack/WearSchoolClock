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
import net.parablack.clocktest.color.ColorManager;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private ClockCommunicator communicator;
    private ColorManager colorManager;

    public static MainActivity instance;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        setContentView(R.layout.activity_main);

        communicator = new ClockCommunicator(this);
        colorManager = new ColorManager(this);


    }

    public ColorManager getColorManager() {
        return colorManager;
    }

    public void testButtonClicked(View v){

        // Send to clock
        communicator.sendNewColorPreset(colorManager.getCreator().toJSON() + System.currentTimeMillis());

        // Send message
        communicator.requestTranscription("/message/reload", "RELOAD_COLORS".getBytes());

    }

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
        if(id == R.id.color_reset){

            colorManager.reset();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }



}
