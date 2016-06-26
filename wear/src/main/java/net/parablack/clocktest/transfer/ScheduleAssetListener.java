package net.parablack.clocktest.transfer;

import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import net.parablack.clocktest.watchface.SchoolWatchFaceService;
import net.parablack.schedulelib.Schedule;
import net.parablack.schedulelib.color.ScheduleColors;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class ScheduleAssetListener extends WearableListenerService {

    private static final String COLOR_PRESET = "net.parablack.clock.color";


    public ScheduleAssetListener() {
    }


    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.d("Clock", "onDataChanged: " + dataEvents);
        final List<DataEvent> events = FreezableUtils
                .freezeIterable(dataEvents);

        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();

        ConnectionResult connectionResult =
                googleApiClient.blockingConnect(30, TimeUnit.SECONDS);

        if (!connectionResult.isSuccess()) {
            Log.e("Clock", "Failed to connect to GoogleApiClient.");
            return;
        }

        // Loop through the events and send a message
        // to the node that created the data item.
        for (DataEvent event : events) {
            Log.d("Clock", "event.getDataItem.getUri.getPath = " + event.getDataItem().getUri().getPath());
            if(event.getDataItem().getUri().getPath().equals("/clock/color")){
                 // We can expect to get an JSON String  here
                try {
                    DataMapItem item = DataMapItem.fromDataItem(event.getDataItem());
                    ScheduleColors colors = new ScheduleColors(new JSONObject(item.getDataMap().getString("color")));
                    Log.d("Clock", "Color string: " + item.getDataMap().getString("color"));
                    colors.saveToPreferences(SchoolWatchFaceService.getInstance());
                    SchoolWatchFaceService.getInstance().getWatchEngine().getDrawer().updateColors(colors);
                    Log.d("Clock", "Changed colors!");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if(event.getDataItem().getUri().getPath().equals("/clock/schedule")){
                // We can expect to get an JSON String  here
                try {
                    DataMapItem item = DataMapItem.fromDataItem(event.getDataItem());
                    JSONObject object = new JSONObject(item.getDataMap().getString("schedule"));
                    Schedule schedule = new Schedule(object);
                    Log.d("Clock", "Schedule string: " + object.toString());
                    schedule.toPrefs(SchoolWatchFaceService.getInstance());
                    schedule.reload();
                    SchoolWatchFaceService.getInstance().getWatchEngine().setMainSchedule(schedule);
                    Log.d("Clock", "Changed colors!");
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }


        }
    }

    @Override
      public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);
        Log.i("Clock","Message received: " + messageEvent);

        if(messageEvent.getPath().equals("/message/reload")){
            Log.i("Clock", "onMessageReceived GOT /message/reload ");
//            JSONColors colors = new JSONColors();
//            try {
//                colors.reload(new JSONObject(Wearable.DataApi.getDataItem(mGoogleApiClient), "/clock/color"));
//                SchoolWatchFaceService.getInstance().getWatchEngine().getDrawer().updateColors(colors);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
        }


    }


    @Override
    public void onPeerConnected(Node peer) {
        super.onPeerConnected(peer);

        Log.d("Clock","Peer connected [Wear] : " + peer);
    }
}
