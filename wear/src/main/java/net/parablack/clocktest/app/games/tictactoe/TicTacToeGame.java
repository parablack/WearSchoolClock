package net.parablack.clocktest.app.games.tictactoe;

import android.os.Bundle;

import net.parablack.clocktest.app.games.MainGameActivity;


public class TicTacToeGame extends MainGameActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(new TicTacToeView(this));

    }

    @Override
    public String getName() {
        return "Tic-Tac-Toe";
    }
}
