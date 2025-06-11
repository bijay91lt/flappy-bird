package com.example.flappybird;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class Bird {
    private Bitmap birdBitmap;
    private float x, y;
    private float velocity = 0;
    private final float gravity = 1f;
    private final float lift = -15f;
    private RectF rect;

    public Bird(Context context, float screenX, float screenY) {
        birdBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.bird);
        x = screenX / 4;
        y = screenY / 2;
        rect = new RectF(x, y, x + birdBitmap.getWidth(), y + birdBitmap.getHeight());
    }

    public void update() {
        velocity += gravity;
        y += velocity;
        rect.set(x, y, x + birdBitmap.getWidth(), y + birdBitmap.getHeight());
    }

    public void draw(Canvas canvas, Paint paint) {
        canvas.drawBitmap(birdBitmap, x, y, paint);
    }

    public void flap() {
        velocity = lift;
    }

    public RectF getRect() {
        return rect;
    }
}