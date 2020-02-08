package com.example.kotlininvaders

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.graphics.RectF
import java.util.*

class Invader(context:Context, row:Int, column:Int, screenX:Int, screenY: Int) {

    // how wide, high and spaced out the invaders are
    var width = screenX / 35f
    private var height = screenY / 35f
    private var padding = screenX / 45

    var position = RectF(
        column * (width  + padding),
        100 + row * (width + padding/4),
        column * (width+padding)+width,
        100 + row * (width + padding/4) + height
    )

    // invader speed in pixels per second
    private var speed = 40f

    private val left = 1
    private val right = 2

    //is the moving and in which direction
    private var shipMoving = right

    var isVisible = true

    companion object{
        // ship bitmap
        //var bitmap1 = BitmapFactory.decodeResource(context.resources, R.drawable.invader1)
        //var bitmap2 = BitmapFactory.decodeResource(context.resources, R.drawable.invader2)

        var bitmap1: Bitmap? = null
        var bitmap2: Bitmap? = null

        // track live instances
        var numberOfInvaders = 0
    }

    init {
        bitmap1 = BitmapFactory.decodeResource(context.resources, R.drawable.invader1)
        bitmap2 = BitmapFactory.decodeResource(context.resources, R.drawable.invader2)

        var b1 = bitmap1
        if(b1 != null) {
            // stretch bitmaps to fit screensize
            bitmap1 = Bitmap.createScaledBitmap(b1, (width.toInt()), (height.toInt()), false)
        }

        b1 = bitmap2
        if(b1 != null) {
            bitmap2 = Bitmap.createScaledBitmap(b1, (width.toInt()), (height.toInt()), false)
        }
        numberOfInvaders++
    }

    fun update(fps:Long){
        if(shipMoving == left){
            position.left -= speed/fps
        }

        if(shipMoving == right){
            position.left += speed/fps
        }

        position.right = position.left + width
    }

    fun dropDownAndReverse(waveNumber:Int){
        shipMoving = if(shipMoving == left){
            right
        }else{
            left
        }

        position.top += height
        position.bottom += height

        // the later the wave, the more it speeds up
        speed *= (1.1f + (waveNumber.toFloat() / 20))
    }

    fun takeAim(playerShipX: Float, playerShipLength: Float, waves:Int):Boolean{
        val generator = Random()
        var randomNumber:Int

        // if near the player, take a shot
        if(playerShipX + playerShipLength > position.left &&
                playerShipX + playerShipLength < position.left + width ||
                playerShipX > position.left && playerShipX < position.left + width){
            // fewer invaders = more shots
            // higeher the wave = more shots
            randomNumber = generator.nextInt(100 * numberOfInvaders) / waves

            if(randomNumber == 0){
                return true
            }
        }

        // fire randomly
        randomNumber = generator.nextInt(150 * numberOfInvaders)
        return randomNumber == 0
    }


}