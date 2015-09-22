package net.parablack.clocktest.app;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.View;

import net.parablack.clocktest.R;

/**
 * Created by Simon on 22.09.2015.
 */
public class MenuActivity extends WearableActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_layout);

    }


    public void onMasterButtonClick(View v){
        System.out.println("Clicked! Du Penne Tagliatelle");
    }

}
