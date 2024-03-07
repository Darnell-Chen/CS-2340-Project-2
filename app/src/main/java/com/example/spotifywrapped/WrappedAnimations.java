package com.example.spotifywrapped;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

public class WrappedAnimations extends View {

    Rect rect;
    Paint paint;
    float xPos1 = 0;
    float yPos1 = 0;
    float xPos2 = 0;
    float yPos2 = 0;
    int point = 1;
    public WrappedAnimations(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // Draw Here...........
        // let's try circle
        paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(40);
        paint.setStyle(Paint.Style.STROKE);
        //canvas.drawCircle(xPos,yPos,100,paint);
        // Let's Draw Line.
        if (point == 2) {
            canvas.drawLine(xPos1,yPos1,xPos2,yPos2,paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (point == 1) {
            xPos1 = event.getX();
            yPos1 = event.getY();
            point = 2;
        } else {
            xPos2 = event.getX();
            yPos2 = event.getY();
            point = 1;
        }
        invalidate();
        return super.onTouchEvent(event);
    }
}
