package com.alimapps.senbombardir.data.source

import android.content.SharedPreferences
import androidx.core.content.edit
import com.alimapps.senbombardir.ui.model.types.GameSoundSetting

class SoundSettingsStorage(private val preferences: SharedPreferences) {

    private fun key(setting: GameSoundSetting) = "sound_setting_${setting.name}"

    fun isEnabled(setting: GameSoundSetting): Boolean =
        preferences.getBoolean(key(setting), true)

    fun setEnabled(setting: GameSoundSetting, enabled: Boolean) {
        preferences.edit { putBoolean(key(setting), enabled) }
    }
}
