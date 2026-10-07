package com.alimapps.senbombardir.data.repository

import com.alimapps.senbombardir.data.source.SoundSettingsStorage
import com.alimapps.senbombardir.ui.model.types.GameSoundSetting

class SoundSettingsRepository(
    private val storage: SoundSettingsStorage,
) {

    fun isEnabled(setting: GameSoundSetting): Boolean = storage.isEnabled(setting)

    fun getAll(): Map<GameSoundSetting, Boolean> =
        GameSoundSetting.entries.associateWith { storage.isEnabled(it) }

    fun setEnabled(setting: GameSoundSetting, enabled: Boolean) {
        storage.setEnabled(setting, enabled)
    }

    fun setAllEnabled(enabled: Boolean) {
        GameSoundSetting.entries.forEach { storage.setEnabled(it, enabled) }
    }
}
