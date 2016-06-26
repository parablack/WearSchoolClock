package net.parablack.clocktest.watchface;

import android.content.Intent;
import android.util.Log;

import net.parablack.clocktest.app.MainActivityWear;
import net.parablack.clocktest.watchface.drawer.mode.FullLineDrawer;
import net.parablack.clocktest.watchface.drawer.mode.SingeLineDrawer;
import net.parablack.clocktest.watchface.drawer.mode.TextDrawer;

public class WatchFaceTapper {

    private SchoolWatchFaceService.Engine service;

    int alreadyTapped = 0;
    int alreadyDownRightTapped = 0;

    public WatchFaceTapper(SchoolWatchFaceService.Engine service) {
        this.service = service;
    }

    public void reset() {
        alreadyTapped = 0;
    }

    public void onClick(int tapType, int x, int y, long eventTime) {


        if (x < 100 && y < 100) {
            alreadyTapped++;
        } else if (alreadyTapped >= 10) {
            if (x > 200 && y > 200) {
                Log.d("Clock","Forced starting app --> Starting intent");
                Intent i = new Intent(SchoolWatchFaceService.getInstance(), MainActivityWear.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                SchoolWatchFaceService.getInstance().startActivity(i);
                reset();

            }
        } else if (alreadyTapped >= 4) {
            if (x > 200 && y > 200) {
                service.getDrawer().setCurrentDrawer(new SingeLineDrawer(service.getDrawer()));
                reset();
            }
            if (x > 200 && y < 100) {
                service.getDrawer().setCurrentDrawer(new FullLineDrawer(service.getDrawer()));
                reset();
            }
            if (x < 100 && y > 200) {
                service.getDrawer().setCurrentDrawer(new TextDrawer(service.getDrawer()));
                reset();
            }
            service.invalidate();
        }
    }
}
