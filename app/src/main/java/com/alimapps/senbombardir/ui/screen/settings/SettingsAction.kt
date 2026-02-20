package com.alimapps.senbombardir.ui.screen.settings

import com.alimapps.senbombardir.ui.model.types.SettingsItemType

sealed interface SettingsAction {
    class OnSettingsItemClicked(val item: SettingsItemType) : SettingsAction
    class OnActivationCodeSubmitted(val code: String) : SettingsAction
}