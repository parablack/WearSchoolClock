package net.parablack.clocktest.color;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
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
