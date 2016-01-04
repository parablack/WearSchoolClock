package net.parablack.clocktest;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

public class ClockCommunicator implements
        DataApi.DataListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private static final String COLOR_PRESET = "net.parablack.clock.color";

    private GoogleApiClient mGoogleApiClient;

    public ClockCommunicator(Context con) {
        mGoogleApiClient = new GoogleApiClient.Builder(con)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i("[Clock]", "Connection built.");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("[Clock]", "Connection supsendend {"+i+"}.");
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {
        Log.i("[Clock]", "Data changed");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e("[Clock]", "Connection failed.");
    }

    public void sendNewColorPreset(String json){
        System.out.println("Sending " + json);
        PutDataMapRequest request = PutDataMapRequest.create("/color/json");

        request.getDataMap().putString("color", json);

        PutDataRequest rawRequest = request.asPutDataRequest();

        PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi.putDataItem(mGoogleApiClient, rawRequest);

        pendingResult.setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
            @Override
            public void onResult(DataApi.DataItemResult dataItemResult) {
                System.out.println("Result: " + dataItemResult);
            }
        });

        Wearable.NodeApi.getLocalNode(mGoogleApiClient).setResultCallback(new ResultCallback<NodeApi.GetLocalNodeResult>() {
            @Override
            public void onResult(NodeApi.GetLocalNodeResult nodes) {
                System.out.println("GetLocalNodeResult : " + nodes);
                Node node = nodes.getNode();
                Log.v("[Clock", "Activity Node is : " + node.getId() + " - " + node.getDisplayName());
                Wearable.MessageApi.sendMessage(mGoogleApiClient, node.getId(), "/clock/colors", "123abc".getBytes()).setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                    @Override
                    public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                        System.out.println("SendMessageResult : " + sendMessageResult);

                    }
                });

            }
        });

    }


}
