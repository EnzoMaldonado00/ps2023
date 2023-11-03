package com.maldEnz.ps.presentation.gamecontroller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;

import com.maldEnz.ps.R;
import com.maldEnz.ps.presentation.activity.GameOverActivity;

import java.util.ArrayList;
import java.util.Random;

public class GameView extends View {

    Bitmap background, ground, player;
    Rect rectBackground, rectGround;
    Context context;
    Handler handler;
    long UPDATE_MILLIS = 21;
    Runnable runnable;
    Paint textPaint = new Paint();
    float textSize = 120;
    int score = 0;
    int life = 1;
    static int dWidth, dHeight;
    Random random;
    float playerX, playerY;
    float oldX;
    float oldPlayerX;
    ArrayList<FireModel> fires;
    ArrayList<ExplosionModel> explosions;


    public GameView(Context context) {
        super(context);
        this.context = context;
        background = BitmapFactory.decodeResource(context.getResources(), R.drawable.background);
        ground = BitmapFactory.decodeResource(context.getResources(), R.drawable.ground);
        player = BitmapFactory.decodeResource(context.getResources(), R.drawable.player);
        Display gameDisplay = ((Activity) getContext()).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        gameDisplay.getSize(size);
        dWidth = size.x;
        dHeight = size.y;
        if (dWidth <= 480){
            UPDATE_MILLIS = 50;
            textSize = 60;
        }
        rectBackground = new Rect(0, 0, dWidth, dHeight);
        rectGround = new Rect(0, dHeight - ground.getHeight(), dWidth, dHeight);
        handler = new Handler();
        runnable = this::invalidate;
        textPaint.setColor(Color.rgb(255, 165, 0));
        textPaint.setTextSize(textSize);
        textPaint.setTextAlign(Paint.Align.LEFT);
        random = new Random();
        playerX = dWidth / 2 - player.getWidth() / 2;
        playerY = dHeight - ground.getHeight() - player.getHeight();
        fires = new ArrayList<>();
        explosions = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            FireModel fire = new FireModel(context);
            fires.add(fire);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(background, null, rectBackground, null);
        canvas.drawBitmap(ground, null, rectGround, null);
        canvas.drawBitmap(player, playerX, playerY, null);
        for (int i = 0; i < fires.size(); i++) {
            int fireFrame = fires.get(i).fireFrame;
            int fireCount = fires.get(i).fires.length;
            if (fireFrame >= 0 && fireFrame < fireCount) {
                canvas.drawBitmap(fires.get(i).getFire(fireFrame), fires.get(i).fireX, fires.get(i).fireY, null);
            }
            fires.get(i).fireFrame++;
            if (fires.get(i).fireFrame > 2) {
                fires.get(i).fireFrame = 0;
            }
            fires.get(i).fireY += fires.get(i).fireSpeed;
            if (fires.get(i).fireY + fires.get(i).getFireHeight() >= dHeight - ground.getHeight()) {
                score += 10;
                ExplosionModel explosion = new ExplosionModel(context);
                explosion.explosionX = fires.get(i).fireX;
                explosion.explosionY = fires.get(i).fireY;
                explosions.add(explosion);
                fires.get(i).resetPosition();
            }
        }
        for (int i = 0; i < fires.size(); i++) {
            if (fires.get(i).fireX + fires.get(i).getFireWidth() >= playerX &&
                    fires.get(i).fireX <= playerX + player.getWidth() &&
                    fires.get(i).fireY + fires.get(i).getFireWidth() >= playerY &&
                    fires.get(i).fireY + fires.get(i).getFireWidth() <= playerY + player.getHeight()) {
                life--;
                fires.get(i).resetPosition();
                if (life == 0) {
                    Intent intent = new Intent(context, GameOverActivity.class);
                    intent.putExtra("score", score);
                    context.startActivity(intent);
                    ((Activity) context).finish();
                }
            }
        }
        for (int i = 0; i < explosions.size(); i++) {
            canvas.drawBitmap(explosions.get(i).getExplosion(explosions.get(i).explosionFrame), explosions.get(i).explosionX, explosions.get(i).explosionY, null);
            explosions.get(i).explosionFrame++;
            if (explosions.get(i).explosionFrame > 3) {
                explosions.remove(i);
            }
        }
        canvas.drawText("" + score, 20, textSize, textPaint);
        handler.postDelayed(runnable, UPDATE_MILLIS);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        if (touchY >= playerY) {
            int action = event.getAction();
            if (action == MotionEvent.ACTION_DOWN) {
                oldX = event.getX();
                oldPlayerX = playerX;
            }
            if (action == MotionEvent.ACTION_MOVE) {
                float shift = oldX - touchX;
                float newPlayerX = oldPlayerX - shift;
                if (newPlayerX <= 0) {
                    playerX = 0;
                } else if (newPlayerX >= dWidth - player.getWidth()) {
                    playerX = dWidth - player.getWidth();
                } else {
                    playerX = newPlayerX;
                }
            }
        }
        return true;
    }
}
