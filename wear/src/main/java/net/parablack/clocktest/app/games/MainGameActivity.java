package net.parablack.clocktest.app.games;

import android.support.wearable.activity.WearableActivity;


public abstract class MainGameActivity extends WearableActivity{


    public abstract String getName();

    @Override
    public String toString() {
        return getName();
    }
}
