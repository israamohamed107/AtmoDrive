package com.israa.atmodrive.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.israa.atmodrive.databinding.ActivityHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private  var _binding:ActivityHomeBinding?=null
     private val binding get() = _binding!!



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)



    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}