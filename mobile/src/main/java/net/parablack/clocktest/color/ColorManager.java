package net.parablack.clocktest.color;

import android.graphics.Color;
import android.view.View;
import android.widget.ListView;

import net.parablack.clocktest.MainActivity;
import net.parablack.clocktest.R;

import java.util.HashMap;

public class ColorManager {

    private ColorFragment fragment;

    public HashMap<Integer, String> posToName = new HashMap<>();
    private ColorCreator creator;
    private ColorListAdapter<String> adapter;

    public ColorManager(ColorFragment fragment) {
        this.fragment = fragment;

        creator = new ColorCreator(MainActivity.instance);



    }

    public void viewPresent(View main){
        View v1 = main.findViewById(R.id.colorList);
        ListView v = (ListView) v1;

        adapter = new ColorListAdapter<>(this.fragment.getActivity(), android.R.layout.simple_list_item_1, this);
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
        creator = new ColorCreator(fragment.getActivity(), true);
        creator.saveToPreferences(fragment.getActivity());
        adapter.notifyDataSetChanged();

    }

    public ColorFragment getFragment() {
        return fragment;
    }

    public ColorCreator getCreator() {
        return creator;
    }

    public ColorListAdapter<String> getAdapter() {
        return adapter;
    }
}
