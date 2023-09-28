package com.israa.atmodrive.splash

import MySharedPreferences
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.israa.atmodrive.R
import com.israa.atmodrive.auth.presentation.MainActivity
import com.israa.atmodrive.home.HomeActivity

import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)


//        Handler(Looper.myLooper()!!).postDelayed({
//            navigate()
//        },5000)

        lifecycleScope.launchWhenStarted {
            delay(5000)
            navigate()
        }

    }

    private fun navigate() {
        if(MySharedPreferences.getIsFirstRun()){
            startActivity(Intent(this, WelcomeActivity::class.java))
            finish()
        }
        else{

            if(MySharedPreferences.getUser().rememberToken == ""){
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }else{
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }
        }
    }


}