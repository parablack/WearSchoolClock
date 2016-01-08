package net.parablack.clocktest.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.View;

import net.parablack.clocktest.R;

public class MainActivityWear extends WearableActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_layout);
    }


    public void onMasterButtonClick(View v){
        System.out.println("Clicked! Du Penne Tagliatelle");
        Intent i = new Intent(this, MenuActivity.class);
        startActivity(i);
    }

}
