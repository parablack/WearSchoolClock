package net.parablack.clocktest.app.games.repeat;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;


public class RepeatView extends View {

    private Paint linePaint = new Paint();

    public RepeatView(RepeatGame context) {
        super(context);

        setBackgroundColor(Color.RED);

        linePaint.setColor(Color.YELLOW);

        context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int centerW = getWidth() / 2;
        int centerH = getHeight() / 2;

        canvas.drawLine(centerW, 0, centerW, getHeight(), linePaint);
        linePaint.setStyle(Paint.Style.FILL);
        canvas.drawLine(0, centerH, getWidth(), centerH + 1, linePaint);

    }


    Handler h = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            return false;
        }
    });


}
