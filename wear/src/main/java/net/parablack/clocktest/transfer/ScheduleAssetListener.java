package net.parablack.clocktest.transfer;

import android.net.Uri;
import android.os.Bundle;
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

import net.parablack.clocktest.json.JSONColors;
import net.parablack.clocktest.watchface.SchoolWatchFaceService;
import net.parablack.clocktest.watchface.drawer.WatchFaceDrawer;

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
            System.out.println("event.getDataItem.getUri.getPath = " + event.getDataItem().getUri().getPath());
            if(event.getDataItem().getUri().getPath().equals("/clock/color")){
                 // We can expect to get an JSON String  here
                JSONColors colors = new JSONColors();
                try {
                    DataMapItem item = DataMapItem.fromDataItem(event.getDataItem());
                    colors.reload(new JSONObject(item.getDataMap().getString("color")));
                    colors.save(SchoolWatchFaceService.getInstance().getSharedPreferences("SchoolClock_pref", MODE_PRIVATE));
                    SchoolWatchFaceService.getInstance().getWatchEngine().getDrawer().updateColors(colors);
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
        System.out.println("Message received: " + messageEvent);

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

        System.out.println("Peer connected [Wear] : " + peer);
    }
}
