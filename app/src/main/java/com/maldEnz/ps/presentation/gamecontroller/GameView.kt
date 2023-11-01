package com.maldEnz.ps.presentation.gamecontroller

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.graphics.Rect
import android.os.Handler
import android.view.View
import java.util.Random

class GameView(context: Context) : View(context) {

    var background: Bitmap? = null
    var ground: Bitmap? = null
    var player: Bitmap? = null
    var rectBackground: Rect? = null
    //var handler: Handler? = null
    val UPDATE_MILIS = 30
    var textPaint: Paint? = null
    var healthPaint: Paint? = null
    var textSize = 120f
    var points = 0
    var life = 3
    var dWidth: Int? = null
    var dHeight: Int? = null
    var random: Random? = null
    var playerX: Float? = null
    var playerY: Float? = null
    var oldX: Float? = null
    var oldPlayerX: Float? = null
    var fires = Array<FireModel?>(1) { null }
    var explosions = Array<ExplodeModel?>(1) { null }


    init {
        //background = BitmapFactory.decodeResource(resources)
    }
}