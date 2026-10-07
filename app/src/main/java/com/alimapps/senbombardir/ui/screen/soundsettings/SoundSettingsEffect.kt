package com.alimapps.senbombardir.ui.screen.soundsettings

import com.alimapps.senbombardir.ui.utils.DebounceEffect

sealed interface SoundSettingsEffect : DebounceEffect {
    data object CloseScreen : SoundSettingsEffect
}
