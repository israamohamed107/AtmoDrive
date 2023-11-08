package com.israa.atmodrive.home

import MySharedPreferences
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.button.MaterialButton
import com.israa.atmodrive.R
import com.israa.atmodrive.auth.presentation.MainActivity
import com.israa.atmodrive.databinding.ActivityHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private  var _binding:ActivityHomeBinding?=null
     val binding get() = _binding!!



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