package com.example.flappybird;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.util.ArrayList;
import java.util.List;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    private GameThread thread;
    private Bird bird;
    private List<Pipe> pipes;
    private Bitmap background;
    private int score = 0;
    private boolean gameOver = false;
    private boolean isPaused = false;

    public GameView(Context context) {
        super(context);
        getHolder().addCallback(this);
        thread = new GameThread(getHolder(), this);
        setFocusable(true);

        background = BitmapFactory.decodeResource(getResources(), R.drawable.bg);
        pipes = new ArrayList<>();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        resetGame();
        thread.setRunning(true);
        thread.start();
    }

    private void resetGame() {
        bird = new Bird(getContext(), getWidth(), getHeight());

        // Scale background to fit screen
        background = Bitmap.createScaledBitmap(background, getWidth(), getHeight(), false);

        pipes.clear();
        for (int i = 0; i < 2; i++) {
            pipes.add(new Pipe(getContext(), getWidth() + i * (getWidth() / 2), getHeight()));
        }

        score = 0;
        gameOver = false;
        isPaused = false;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        if (canvas != null) {
            canvas.drawBitmap(background, 0, 0, null);
            Paint paint = new Paint();
            bird.draw(canvas, paint);

            for (Pipe pipe : pipes) {
                pipe.draw(canvas, paint);
            }

            paint.setTextSize(80);
            paint.setColor(android.graphics.Color.WHITE);
            canvas.drawText("Score: " + score, 100, 100, paint);

            if (gameOver) {
                paint.setTextSize(120);
                canvas.drawText("Game Over", getWidth() / 3, getHeight() / 2, paint);
            }
        }
    }

    public void update() {
        if (!gameOver && !isPaused) {
            bird.update();

            for (Pipe pipe : pipes) {
                pipe.update();

                if (pipe.collidesWith(bird)) {
                    gameOver = true;
                    thread.setRunning(false);
                }

                // Scoring logic
                if (!pipe.isPassed() && pipe.getX() + pipe.getWidth() < bird.getRect().left) {
                    score++;
                    pipe.setPassed(true);
                }
            }

            // Recycle pipes
            if (pipes.get(0).getX() + pipes.get(0).getWidth() < 0) {
                pipes.remove(0);
                pipes.add(new Pipe(getContext(), getWidth(), getHeight()));
            }

            if (bird.getRect().top < 0 || bird.getRect().bottom > getHeight()) {
                gameOver = true;
                thread.setRunning(false);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (!gameOver) {
                bird.flap();
            } else {
                resetGame();
                thread = new GameThread(getHolder(), this);
                thread.setRunning(true);
                thread.start();
            }
        }
        return true;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        thread.setRunning(false);
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
