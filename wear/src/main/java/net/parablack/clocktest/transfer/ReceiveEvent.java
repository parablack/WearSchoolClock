package net.parablack.clocktest.transfer;

import com.google.android.gms.wearable.DataEvent;

public abstract class ReceiveEvent {

    private static final String ROOT_PATH = "/net/parablack/color/";


    public abstract String getPathEndpoint();

    public abstract void onReceive(DataEvent event);

}
