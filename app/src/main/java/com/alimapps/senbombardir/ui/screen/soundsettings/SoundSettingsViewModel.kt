package com.alimapps.senbombardir.ui.screen.soundsettings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alimapps.senbombardir.data.repository.SoundSettingsRepository
import com.alimapps.senbombardir.ui.model.types.GameSoundSetting
import com.alimapps.senbombardir.ui.utils.debounceEffect
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SoundSettingsViewModel(
    private val soundSettingsRepository: SoundSettingsRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SoundSettingsUiState(settings = soundSettingsRepository.getAll()))
    val uiState: StateFlow<SoundSettingsUiState> = _uiState

    private val _effect = MutableSharedFlow<SoundSettingsEffect>()
    val effect: Flow<SoundSettingsEffect> get() = _effect.debounceEffect()

    fun action(action: SoundSettingsAction) {
        when (action) {
            is SoundSettingsAction.OnBackClicked -> setEffectSafely(SoundSettingsEffect.CloseScreen)
            is SoundSettingsAction.OnAllToggled -> onAllToggled(action.enabled)
            is SoundSettingsAction.OnSettingToggled -> onSettingToggled(action.setting, action.enabled)
        }
    }

    private fun onAllToggled(enabled: Boolean) {
        soundSettingsRepository.setAllEnabled(enabled)
        _uiState.update { it.copy(settings = soundSettingsRepository.getAll()) }
    }

    private fun onSettingToggled(setting: GameSoundSetting, enabled: Boolean) {
        soundSettingsRepository.setEnabled(setting, enabled)
        _uiState.update { it.copy(settings = it.settings + (setting to enabled)) }
    }

    private fun setEffectSafely(effect: SoundSettingsEffect) {
        viewModelScope.launch {
            _effect.emit(effect)
        }
    }
}
