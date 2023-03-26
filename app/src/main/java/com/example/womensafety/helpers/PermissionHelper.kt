package com.example.womensafety.helpers

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PermissionHelper @Inject constructor(private val permissionListener: IPermission? = null, private val activity: Activity?) {

    interface IPermission {
        fun onPermissionGranted(requestCode: Int, permission: String)
        fun onPermissionDenied(requestCode: Int, permission: String)
        fun onPermissionDeniedPermanently(requestCode: Int, permission: String)
    }

    fun isPermissionGranted(permission: String, context: Context) : Boolean {
        if(ActivityCompat.checkSelfPermission(context, permission)==PackageManager.PERMISSION_GRANTED) return true
        return false
    }

    fun processPermissions(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        for(i: Int in (grantResults.indices)) {
            //Permission Granted
            if(grantResults[i]==PackageManager.PERMISSION_GRANTED) {
                permissionListener?.onPermissionGranted(requestCode, permissions[i])
            }
            else if(grantResults[i]==PackageManager.PERMISSION_DENIED && activity?.let {
                    ActivityCompat.shouldShowRequestPermissionRationale(
                        it, permissions[i])
                }==true) {
                permissionListener?.onPermissionDenied(requestCode, permissions[i])
            }
            //Permission Denied Permanently
            else {
                permissionListener?.onPermissionDeniedPermanently(requestCode, permissions[i])
            }
        }
    }
}

