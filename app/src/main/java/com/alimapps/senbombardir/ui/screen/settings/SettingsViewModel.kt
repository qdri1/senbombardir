package com.alimapps.senbombardir.ui.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alimapps.senbombardir.ui.model.types.SettingsItemType
import com.alimapps.senbombardir.ui.utils.debounceEffect
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class SettingsViewModel : ViewModel() {

    private val _effect = MutableSharedFlow<SettingsEffect>()
    val effect: Flow<SettingsEffect> get() = _effect.debounceEffect()

    fun action(action: SettingsAction) {
        when (action) {
            is SettingsAction.OnSettingsItemClicked -> onSettingsItemClicked(action.item)
        }
    }

    private fun onSettingsItemClicked(item: SettingsItemType) {
        when (item) {
            SettingsItemType.Language -> setEffectSafely(SettingsEffect.ShowSelectLanguage)
            SettingsItemType.Share -> setEffectSafely(SettingsEffect.Share)
            SettingsItemType.Evaluate -> setEffectSafely(SettingsEffect.OpenPlayMarket)
            SettingsItemType.Telegram -> setEffectSafely(SettingsEffect.OpenTelegram)
        }
    }

    private fun setEffectSafely(effect: SettingsEffect) {
        viewModelScope.launch {
            _effect.emit(effect)
        }
    }
}