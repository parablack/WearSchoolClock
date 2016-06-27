package net.parablack.clocktest.app.games.tictactoe;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;


public class TicTacToeView extends View implements View.OnTouchListener{

    private final int FIELD_SIZE = 3;

    private Paint linePaint = new Paint();
    private Paint[] byPlayer = new Paint[3];

    private int[][] ownedFields = new int[FIELD_SIZE ][FIELD_SIZE];
    private float[] points = new float[FIELD_SIZE + 1];
    private Handler handler = new Handler();

    private int player;

    public TicTacToeView(TicTacToeGame context) {
        super(context);

        setBackgroundColor(Color.WHITE);
        linePaint.setColor(Color.BLACK);


        setOnTouchListener(this);

        context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public void reset(){
        ownedFields = new int[FIELD_SIZE][FIELD_SIZE];
        player = 1;
        byPlayer[1] = new Paint();
        byPlayer[2] = new Paint();
        byPlayer[0] = new Paint();
        byPlayer[0].setColor(Color.WHITE);
        byPlayer[1].setColor(Color.BLUE);
        byPlayer[2].setColor(Color.RED);
        invalidate();
    }

    public void resetDelayed(){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                reset();
            }
        }, 1500);
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if(hasFocus){
            reset();
            float widthPerSq = (getWidth() + 6) / FIELD_SIZE;
            Log.d("ClockGame", "RepeatView: "+  widthPerSq + " " + getWidth());
            for(int i = 0; i < points.length; i++){
                // SQUARED FIELD, ONE LOOP IS ENOUGH
                points[i] = i * widthPerSq;
                Log.d("ClockGame", "RepeatView: "+  i + " + " + points[i]);

            }
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.WHITE);
      //  linePaint.setStyle(Paint.Style.FILL);

        for(int i = 0; i < ownedFields.length; i++){
            for(int j = 0;j < ownedFields.length; j++){
                canvas.drawRect(points[i], points[j], points[i+1], points[j+1], byPlayer[ownedFields[i][j]]);
            }
        }

        for(int i = 1; i < points.length - 1; i++){

            Log.i("Clock", "onDraw: "+ i+" " +points[i-1] +" : " + points[i]);

            canvas.drawLine(0, points[i], getHeight(), points[i], linePaint);
            canvas.drawLine(points[i], 0, points[i], getWidth(), linePaint);

        }
    }

    public void checkWin(){
        boolean allO = true;
        int winner = 0;
        for(int i = 0; i < ownedFields.length; i++){
            int lastJ = 0;
            for(int j = 0;j < ownedFields.length; j++){
                allO = allO & ownedFields[i][j] != 0;
            }
        }

        for(int i = 0; i < ownedFields.length; i++){
            if(ownedFields[i][0] == ownedFields[i][1] && ownedFields[i][0] == ownedFields[i][2] && winner == 0) winner = ownedFields[i][0];
            if(ownedFields[0][i] == ownedFields[1][i] && ownedFields[0][i] == ownedFields[2][i]&& winner == 0) winner = ownedFields[0][i];
        }
        if(ownedFields[0][0] == ownedFields[1][1] && ownedFields[0][0] == ownedFields[2][2]&& winner == 0) winner = ownedFields[1][1];
        if(ownedFields[0][2] == ownedFields[1][1] && ownedFields[2][0] == ownedFields[1][1]&& winner == 0) winner = ownedFields[1][1];

        if(winner != 0){
            if(winner == 1) simpleToast("Blau gewinnt!");
            else simpleToast("Rot gewinnt!");
            resetDelayed();
        }
        else
        if(allO){
            simpleToast("Unentschieden!");
            resetDelayed();
        }

    }



    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if(event.getAction() == MotionEvent.ACTION_DOWN){
            int x = -1,y = -1;
            for(int i = 0; i < points.length; i++){
                if(points[i] < event.getX()) x = i;
                if(points[i] < event.getY()) y = i;
            }
            Log.d("ClockGame", "onTouch: x=" + x + " y=" + y);
            if(ownedFields[x][y] == 0){ // not occupied
                ownedFields[x][y] = player;
                invalidate();
                checkWin();
                player = player == 1 ? 2 : 1;

            }else {
                simpleToast("Besetzt!");
            }


        }
        return true;
    }

    private void simpleToast(String text){
        Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
    }
}
