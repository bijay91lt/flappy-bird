package com.example.flappybird;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import java.util.Random;

public class Pipe {
    private Bitmap topPipe, bottomPipe;
    private float x, topY, bottomY;
    private float pipeGap;
    private float speed = 10;
    private int screenHeight;
    private boolean passed = false;

    public Pipe(Context context, int screenWidth, int screenHeight, float pipeGap) {
        this.pipeGap = pipeGap;
        this.screenHeight = screenHeight;

        // Load images
        topPipe = BitmapFactory.decodeResource(context.getResources(), R.drawable.pipe_top);
        bottomPipe = BitmapFactory.decodeResource(context.getResources(), R.drawable.pipe_bottom);

        // Resize pipes to fit screen (adjust scale as needed)
        int pipeHeight = (int) (screenHeight * 0.35f); // 35% of screen height
        int pipeWidth = (int) (pipeHeight * 0.4f);     // Aspect ratio ~2:5

        topPipe = Bitmap.createScaledBitmap(topPipe, pipeWidth, pipeHeight, false);
        bottomPipe = Bitmap.createScaledBitmap(bottomPipe, pipeWidth, pipeHeight, false);

        resetPosition(screenWidth);
    }

    public void update() {
        x -= speed;

        if (x + topPipe.getWidth() < 0) {
            resetPosition(GameView.screenX);
        }
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(topPipe, x, topY, null);
        canvas.drawBitmap(bottomPipe, x, bottomY, null);
    }

    // Reset pipe to right side with random Y position
    public void resetPosition(int screenWidth) {
        x = screenWidth;

        // Randomize middle gap between 20% and 80% of screen height
        Random rand = new Random();
        int midY = rand.nextInt((int) (screenHeight * 0.6f)) + (int) (screenHeight * 0.2f);

        topY = midY - pipeGap / 2 - topPipe.getHeight();
        bottomY = midY + pipeGap / 2;

        passed = false;
    }

    // Getter methods
    public float getX() {
        return x;
    }

    public float getTopY() {
        return topY;
    }

    public float getBottomY() {
        return bottomY;
    }

    public Bitmap getTopPipe() {
        return topPipe;
    }

    public Bitmap getBottomPipe() {
        return bottomPipe;
    }

    public int getWidth() {
        return topPipe.getWidth();
    }

    public int getTopHeight() {
        return topPipe.getHeight();
    }

    public int getBottomHeight() {
        return bottomPipe.getHeight();
    }

    public boolean isPassed() {
        return passed;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }
}