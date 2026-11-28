package com.alimapps.senbombardir.ui.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alimapps.senbombardir.data.repository.BillingRepository
import com.alimapps.senbombardir.domain.model.BillingType
import com.alimapps.senbombardir.ui.model.types.SettingsItemType
import com.alimapps.senbombardir.ui.utils.RemoteConfig
import com.alimapps.senbombardir.ui.utils.debounceEffect
import com.alimapps.senbombardir.utils.analytics.Analytics
import com.alimapps.senbombardir.utils.analytics.AnalyticsEvent
import com.alimapps.senbombardir.utils.analytics.AnalyticsParam
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val billingRepository: BillingRepository,
    private val analytics: Analytics,
) : ViewModel() {

    private val _effect = MutableSharedFlow<SettingsEffect>()
    val effect: Flow<SettingsEffect> get() = _effect.debounceEffect()

    private val _isPremium = MutableStateFlow(hasFullVersion())
    val isPremium: StateFlow<Boolean> = _isPremium.asStateFlow()

    fun action(action: SettingsAction) {
        when (action) {
            is SettingsAction.OnSettingsItemClicked -> onSettingsItemClicked(action.item)
            is SettingsAction.OnActivationCodeSubmitted -> onActivationCodeSubmitted(action.code)
            is SettingsAction.OnActivationCodeDialogOpened -> analytics.track(AnalyticsEvent.ACTIVATION_CODE_DIALOG_OPENED)
            is SettingsAction.OnScreenResumed -> refreshBillingState()
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
                trackActivationCode(result = "deactivated", secretActivated = false)
            } else {
                billingRepository.setCurrentBillingType(BillingType.Lifetime)
                billingRepository.setSecretActivated(true)
                trackActivationCode(result = "activated", secretActivated = true)
            }
            refreshBillingState()
            setEffectSafely(SettingsEffect.ActivationSuccess)
        } else {
            analytics.track(AnalyticsEvent.ACTIVATION_CODE_SUBMITTED, AnalyticsParam.RESULT to "wrong_code")
            setEffectSafely(SettingsEffect.ActivationError)
        }
    }

    private fun trackActivationCode(result: String, secretActivated: Boolean) {
        analytics.track(AnalyticsEvent.ACTIVATION_CODE_SUBMITTED, AnalyticsParam.RESULT to result)
        analytics.setSecretActivated(secretActivated)
    }

    private fun refreshBillingState() {
        _isPremium.value = hasFullVersion()
    }

    private fun hasFullVersion(): Boolean =
        billingRepository.getCurrentBillingType() != BillingType.Limited

    private fun setEffectSafely(effect: SettingsEffect) {
        viewModelScope.launch {
            _effect.emit(effect)
        }
    }
}