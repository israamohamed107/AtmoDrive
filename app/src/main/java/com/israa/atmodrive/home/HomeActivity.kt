package com.israa.atmodrive.home

import MySharedPreferences
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.button.MaterialButton
import com.israa.atmodrive.R
import com.israa.atmodrive.auth.presentation.MainActivity
import com.israa.atmodrive.databinding.ActivityHomeBinding
import java.util.Locale

class HomeActivity : AppCompatActivity() {

    lateinit var binding:ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogout.setOnClickListener { // design in home fragment not in aome activity
            MySharedPreferences.deleteCurrentUser()
            MySharedPreferences.setIsFirstRun(false)
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

    }

    private fun getAddress(latLng: LatLng) {
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses: List<Address>?
        val address: Address?
        var addressText = ""

        addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)

        addresses?.let {
            if (addresses.isNotEmpty()) {
                address = addresses[0]
                addressText = address.getAddressLine(0)
            } else{
                addressText = "its not appear"
            }
        }
        binding.txtPickup.text = addressText
    }

}