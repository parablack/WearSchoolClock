package net.parablack.clocktest.watchface.drawer.mode;

import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;

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

        c.drawColor(getColor(args.getPercentageDone()));

    }

    @Override
    public void reloadColors() {

    }


    private int getColor(double percent) {
        double degree = percent * 240;
        int mode = (int) (degree / 60);
        int bowCalc = (int) ((degree % 60) * (255 / 60));
        int r = 0, g = 0, b = 0;

        Log.i("Clock", "Perc=" + percent + ", mode=" + mode + "; bowcalc=" + bowCalc);

        switch (mode) {
            case 0:
                r = 255;
                b = bowCalc;
                break;
            case 1:
                b = 255;
                r = 255 - bowCalc;
                break;
            case 2:
                b = 255;
                g = bowCalc;
                break;
            case 3:
                g = 255;
                b = 255 - bowCalc;
                break;

        }
        Log.i("Clock", "Perc=" + percent + ", mode=" + mode + "; bowcalc=" + bowCalc + ", rgb=" + r + " " + g +" " + b);
        return Color.rgb(r,g,b);
    }

}
