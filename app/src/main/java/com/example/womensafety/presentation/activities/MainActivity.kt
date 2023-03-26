package com.example.womensafety.presentation.activities

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.commit
import com.example.womensafety.R
import com.example.womensafety.presentation.fragments.StartingScreenFragment
import com.example.womensafety.helpers.Constants
import com.example.womensafety.helpers.PermissionHelper
import com.example.womensafety.presentation.services.LocationService
import com.example.womensafety.utils.HelpRepo
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/* TO-DO
Implement code to redirect to app settings for granting location permission
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity(), PermissionHelper.IPermission {
    private var permissionHelper: PermissionHelper? = null
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    @Inject
    lateinit var repo: HelpRepo
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        permissionHelper = PermissionHelper(this, this)
        if(repo.getSharedPreferences(Constants.IS_LOGGED_IN)!="") {
            addFragment(Constants.HOME_FRAGMENT)
        }else {
            addFragment(Constants.STARTING_SCREEN)
        }
        startLocationService()
        checkForPermission()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun checkForPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermission()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startLocationService() {
        val serviceIntent = Intent(this, LocationService::class.java)
        startForegroundService(serviceIntent)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun requestPermission() {
        requestPermissions(
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ), Constants.LOCATION_CODE
        )
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            Constants.LOCATION_CODE -> {
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED && grantResults[1]==PackageManager.PERMISSION_GRANTED) {
                    showToast("Permission granted", Toast.LENGTH_SHORT)
                }else if(grantResults[0]!=PackageManager.PERMISSION_GRANTED && grantResults[1]!=PackageManager.PERMISSION_GRANTED) {
                    showToast("Permission denied", Toast.LENGTH_SHORT)
                }else {
                    requestPermission()
                }
            }
        }
    }

    private fun showToast(str: String, length: Int) {
        Toast.makeText(this, str, length).show()
    }

    private fun addFragment(id: String) {
        supportFragmentManager.commit {
            add(R.id.container, Constants.getFragment(id), null)
            setReorderingAllowed(true)
            addToBackStack(id)
        }
    }

    override fun onPermissionGranted(requestCode: Int, permission: String) {
    }

    override fun onPermissionDenied(requestCode: Int, permission: String) {

    }

    override fun onPermissionDeniedPermanently(requestCode: Int, permission: String) {
    }



}