package com.example.mymeme.other

import android.Manifest
import android.content.Context
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.mymeme.other.Constants.PERMISSION_STORAGE_REQUEST_CODE
import com.vmadalin.easypermissions.EasyPermissions

private const val TAG = "Permission"

object Permissions {

    fun hasExternalStoragePermission(context: Context): Boolean {
        Log.d(TAG, "hasExternalStoragePermission: called")
        return EasyPermissions.hasPermissions(
            context,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

    fun requestExternalStoragePermission(fragment: Fragment) {
        Log.d(TAG, "requestExternalStoragePermission: called")
        EasyPermissions.requestPermissions(
            fragment,
            "You can't download meme without storage permission",
            PERMISSION_STORAGE_REQUEST_CODE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }
}