package com.example.kotlininvaders

import android.graphics.RectF

class Bullet(screenY:Int, private val speed:Float = 350f, heightModifier:Float =20f) {
    val position = RectF()

    // which direction is it shooting
    val up = 0
    val down = 1

    // going nowhere
    private var heading = -1

    private var width = 2
    private var height = screenY / heightModifier

    var isActive = false

    fun shoot(startX:Float, startY:Float, direction:Int):Boolean{
        if(!isActive){
            position.left = startX
            position.top = startY
            position.right = position.left + width
            position.bottom = position.top + height
            heading = direction
            isActive = true
            return true
        }

        // if active already
        return false
    }

    fun update(fps:Long){
        // just move up or down
        if(heading == up){
            position.top -= speed / fps
        }else{
            position.top += speed / fps
        }

        // update bottom position
        position.bottom = position.top + height
    }
}