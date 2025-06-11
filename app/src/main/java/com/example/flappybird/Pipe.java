package com.example.flappybird;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class Pipe {
    private Bitmap topPipe, bottomPipe;
    private float x, topY, bottomY;
    private float width, height;
    private final float gap;
    private boolean passed = false;
    private float speed = 10f;
    private RectF topRect, bottomRect;

    public Pipe(Context context, float startX, float screenHeight) {
        Bitmap rawTop = BitmapFactory.decodeResource(context.getResources(), R.drawable.pipe_top);
        Bitmap rawBottom = BitmapFactory.decodeResource(context.getResources(), R.drawable.pipe_bottom);

        // ✅ Scale pipes to screen height
        this.height = screenHeight / 3f;
        this.width = height / 3f;

        this.topPipe = Bitmap.createScaledBitmap(rawTop, (int) width, (int) height, false);
        this.bottomPipe = Bitmap.createScaledBitmap(rawBottom, (int) width, (int) height, false);

        this.gap = screenHeight / 4f;
        this.x = startX;

        float centerY = (float) (Math.random() * (screenHeight - gap - 400)) + 200;
        this.topY = centerY - gap / 2 - height;
        this.bottomY = centerY + gap / 2;

        this.topRect = new RectF(x, topY, x + width, topY + height);
        this.bottomRect = new RectF(x, bottomY, x + width, bottomY + height);
    }

    public void update() {
        x -= speed;
        topRect.offset(-speed, 0);
        bottomRect.offset(-speed, 0);
    }

    public void draw(Canvas canvas, Paint paint) {
        canvas.drawBitmap(topPipe, null, topRect, paint);
        canvas.drawBitmap(bottomPipe, null, bottomRect, paint);
    }

    public boolean collidesWith(Bird bird) {
        return RectF.intersects(topRect, bird.getRect()) || RectF.intersects(bottomRect, bird.getRect());
    }

    public float getX() {
        return x;
    }

    public float getWidth() {
        return width;
    }

    public boolean isPassed() {
        return passed;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }
}
