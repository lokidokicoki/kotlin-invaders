package com.example.kotlininvaders

//import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.graphics.Point
import androidx.fragment.app.FragmentActivity

class KotlinInvadersActivity : FragmentActivity() {

    private var kotlinInvadersView: KotlinInvadersView?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        //init gameview
        kotlinInvadersView = KotlinInvadersView(this, size)
        super.onCreate(savedInstanceState)                                                                                                                                                                                                                                                                                                      
        setContentView(kotlinInvadersView)
    }

    override fun onResume() {
        super.onResume()

        kotlinInvadersView?.resume()
    }

    override fun onPause() {
        super.onPause()
        kotlinInvadersView?.pause()
    }
}
