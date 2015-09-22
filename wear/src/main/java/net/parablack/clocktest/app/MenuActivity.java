package net.parablack.clocktest.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import net.parablack.clocktest.R;
import net.parablack.clocktest.app.games.MainGameActivity;
import net.parablack.clocktest.app.games.repeat.RepeatGame;


public class MenuActivity extends WearableActivity {

    private ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.game_choose_layout);

        listView = (ListView) findViewById(R.id.gameList);

        ArrayAdapter<RepeatGame> ad = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);

        ad.add(new RepeatGame());
      //  ad.add("Das");
      //  ad.add("Programmo");


        listView.setAdapter(ad);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MainGameActivity act = (MainGameActivity) listView.getItemAtPosition(position);
                Intent i = new Intent(MenuActivity.this, act.getClass());
                startActivity(i);
            }
        });
    }





}
