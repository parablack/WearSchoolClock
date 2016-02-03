package net.parablack.clocktest.watchface.drawer.mode;

import android.graphics.Canvas;
import android.graphics.Color;

import net.parablack.clocktest.watchface.drawer.WatchFaceDrawer;
import net.parablack.clocktest.watchface.drawer.mode.wrapper.SuperTimeWrapper;
import net.parablack.clocktest.watchface.drawer.mode.wrapper.TimeException;


public class PixelDrawer extends ModeFaceDrawer<SuperTimeWrapper> {

    private static final int SQUARE_SIZE = 6;
    private static final int PIXEL_PER_SQUARE = SQUARE_SIZE * SQUARE_SIZE;


    public PixelDrawer(WatchFaceDrawer drawer) {
        super(drawer);
    }

    @Override
    protected void onDraw(Canvas c, SuperTimeWrapper args) throws ScheduleDrawException, TimeException {
        if(!displaySeconds()) return;
        c.drawColor(Color.argb(100, 255 - (int) (args.getPercentageDone() * 255), (int) (args.getPercentageDone() * 255)
                , 0));

    }

    @Override
    public void reloadColors() {

    }


}
