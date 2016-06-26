package net.parablack.clocktest.color;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import net.parablack.clocktest.MainActivity;
import net.parablack.clocktest.R;

public class ColorFragment extends Fragment implements View.OnClickListener {

    private ColorManager colorManager;
    private View view;

    public ColorFragment() {
        colorManager = new ColorManager(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_color, container, false);
        Button b = (Button) view.findViewById(R.id.upload_colors);
        b.setOnClickListener(this);
        colorManager.viewPresent(view);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_colors, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.delete_clock) {
            MainActivity.instance.getCommunicator().sendNewColorPreset("{}");
            return true;
        }
        if(id == R.id.color_reset){
            getColorManager().reset();
            colorManager.getAdapter().notifyDataSetChanged();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }


    public ColorManager getColorManager() {
        return colorManager;
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.upload_colors) {
            MainActivity.instance.getCommunicator().sendNewColorPreset(colorManager.getCreator().toJSON() + System.currentTimeMillis());
        }

    }
}
