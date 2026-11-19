package com.alimapps.senbombardir.data.source

import android.content.SharedPreferences
import androidx.core.content.edit

class NotificationPermissionStorage(private val preferences: SharedPreferences) {

    fun isPermissionRequested(): Boolean = preferences.getBoolean(KEY_PERMISSION_REQUESTED, false)

    fun setPermissionRequested() {
        preferences.edit { putBoolean(KEY_PERMISSION_REQUESTED, true) }
    }

    private companion object {
        const val KEY_PERMISSION_REQUESTED = "notification_permission_requested"
    }
}
