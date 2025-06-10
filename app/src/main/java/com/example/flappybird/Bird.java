package com.example.flappybird;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Bird {
    private Bitmap birdBitmap;
    private float x, y;
    private float velocity = 0;
    private float gravity = 1.5f;

    public Bird(Context context) {
        birdBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.bird);
        x = 100;
        y = GameView.screenY / 2 - birdBitmap.getHeight() / 2;
    }

    public void update() {
        velocity += gravity;
        y += velocity;
    }

    public void fly() {
        velocity = -20;
    }

    public void draw(android.graphics.Canvas canvas) {
        canvas.drawBitmap(birdBitmap, x, y, null);
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public Bitmap getBitmap() { return birdBitmap; }
    public int getWidth() { return birdBitmap.getWidth(); }
    public int getHeight() { return birdBitmap.getHeight(); }
}