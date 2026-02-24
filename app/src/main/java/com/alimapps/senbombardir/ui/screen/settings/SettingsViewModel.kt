package com.alimapps.senbombardir.ui.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alimapps.senbombardir.data.repository.BillingRepository
import com.alimapps.senbombardir.domain.model.BillingType
import com.alimapps.senbombardir.ui.model.types.SettingsItemType
import com.alimapps.senbombardir.ui.utils.RemoteConfig
import com.alimapps.senbombardir.ui.utils.debounceEffect
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val billingRepository: BillingRepository,
) : ViewModel() {

    private val _effect = MutableSharedFlow<SettingsEffect>()
    val effect: Flow<SettingsEffect> get() = _effect.debounceEffect()

    fun action(action: SettingsAction) {
        when (action) {
            is SettingsAction.OnSettingsItemClicked -> onSettingsItemClicked(action.item)
            is SettingsAction.OnActivationCodeSubmitted -> onActivationCodeSubmitted(action.code)
        }
    }

    private fun onSettingsItemClicked(item: SettingsItemType) {
        when (item) {
            SettingsItemType.Language -> setEffectSafely(SettingsEffect.ShowSelectLanguage)
            SettingsItemType.Share -> setEffectSafely(SettingsEffect.Share)
            SettingsItemType.Evaluate -> setEffectSafely(SettingsEffect.OpenPlayMarket)
            SettingsItemType.Activation -> setEffectSafely(SettingsEffect.OpenActivationScreen)
        }
    }

    private fun onActivationCodeSubmitted(code: String) {
        if (code.trim() == RemoteConfig.activationCode) {
            if (billingRepository.getCurrentBillingType() == BillingType.Lifetime && billingRepository.isSecretActivated()) {
                billingRepository.setCurrentBillingType(BillingType.Limited)
                billingRepository.setSecretActivated(false)
            } else {
                billingRepository.setCurrentBillingType(BillingType.Lifetime)
                billingRepository.setSecretActivated(true)
            }
            setEffectSafely(SettingsEffect.ActivationSuccess)
        } else {
            setEffectSafely(SettingsEffect.ActivationError)
        }
    }

    private fun setEffectSafely(effect: SettingsEffect) {
        viewModelScope.launch {
            _effect.emit(effect)
        }
    }
}