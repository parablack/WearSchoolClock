package net.parablack.clocktest.color;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import net.parablack.clocktest.MainActivity;

public class ColorListAdapter<T> extends ArrayAdapter<T> {
    public ColorListAdapter(Context context, int resource) {
        super(context, resource);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v =  super.getView(position, convertView, parent);
        String id = MainActivity.posToName.get(position);

        int color = MainActivity.instance.getColorCreator().getColor(id);
        v.setBackgroundColor(color);

        return v;
    }

}
