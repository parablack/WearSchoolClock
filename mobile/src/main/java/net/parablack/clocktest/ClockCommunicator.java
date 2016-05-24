package net.parablack.clocktest;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.CapabilityApi;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.Set;

public class ClockCommunicator implements
        DataApi.DataListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String
            WATCHFACE_RELOAD_CAPABILITY = "watchface_reload";

    private GoogleApiClient mGoogleApiClient;

    public ClockCommunicator(Context con) {
        mGoogleApiClient = new GoogleApiClient.Builder(con)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mGoogleApiClient.connect();

        setupMessageTranscription();

    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i("[Clock]", "Connection built.");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("Clock", "Connection supsendend {" + i + "}.");
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {
        Log.i("[Clock]", "Data changed");
    }

    @Override
    public void onConnectionFailed(@NonNull  ConnectionResult connectionResult) {
        Log.e("Clock", "Connection failed.");
    }

    public void sendNewColorPreset(String json) {
        Log.d("Clock", "Sending " + json);
        PutDataMapRequest request = PutDataMapRequest.create("/clock/color");

        request.getDataMap().putString("color", json);

        PutDataRequest rawRequest = request.asPutDataRequest();
   //     rawRequest.setUrgent();
        PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi.putDataItem(mGoogleApiClient, rawRequest);
        pendingResult.setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
            @Override
            public void onResult(@NonNull  DataApi.DataItemResult dataItemResult) {
               Log.d("Clock", "Result: " + dataItemResult.getStatus());
            }
        });

//        Wearable.NodeApi.getLocalNode(mGoogleApiClient).setResultCallback(new ResultCallback<NodeApi.GetLocalNodeResult>() {
//            @Override
//            public void onResult(NodeApi.GetLocalNodeResult nodes) {
//                System.out.println("GetLocalNodeResult : " + nodes);
//                Node node = nodes.getNode();
//                Log.v("Clock", "Activity Node is : " + node.getId() + " - " + node.getDisplayName());
//                Wearable.MessageApi.sendMessage(mGoogleApiClient, node.getId(), "/clock/color", "123abc".getBytes()).setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
//                    @Override
//                    public void onResult(MessageApi.SendMessageResult sendMessageResult) {
//                        System.out.println("SendMessageResult : " + sendMessageResult.getStatus());
//
//                    }
//                });
//
//            }
//        });

    }

    String reloadNodeId;

    private void setupMessageTranscription() {

        Wearable.CapabilityApi.getCapability(
                mGoogleApiClient, WATCHFACE_RELOAD_CAPABILITY,
                CapabilityApi.FILTER_REACHABLE).setResultCallback(new ResultCallback<CapabilityApi.GetCapabilityResult>() {
            @Override
            public void onResult( @NonNull  CapabilityApi.GetCapabilityResult getCapabilityResult) {
                Log.d("Clock","[1] Capability result received, passing to updateTranscriptionCapability()");
                updateTranscriptionCapability(getCapabilityResult.getCapability());
            }
        });

        CapabilityApi.CapabilityListener capabilityListener =
                new CapabilityApi.CapabilityListener() {
                    @Override
                    public void onCapabilityChanged(CapabilityInfo capabilityInfo) {
                        updateTranscriptionCapability(capabilityInfo);
                        Log.d("Clock","[2] Capability result received (CHANGED!), passing to updateTranscriptionCapability()");

                    }
                };
        Wearable.CapabilityApi.addCapabilityListener(
                mGoogleApiClient,
                capabilityListener,
                WATCHFACE_RELOAD_CAPABILITY);

    }


    private void updateTranscriptionCapability(CapabilityInfo capabilityInfo) {
        Set<Node> connectedNodes = capabilityInfo.getNodes();
        Log.v("Clock", "Node length  = " + connectedNodes.size());
        reloadNodeId = pickBestNodeId(connectedNodes);
        Log.v("Clock", "reloadNodeId = " + reloadNodeId);
    }

    public void requestTranscription(String path, byte[] data) {
        if (reloadNodeId != null) {
            Wearable.MessageApi.sendMessage(mGoogleApiClient, reloadNodeId,
                    "/message/reload", data).setResultCallback(
                    new ResultCallback<MessageApi.SendMessageResult>() {

                        @Override
                        public void onResult(@NonNull MessageApi.SendMessageResult sendMessageResult) {
                            if (!sendMessageResult.getStatus().isSuccess()) {
                                // Failed to send message
                                Log.e("Clock","Message fail: " + sendMessageResult.getStatus());
                            }else{
                                Log.d("Clock","Message succes: " + sendMessageResult.getStatus());
                            }
                        }
                    }
            );
        } else {
            Log.e("Clock","Message fail: No node found, researching!");
            setupMessageTranscription();
        }
    }

    private String pickBestNodeId(Set<Node> nodes) {
        String bestNodeId = null;
        // Find a nearby node or pick one arbitrarily
        for (Node node : nodes) {
            if (node.isNearby()) {
                return node.getId();
            }
            bestNodeId = node.getId();
        }
        return bestNodeId;
    }


}
