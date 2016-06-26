package net.parablack.clocktest.color;

import android.view.View;
import android.widget.ListView;

import net.parablack.clocktest.MainActivity;
import net.parablack.clocktest.R;
import net.parablack.schedulelib.color.ScheduleColors;
import net.parablack.schedulelib.utils.JSONReaderUtil;

import java.util.HashMap;

public class ColorManager {

    private ColorFragment fragment;

    public HashMap<Integer, String> posToName = new HashMap<>();
    private ScheduleColors colors;
    private ColorListAdapter<String> adapter;

    public ColorManager(ColorFragment fragment) {
        this.fragment = fragment;

        ColorDisplayNames.loadDisplayNames(MainActivity.instance);

        colors = new ScheduleColors();
        colors.loadFromPreferences(MainActivity.instance);
    }

    public void viewPresent(View main){
        View v1 = main.findViewById(R.id.colorList);
        ListView v = (ListView) v1;

        adapter = new ColorListAdapter<>(this.fragment.getActivity(), android.R.layout.simple_list_item_1, this);
        for(String s : colors.getColorNames()){
            adapter.add( ColorDisplayNames.getDisplayName(s));
            posToName.put(adapter.getPosition( ColorDisplayNames.getDisplayName(s)), s);
        }
        assert v != null;
        v.setAdapter(adapter);
        v.setLongClickable(true);
        v.setOnItemLongClickListener(new ColorChanger(this));
    }

    public void reset(){
        colors = new ScheduleColors(JSONReaderUtil.byAsset(MainActivity.instance.getAssets(), "colors.json"));
        colors.saveToPreferences(fragment.getActivity());
        adapter.notifyDataSetChanged();

    }

    public ColorFragment getFragment() {
        return fragment;
    }

    public ScheduleColors getCreator() {
        return colors;
    }

    public ColorListAdapter<String> getAdapter() {
        return adapter;
    }
}
