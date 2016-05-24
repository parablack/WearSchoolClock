package net.parablack.clocktest.color;

import android.content.Context;
import android.view.View;
import android.widget.ListView;

import net.parablack.clocktest.ClockCommunicator;
import net.parablack.clocktest.MainActivity;
import net.parablack.clocktest.R;

import java.util.HashMap;

public class ColorManager {

    private MainActivity context;

    public HashMap<Integer, String> posToName = new HashMap<>();
    private ColorCreator creator;
    private ColorListAdapter<String> adapter;

    public ColorManager(MainActivity context) {
        this.context = context;

        creator = new ColorCreator(context);

        View v1 = context.findViewById(R.id.colorList);
        ListView v = (ListView) v1;

        adapter = new ColorListAdapter<>(context, android.R.layout.simple_list_item_1, this);
        for(String s : creator.getColorNames()){
            adapter.add(ColorCreator.getDisplayName(s));
            posToName.put(adapter.getPosition(ColorCreator.getDisplayName(s)), s);
        }
        assert v != null;
        v.setAdapter(adapter);
        v.setLongClickable(true);
        v.setOnItemLongClickListener(new ColorChanger(this));

    }

    public void reset(){
        creator = new ColorCreator(context, true);
        creator.saveToPreferences(context);
        adapter.notifyDataSetChanged();

    }

    public MainActivity getContext() {
        return context;
    }

    public ColorCreator getCreator() {
        return creator;
    }

    public ColorListAdapter<String> getAdapter() {
        return adapter;
    }
}
