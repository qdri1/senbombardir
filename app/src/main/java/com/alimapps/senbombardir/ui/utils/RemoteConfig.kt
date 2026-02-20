package com.alimapps.senbombardir.ui.utils

import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings

object RemoteConfig {

    fun configureAndActivate() {
        val configSettings = remoteConfigSettings { minimumFetchIntervalInSeconds = 300 }
        Firebase.remoteConfig.setConfigSettingsAsync(configSettings)
        Firebase.remoteConfig.fetchAndActivate()
    }

    val activationCode: String get() = Firebase.remoteConfig.getString("activation_code").ifEmpty { "Dop-Tep" }

    val isAppUpdateRequired: Boolean get() = Firebase.remoteConfig.getBoolean("app_update_required")

    val appVersionCode: Int get() = Firebase.remoteConfig.getString("app_version_code").toIntOrNull() ?: -1

    val telegramUrl: String get() = Firebase.remoteConfig.getString("telegram_url")

    val whatsappUrl: String get() = Firebase.remoteConfig.getString("whatsapp_url")
}