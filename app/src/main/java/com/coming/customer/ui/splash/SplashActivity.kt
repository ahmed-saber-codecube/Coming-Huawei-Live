package com.coming.customer.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.coming.customer.R
import com.coming.customer.ui.auth.activity.AuthActivity

class SplashActivity : AppCompatActivity() {

    private lateinit var handler: Handler
    private lateinit var runnable: Runnable
    private val splashDelay = 1000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_activity)
    }

    //TODO: Splash Flow, Uncomment Later
    override fun onResume() {
        super.onResume()
        runnable = Runnable {
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
            finish()
        }

        handler = Handler()
        handler.postDelayed(runnable, splashDelay)
    }

    override fun onStop() {
        super.onStop()
        handler.removeCallbacks(runnable)
    }
}
