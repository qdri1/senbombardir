package com.alimapps.senbombardir.ui.screen.settings

import com.alimapps.senbombardir.ui.utils.DebounceEffect

sealed interface SettingsEffect : DebounceEffect {
    data object ShowSelectLanguage : SettingsEffect
    data object Share : SettingsEffect
    data object OpenPlayMarket : SettingsEffect
    data object OpenActivationScreen : SettingsEffect
    data object ActivationSuccess : SettingsEffect
    data object ActivationError : SettingsEffect
}