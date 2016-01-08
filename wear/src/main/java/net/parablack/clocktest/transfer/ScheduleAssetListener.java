package net.parablack.clocktest.transfer;

import android.util.Log;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.WearableListenerService;

public class ScheduleAssetListener extends WearableListenerService {

    private static final String COLOR_PRESET = "net.parablack.clock.color";

    public ScheduleAssetListener() {

    }


    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        System.out.println("OMG! Daten sind angekommen! " + dataEvents);
        for (DataEvent event : dataEvents) {
            if (event.getDataItem().getUri().getPath().equals("/color/json")) {
                DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
                String colorJson = dataMapItem.getDataMap().getString("color");
                Log.i("[Clock]", "Data received: " + colorJson);

                // Do something with the bitmap
            }
        }
    }

    @Override
      public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);
        System.out.println("Message received: " + messageEvent);
    }

    @Override
    public void onPeerConnected(Node peer) {
        super.onPeerConnected(peer);

        System.out.println("Peer connected [Wear] : " + peer);
    }
}
