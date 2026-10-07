package com.alimapps.senbombardir.ui.screen.soundsettings

import com.alimapps.senbombardir.ui.model.types.GameSoundSetting

sealed interface SoundSettingsAction {
    data object OnBackClicked : SoundSettingsAction
    class OnAllToggled(val enabled: Boolean) : SoundSettingsAction
    class OnSettingToggled(val setting: GameSoundSetting, val enabled: Boolean) : SoundSettingsAction
}
