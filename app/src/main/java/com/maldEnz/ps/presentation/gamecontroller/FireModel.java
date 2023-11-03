package com.maldEnz.ps.presentation.gamecontroller;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.maldEnz.ps.R;

import java.util.Random;

public class FireModel {
    Bitmap[] fires = new Bitmap[3];
    int fireFrame = 0;
    int fireX, fireY, fireSpeed;
    Random random;

    public FireModel(Context context){
        fires[0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.fire_0);
        fires[1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.fire_1);
        fires[2] = BitmapFactory.decodeResource(context.getResources(), R.drawable.fire_2);
        random = new Random();
        resetPosition();
    }

    public void resetPosition(){
        fireX = random.nextInt(GameView.dWidth - getFireWidth());
        fireY = -200 + random.nextInt(600) * -1;
        fireSpeed = 35 + random.nextInt(16);
    }

    public Bitmap getFire(int fireFrame){
        return fires[fireFrame];
    }

    public int getFireWidth(){
        return fires[0].getWidth();
    }

    public int getFireHeight(){
        return fires[0].getHeight();
    }
}
