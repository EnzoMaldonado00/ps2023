package com.maldEnz.ps.presentation.gamecontroller

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.maldEnz.ps.R
import java.util.Random

class FireModel(context: Context) {
    val fire = Array<Bitmap>(3) {index ->
        BitmapFactory.decodeResource(context.resources, R.drawable.fire_0 + index)
        BitmapFactory.decodeResource(context.resources, R.drawable.fire_1 + index)
        BitmapFactory.decodeResource(context.resources, R.drawable.fire_2 + index)
    }
    var fireFrame = 0
    var fireX: Int? = null
    var fireY: Int? = null
    var fireSpeed: Int? = null
    var random = Random()

    init {
        resetPosition()
    }


    fun resetPosition(){
       // fireX = random.nextInt(GameView.dWidth - getFireWidth())
        fireY = -200 + random.nextInt(600) * -1
        fireSpeed = 35 + random.nextInt(16)
    }

    fun getFire(fireFrame: Int): Bitmap? {
        return fire.getOrNull(fireFrame)
    }

    fun getFireWidth() : Int{
        return fire[0]!!.width
    }

    fun getFireHeight(): Int{
        return fire[0]!!.height
    }

}