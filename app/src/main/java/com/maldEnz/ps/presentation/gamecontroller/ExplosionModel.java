package com.maldEnz.ps.presentation.gamecontroller;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.maldEnz.ps.R;

public class ExplosionModel {

    Bitmap[] explosions = new Bitmap[4];
    int explosionFrame = 0;
    int explosionX, explosionY;

    public ExplosionModel(Context context) {
        explosions[0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.explode_0);
        explosions[1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.explode_1);
        explosions[2] = BitmapFactory.decodeResource(context.getResources(), R.drawable.explode_2);
        explosions[3] = BitmapFactory.decodeResource(context.getResources(), R.drawable.explode_3);
    }

    public Bitmap getExplosion(int explosionFrame) {
        return explosions[explosionFrame];
    }
}
