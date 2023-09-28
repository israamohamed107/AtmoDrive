package com.israa.atmodrive.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.button.MaterialButton
import com.israa.atmodrive.R
import com.israa.atmodrive.auth.presentation.MainActivity
import dagger.hilt.EntryPoint
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)


        findViewById<MaterialButton>(R.id.btn_get_started).setOnClickListener {
            MySharedPreferences.setIsFirstRun(false)
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}