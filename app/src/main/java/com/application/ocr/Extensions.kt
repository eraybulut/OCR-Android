package com.application.ocr

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.InputStream

/**
 * Created by Eray BULUT on 20.12.2023
 * eraybulutlar@gmail.com
 */


fun Activity.getReadAndWritePermission(): Boolean {
    val permissions = mutableListOf<String>()

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        permissions.add(android.Manifest.permission.READ_MEDIA_IMAGES)
        permissions.add(android.Manifest.permission.CAMERA)
    } else {
        permissions.add(android.Manifest.permission.CAMERA)
        permissions.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        permissions.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    if (permissions.isEmpty()) {
        showToast("Permission not found")
        return false
    }

    val listPermissionsNeeded = permissions.filter {
        ContextCompat.checkSelfPermission(
            this,
            it
        ) != PackageManager.PERMISSION_GRANTED
    }

    if (listPermissionsNeeded.isNotEmpty()) {
        ActivityCompat.requestPermissions(
            this,
            listPermissionsNeeded.toTypedArray(),
            101
        )
        return false
    }
    return true
}

fun Uri.toBitmap(context: Context): Bitmap? {
    return try {
        val inputStream: InputStream? = context.contentResolver.openInputStream(this)
        BitmapFactory.decodeStream(inputStream)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}
