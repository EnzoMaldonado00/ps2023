package com.maldEnz.ps.presentation.gamecontroller

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.maldEnz.ps.R

class ExplodeModel(context: Context) {

    val explosion = Array<Bitmap>(4) { index ->
        BitmapFactory.decodeResource(context.resources, R.drawable.explode_0 + index)
        BitmapFactory.decodeResource(context.resources, R.drawable.explode_1 + index)
        BitmapFactory.decodeResource(context.resources, R.drawable.explode_2 + index)
        BitmapFactory.decodeResource(context.resources, R.drawable.explode_3 + index)
    }
    var explosionFrame = 0
    var explodeX: Int? = null
    var explodeY: Int? = null

    fun getExplosion(explosionFrame: Int): Bitmap {
        return explosion[explosionFrame]
    }
}
