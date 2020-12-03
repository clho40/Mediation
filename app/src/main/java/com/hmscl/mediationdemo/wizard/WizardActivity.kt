package com.hmscl.mediationdemo.wizard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hmscl.mediationdemo.MainActivity
import com.hmscl.mediationdemo.R
import com.hmscl.mediationdemo.Utils
import kotlinx.android.synthetic.main.activity_wizard.*

class WizardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wizard)

        card_beginner.setOnClickListener {
            Utils.showToast(this,"Welcome to join the club!")
            navigateToMainActivity()
        }

        card_intermediate.setOnClickListener {
            Utils.showToast(this,"Great to have you back!")
            navigateToMainActivity()
        }

        card_beginner.setOnClickListener {
            Utils.showToast(this,"Hello professor!")
            navigateToMainActivity()
        }
    }

    private fun navigateToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
    }


}