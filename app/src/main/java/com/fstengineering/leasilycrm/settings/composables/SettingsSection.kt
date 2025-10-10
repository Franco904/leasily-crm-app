package com.fstengineering.leasilycrm.settings.composables

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.models.Settings

@Composable
fun SettingsSection(
    settings: Settings,
    onIsTodayCheckInCheckOutNotificationEnabledChange: (Boolean) -> Unit,
    onNotificationPermissionDenied: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val activity = (LocalActivity.current ?: context) as Activity
    val lifecycleOwner = LocalLifecycleOwner.current

    val isAndroid13OrHigher = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
    val hasNotificationPermission = isAndroid13OrHigher && ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.POST_NOTIFICATIONS
    ) == PackageManager.PERMISSION_GRANTED

    var hasOpenSettingsToAllowPermission by remember { mutableStateOf(false) }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { isPermissionGranted ->
        if (!isPermissionGranted) {
            onNotificationPermissionDenied()
            return@rememberLauncherForActivityResult
        }

        onIsTodayCheckInCheckOutNotificationEnabledChange(true)
    }

    fun openAppNotificationSettings() {
        val intent = Intent().apply {
            action = android.provider.Settings.ACTION_APP_NOTIFICATION_SETTINGS
            putExtra(android.provider.Settings.EXTRA_APP_PACKAGE, context.packageName)
        }

        context.startActivity(intent)
    }

    fun onSettingChange() {
        if (!settings.isTodayCheckInCheckOutNotificationEnabled && isAndroid13OrHigher && !hasNotificationPermission) {
            val isPreviousDeniedByUser =
                !activity.shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)

            if (isPreviousDeniedByUser) {
                openAppNotificationSettings()
                hasOpenSettingsToAllowPermission = true
                return
            }

            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            onIsTodayCheckInCheckOutNotificationEnabledChange(
                !settings.isTodayCheckInCheckOutNotificationEnabled,
            )
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            val mustReloadNotificationSettings =
                isAndroid13OrHigher && hasNotificationPermission && hasOpenSettingsToAllowPermission

            if (event == Lifecycle.Event.ON_START && mustReloadNotificationSettings) {
                onSettingChange()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Column(
        modifier = modifier
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.settings_notifications_group_title),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))
        TodayCheckInCheckOutNotificationCard(
            isTodayCheckInCheckOutNotificationEnabled = settings.isTodayCheckInCheckOutNotificationEnabled,
            onIsTodayCheckInCheckOutNotificationEnabledChange = { onSettingChange() },
        )
        Spacer(modifier = Modifier.height(120.dp))
    }
}
