package com.example.kotlininvaders

import android.content.Context
import android.graphics.Bitmap
import android.graphics.RectF
import android.graphics.BitmapFactory

class PlayerShip(context:Context, private val screenX:Int, screenY:Int) {
    // playship bitmap
    var bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.playership)

    // ship dims
    val width = screenX/20f
    private val height = screenY / 20f

    // track ship position
    val position = RectF(screenX/2f, screenY-height, screenX/2 + width, screenY.toFloat())

    // track ship pixel speed
    private val speed = 450f

    // accessible via Classname.property name
    companion object {
        // which ways can we move
        const val stopped = 0;
        const val left = 1
        const val right = 2
    }

    // track motion
    var moving = stopped

    init{
        // stretch bitmap to fit
        bitmap = Bitmap.createScaledBitmap(bitmap, width.toInt(), height.toInt(), false)
    }

    // update player position
    fun update(fps:Long){
        // move as long as we are inside the screen
        if(moving == left && position.left > 0){
            position.left -= speed/fps
        }else if(moving == right && position.left < screenX - width){
            position.left += speed/fps
        }
        position.right = position.left + width
    }
}