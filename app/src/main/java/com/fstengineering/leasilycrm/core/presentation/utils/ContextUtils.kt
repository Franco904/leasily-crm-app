package com.fstengineering.leasilycrm.core.presentation.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

fun Context.hasNotificationPermission(): Boolean {
    val isAndroid13OrHigher = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
    if (!isAndroid13OrHigher) return true

    return ContextCompat.checkSelfPermission(
        /* context = */this,
        /* permission = */ Manifest.permission.POST_NOTIFICATIONS
    ) == PackageManager.PERMISSION_GRANTED
}
