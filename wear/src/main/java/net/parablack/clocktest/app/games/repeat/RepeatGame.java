package net.parablack.clocktest.app.games.repeat;

import android.os.Bundle;

import net.parablack.clocktest.app.games.MainGameActivity;


public class RepeatGame extends MainGameActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(new RepeatView(this));

    }

    @Override
    public String getName() {
        return "Repeat";
    }
}
