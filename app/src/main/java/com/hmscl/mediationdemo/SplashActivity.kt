package com.hmscl.mediationdemo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.hmscl.mediationdemo.wizard.WizardActivity


class SplashActivity : AppCompatActivity() {
    private val SPLASH_TIME_OUT: Long = 500
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Handler(Looper.getMainLooper()).postDelayed({
            navigateToNext()
            finish()
        }, SPLASH_TIME_OUT)
    }

    private fun navigateToNext() {
        val prefs = this.getPreferences(Context.MODE_PRIVATE)
        val first_launch = prefs.getBoolean(getString(R.string.first_launch),true)
        if (first_launch) {
            val edit = prefs.edit()
            edit.putBoolean(getString(R.string.first_launch), false)
            edit.apply()
            startActivity(Intent(this, WizardActivity::class.java))
        } else {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}