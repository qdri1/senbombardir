package com.alimapps.senbombardir.ui.screen.settings

import com.alimapps.senbombardir.ui.utils.DebounceEffect

sealed interface SettingsEffect : DebounceEffect {
    data object Share : SettingsEffect
    data object OpenPlayMarket : SettingsEffect
    data object OpenTelegram : SettingsEffect
}