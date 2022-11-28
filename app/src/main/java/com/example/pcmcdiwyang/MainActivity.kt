package com.example.pcmcdiwyang

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.pcmcdiwyang.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

/*    @RequiresApi(Build.VERSION_CODES.M)
    private val locationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            with(binding.root) {
                when {
                    granted -> Log.e("Log", "Permission granted!")
                    shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION) -> {
                        //this option is available starting in API 23
                        //snackBar("Permission denied, show more info!")
                        Toast.makeText(this@MainActivity, "Permission denied!", Toast.LENGTH_LONG).show()
                    }
                    else -> Toast.makeText(this@MainActivity, "Permission denied!", Toast.LENGTH_LONG).show()
                }
            }
        }*/
    //Toast.makeText(this@MainActivity, "Permission granted!", Toast.LENGTH_LONG).show()
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_list, R.id.navigation_register, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
       // cameraPermission.launch(Manifest.permission.CAMERA)
      //  locationPermission.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
    }
}