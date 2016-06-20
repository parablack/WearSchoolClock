package net.parablack.clocktest.color;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ColorListAdapter<T> extends ArrayAdapter<T> {

    private ColorManager manager;

    public ColorListAdapter(Context context, int resource, ColorManager manager) {
        super(context, resource);
        this.manager = manager;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v =  super.getView(position, convertView, parent);
        String id = manager.posToName.get(position);

        int color = manager.getCreator().getColor(id);
        if(Color.red(color) + Color.blue(color) + Color.green(color) < 200){
       //     Log.d("Clock", "getView: Background too moody, setting color to white ("+id+"): " + Color.red(color) +" + " + Color.blue(color) + " + " +Color.green(color));
            ((TextView) v).setTextColor(Color.WHITE);
        }else ((TextView) v).setTextColor(Color.BLACK);
        v.setBackgroundColor(color);

        return v;
    }

}
