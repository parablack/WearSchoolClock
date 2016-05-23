package net.parablack.clocktest.color;

import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.MainThread;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import net.parablack.clocktest.MainActivity;

import es.dmoral.coloromatic.ColorOMaticDialog;
import es.dmoral.coloromatic.IndicatorMode;
import es.dmoral.coloromatic.OnColorSelectedListener;
import es.dmoral.coloromatic.colormode.ColorMode;

public class ColorChanger implements AdapterView.OnItemLongClickListener {

    @Override
    public boolean onItemLongClick(final AdapterView<?> parent, final View view, int position, long id) {
        final String colorid = MainActivity.posToName.get(position);
        Log.d("Clock", "Click on pos #" + position + "; String found: " + colorid);

        // COLOR-O-MATIC: https://android-arsenal.com/details/1/3391
        // Used here to simplify color creation
        // I DO NOT OWN ANY RIGHTS ON THE INCLUDED CODE!

        new ColorOMaticDialog.Builder()
                .initialColor(MainActivity.instance.getColorCreator().getColor(colorid))
                .colorMode(ColorMode.RGB)
                .indicatorMode(IndicatorMode.HEX)
                .onColorSelected(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(@ColorInt int i) {
                        Log.d("Clock", "Color select: " + i);
                        MainActivity.instance.getColorCreator().setColor(colorid, i);
                        MainActivity.instance.getColorListAdapter().notifyDataSetChanged();
                    }
                })
                .showColorIndicator(true) // Default false, choose to show text indicator showing the current color in HEX or DEC (see images) or not
                .create()
                .show(MainActivity.instance.getSupportFragmentManager(), "Pick Color");

        return true;
    }
}
