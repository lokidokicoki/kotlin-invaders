package com.example.kotlininvaders

import android.content.Context
import android.content.SharedPreferences
import android.graphics.*
import android.view.SurfaceView
import android.util.Log
import android.view.MotionEvent


class KotlinInvadersView(context:Context, private val size:Point):SurfaceView(context), Runnable {
    // sounds
    private var soundPlayer:SoundPlayer = SoundPlayer(context)

    // game thread
    private val gameThread = Thread(this)

    //pause/resume toggle
    private var playing = false

    // paused on start
    private var paused = true

    // canvas and paint
    private var canvas = Canvas()
    private var paint = Paint()

    // player ship
    private var playerShip:PlayerShip = PlayerShip(context, size.x, size.y)

    // invaders
    private val invaders = ArrayList<Invader>()
    private var numberOfInvaders = 0

    // defences
    private val bricks = ArrayList<DefenceBrick>()
    private var numberOfBricks:Int = 0

    // bullets
    // player bullet, faster and half the length of invaders
    private var playerBullet = Bullet(size.y, 1200f, 40f)

    // invaders bullets
    private val invadersBullets = ArrayList<Bullet>()
    private var nextBullet = 0
    private var maxInvaderBullets = 10

    // player stats
    private var score = 0
    private var lives = 3
    private var waves = 1
    private var highScore = 0

    // sound duration
    private var menaceInterval:Long = 1000

    // which sound to play
    private var uhOrOh:Boolean = true

    // when did the last sound play
    private var lastMenaceTime = System.currentTimeMillis()

    private fun prepareLevel(){
        //init game objects
        // build army of invaders
        Invader.numberOfInvaders = 0
        numberOfInvaders = 0;

        for (column in 0..10){
            for (row in 0..5){
                invaders.add(Invader(context, row, column, size.x, size.y))

                numberOfInvaders++
            }
        }

        // build shelters
        numberOfBricks=0

        for(shelterNumber in 0..4){
            for(column in 0..18){
                for(row in 0..8){
                    bricks.add(DefenceBrick(row, column, shelterNumber, size.x, size.y))

                    numberOfBricks++
                }
            }
        }

        // init invader bullet array
        for(i in 0 until maxInvaderBullets){
            invadersBullets.add(Bullet(size.y))
        }
    }

    override fun run(){
        // track frame rate
        var fps:Long = 0;

        while(playing) {
            // capture start time
            val startFrameTime = System.currentTimeMillis()

            // update frame
            if (!paused) {
                update(fps)
            }

            // draw frame
            draw()

            // calc fpx rate
            val timeThisFrame = System.currentTimeMillis() - startFrameTime
            if (timeThisFrame >= 1) {
                fps = 1000 / timeThisFrame
            }
        }
    }

    private fun update(fps:Long){
        // update game state

        // move the player ship
        playerShip.update(fps)

        // updaet player bullet if active
        if(playerBullet.isActive){
            playerBullet.update(fps)
        }

        //update invader bullets
        for(bullet in invadersBullets){
            if(bullet.isActive){
                bullet.update(fps)
            }
        }

        // check invader bounds
        var bumped = false

        // has the player lost
        var lost = false

        // update all visible invaders
        for(invader in invaders){
            if(invader.isVisible) {
                // move the next invader
                invader.update(fps)

                // if the move forces them out of bounds, bump
                if(invader.position.left > size.x - invader.width || invader.position.left < 0){
                    bumped = true
                }
            }
        }

        // did an invader get bumped
        if(bumped){
            // move down and reverse direciton
            for(invader in invaders){
                invader.dropDownAndReverse(waves)

                // have they landed?
                if(invader.position.bottom >= size.y && invader.isVisible){
                    lost = true
                }
            }
        }

    }

    private fun draw(){
        // check holder surface is valid
        if(holder.surface.isValid){
            // lock canvas
            canvas = holder.lockCanvas()

            // draw bg color
            canvas.drawColor(Color.argb(255, 0, 0, 0))

            // choose brush color for painting
            paint.color = Color.argb(255, 0, 255, 0)

            // draw all game objects here
            // draw player ship
            canvas.drawBitmap(playerShip.bitmap, playerShip.position.left, playerShip.position.top, paint)

            // draw invaders
            for(invader in invaders){
                if(invader.isVisible){
                    if(uhOrOh){
                        canvas.drawBitmap(Invader.bitmap1 as Bitmap, invader.position.left, invader.position.top, paint)

                    }else{
                        canvas.drawBitmap(Invader.bitmap2 as Bitmap, invader.position.left, invader.position.top, paint)
                    }
                }
            }

            // draw shelters
            for(brick in bricks){
                if(brick.isVisible){
                    canvas.drawRect(brick.position, paint)
                }
            }

            // draw bullets, player first
            if(playerBullet.isActive){
                canvas.drawRect(playerBullet.position, paint)
            }

            for(bullet in invadersBullets){
                if(bullet.isActive){
                    canvas.drawRect(bullet.position, paint)
                }
            }

            // draw score and lives
            // change paint color
            paint.color = Color.argb(255,255,255,255)
            paint.textSize = 70f

            canvas.drawText("Score: $score Lives: $lives Wave: $waves HI; $highScore", 20f, 75f, paint)

            holder.unlockCanvasAndPost(canvas)
        }
    }

    // if paused, shutdown the thread
    fun pause(){
        playing = false

        try{
            gameThread.join()
        }catch (e:InterruptedException){
            Log.e("Error:", "Joining thread")
        }
    }

    // is game started, start thread
    fun resume(){
        playing = true
        prepareLevel()
        gameThread.start()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when(event.action and MotionEvent.ACTION_MASK){
            //player has touched the screen, or dragged
            MotionEvent.ACTION_POINTER_DOWN,
                MotionEvent.ACTION_DOWN,
                MotionEvent.ACTION_MOVE->{
                paused = false

                if(event.y > size.y - size.y / 8){
                    if(event.x > size.x / 2){
                        playerShip.moving = PlayerShip.right
                    }else{
                        playerShip.moving = PlayerShip.left
                    }
                }

                if(event.y < size.y - size.y / 8){
                    // shots fired
                    if(playerBullet.shoot(
                            playerShip.position.left+playerShip.width / 2f,
                            playerShip.position.top,
                            playerBullet.up
                            )){
                        soundPlayer.playSound(SoundPlayer.shootID)
                    }
                }
            }

            // player removed finger from screen
            MotionEvent.ACTION_POINTER_UP,
                MotionEvent.ACTION_UP -> {
                if(event.y > size.y - size.y / 10){
                    playerShip.moving = PlayerShip.stopped
                }
            }
        }
        return true
    }
}