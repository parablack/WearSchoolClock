package net.parablack.clocktest.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.View;

import net.parablack.clocktest.R;
import net.parablack.clocktest.watchface.SchoolWatchFaceService;
import net.parablack.clocktest.watchface.drawer.mode.PixelDrawer;

public class MainActivityWear extends WearableActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_layout);
    }


    public void onMasterButtonClick(View v){
        if(v.getId() == R.id.main_button){
            System.out.println("Clicked! Test {1}");
            Intent i = new Intent(this, MenuActivity.class);
            startActivity(i);

        }

    }

    public void onDesignChangeButtonClicked(View v){

        System.out.println("Design Button Change clicked --> Applying");
        SchoolWatchFaceService.getInstance().getWatchEngine().getDrawer().setCurrentDrawer(new PixelDrawer(SchoolWatchFaceService.getInstance().getWatchEngine().getDrawer()));

    }

    public void onReloadButtonClicked(View v){

        System.out.println("Reload Button clicked --> Force reload");
        SchoolWatchFaceService.getInstance().getWatchEngine().reload();
    }

    public void onColorChangeButtonClicked(View v){

        System.out.println("Color Button clicked --> Color changing");
        SchoolWatchFaceService.getInstance().getWatchEngine().reloadColors();
    }

}
