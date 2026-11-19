package com.alimapps.senbombardir.ui.utils

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import com.alimapps.senbombardir.data.source.NotificationPermissionStorage

/**
 * On Android 13+ returns false until POST_NOTIFICATIONS is granted.
 * On older versions reflects the system "notifications enabled" switch for the app.
 */
fun Context.areNotificationsEnabled(): Boolean =
    NotificationManagerCompat.from(this).areNotificationsEnabled()

/**
 * Shows the system permission dialog the first time (Android 13+),
 * otherwise opens the app notification settings — same flow as on iOS.
 */
fun Context.requestNotificationPermissionOrOpenSettings(
    storage: NotificationPermissionStorage,
    launcher: ManagedActivityResultLauncher<String, Boolean>,
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !storage.isPermissionRequested()) {
        storage.setPermissionRequested()
        launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
    } else {
        openNotificationSettings()
    }
}

fun Context.openNotificationSettings() {
    val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
            .putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
    } else {
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, "package:$packageName".toUri())
    }
    startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
}
