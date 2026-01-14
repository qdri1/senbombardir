package com.alimapps.senbombardir.ui.screen.activation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alimapps.senbombardir.R
import com.alimapps.senbombardir.data.repository.BillingRepository
import com.alimapps.senbombardir.domain.model.ActivationPlan
import com.alimapps.senbombardir.domain.model.BillingType
import com.alimapps.senbombardir.ui.utils.debounceEffect
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ActivationViewModel(
    private val billingRepository: BillingRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ActivationUiState())
    val uiState: StateFlow<ActivationUiState> = _uiState

    private val _effect = MutableSharedFlow<ActivationEffect>()
    val effect: Flow<ActivationEffect> get() = _effect.debounceEffect()

    init {
        fetchActivation()
    }

    fun action(action: ActivationAction) {
        when (action) {
            is ActivationAction.OnBackClicked -> onBackClicked()
            is ActivationAction.ShowPriceButtonClicked -> onShowPriceButtonClicked()
            is ActivationAction.BuyButtonClicked -> onBuyButtonClicked()
            is ActivationAction.ManageSubscriptionsButtonClicked -> setEffectSafely(ActivationEffect.OpenGooglePlaySubscriptions)
            is ActivationAction.OnActivationPlanItemClicked -> onActivationPlanItemClicked(action.plan)
        }
    }

    private fun fetchActivation() = viewModelScope.launch {
        val billingType = billingRepository.getCurrentBillingType()
        setState(
            uiState.value.copy(
                billingType = billingType,
                monthlyPrice = billingRepository.getMonthlyPrice(),
                yearlyPrice = billingRepository.getYearlyPrice(),
                unlimitedPrice = billingRepository.getUnlimitedPrice(),
            )
        )
    }

    private fun onBackClicked() {
        if (uiState.value.billingType == BillingType.Limited) {
            if (uiState.value.pageIndex > 0) {
                setState(uiState.value.copy(pageIndex = 0))
            } else {
                setEffectSafely(ActivationEffect.CloseScreen)
            }
        } else {
            setEffectSafely(ActivationEffect.CloseScreen)
        }
    }

    private fun onShowPriceButtonClicked() {
        setState(uiState.value.copy(pageIndex = 1))
    }

    private fun onBuyButtonClicked() {
        val selectedPlan = uiState.value.selectedPlan
        if (selectedPlan == null) {
            setEffectSafely(ActivationEffect.ShowSnackbar(R.string.activation_plan_select_error_text))
        } else {
            setEffectSafely(ActivationEffect.BuySelectedPlan(selectedPlan))
        }
    }

    private fun onActivationPlanItemClicked(plan: ActivationPlan) {
        setState(uiState.value.copy(selectedPlan = plan))
    }

    private fun setState(state: ActivationUiState) {
        _uiState.update { state }
    }

    private fun setEffectSafely(effect: ActivationEffect) {
        viewModelScope.launch {
            _effect.emit(effect)
        }
    }
}