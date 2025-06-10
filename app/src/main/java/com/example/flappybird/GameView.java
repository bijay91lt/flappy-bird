package com.example.flappybird;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    private GameThread thread;
    private Bird bird;
    private Pipe pipe;
    private Bitmap background;
    private int bgX;

    public static int screenX, screenY;

    private boolean isGameOver = false;
    private boolean isStarted = false;
    private int score = 0;

    private Paint scorePaint = new Paint();

    public GameView(Context context) {
        super(context);
        getHolder().addCallback(this);
        thread = new GameThread(getHolder(), this);
        setFocusable(true);

        scorePaint.setTextSize(70);
        scorePaint.setColor(Color.WHITE);
        scorePaint.setAntiAlias(true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        bird = new Bird(getContext());
        pipe = new Pipe(getContext(), screenX, screenY, 400); // gap = 400

        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        GameView.screenX = width;
        GameView.screenY = height;

        // Load and scale background after screen size is known
        background = BitmapFactory.decodeResource(getResources(), R.drawable.bg);
        if (background == null) {
            Log.e("GameView", "Background image not found!");
        } else {
            background = Bitmap.createScaledBitmap(background, screenX, screenY, false);
            Log.d("GameView", "Background scaled to " + screenX + "x" + screenY);
        }
    }

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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (!isStarted) {
                isStarted = true;
            } else if (isGameOver) {
                restartGame();
            } else {
                bird.fly();
            }
            return true;
        }
        return false;
    }

    private void restartGame() {
        isGameOver = false;
        isStarted = false;
        score = 0;
        bird = new Bird(getContext());
        pipe = new Pipe(getContext(), screenX, screenY, 400);
    }

    public void update() {
        if (!isGameOver && isStarted) {
            bgX -= 10;
            if (bgX <= -screenX) {
                bgX = 0;
            }

            bird.update();
            pipe.update();

            checkCollision();
        }
    }

    private void checkCollision() {
        int birdX = (int) bird.getX();
        int birdY = (int) bird.getY();
        int birdWidth = bird.getWidth();
        int birdHeight = bird.getHeight();

        int pipeX = (int) pipe.getX();
        int pipeTopY = (int) pipe.getTopY();
        int pipeBottomY = (int) pipe.getBottomY();
        int pipeWidth = pipe.getWidth();

        if (birdX + birdWidth >= pipeX &&
                birdX <= pipeX + pipeWidth) {

            if (birdY <= pipe.getTopHeight() + pipeTopY ||
                    birdY + birdHeight >= pipeBottomY) {
                isGameOver = true;
            }
        }

        if (birdY < 0 || birdY + birdHeight > GameView.screenY) {
            isGameOver = true;
        }

        if (pipeX + pipeWidth < birdX && !pipe.isPassed()) {
            score++;
            pipe.setPassed(true);
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (canvas != null) {
            canvas.drawBitmap(background, bgX, 0, null);
            canvas.drawBitmap(background, bgX + screenX, 0, null);

            bird.draw(canvas);
            pipe.draw(canvas);

            canvas.drawText("" + score, 100, 150, scorePaint);

            if (!isStarted) {
                Paint startPaint = new Paint();
                startPaint.setTextSize(50);
                startPaint.setColor(Color.WHITE);
                startPaint.setAlpha(180);
                startPaint.setAntiAlias(true);
                String msg = "Tap to Start";
                float textWidth = startPaint.measureText(msg);
                canvas.drawText(msg, (screenX - textWidth) / 2, screenY / 2, startPaint);
            }

            if (isGameOver) {
                Paint gameOverPaint = new Paint();
                gameOverPaint.setTextSize(80);
                gameOverPaint.setColor(Color.RED);
                gameOverPaint.setAntiAlias(true);
                String gameOverText = "Game Over!";
                float gameOverWidth = gameOverPaint.measureText(gameOverText);
                canvas.drawText(gameOverText, (screenX - gameOverWidth) / 2, screenY / 2, gameOverPaint);

                Paint restartPaint = new Paint();
                restartPaint.setTextSize(50);
                restartPaint.setColor(Color.WHITE);
                restartPaint.setAntiAlias(true);
                String restartText = "Tap to Restart";
                float restartWidth = restartPaint.measureText(restartText);
                canvas.drawText(restartText, (screenX - restartWidth) / 2, screenY / 2 + 100, restartPaint);
            }
        }
    }
}