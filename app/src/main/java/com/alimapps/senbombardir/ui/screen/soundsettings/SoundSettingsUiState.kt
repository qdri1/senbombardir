package com.alimapps.senbombardir.ui.screen.soundsettings

import com.alimapps.senbombardir.ui.model.types.GameSoundSetting

data class SoundSettingsUiState(
    val settings: Map<GameSoundSetting, Boolean> = GameSoundSetting.entries.associateWith { true },
) {
    val isAllEnabled: Boolean get() = settings.values.all { it }
}
