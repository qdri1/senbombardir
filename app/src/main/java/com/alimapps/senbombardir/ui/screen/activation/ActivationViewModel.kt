package com.alimapps.senbombardir.ui.screen.activation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alimapps.senbombardir.data.repository.BillingRepository
import com.alimapps.senbombardir.domain.model.ActivationPlan
import com.alimapps.senbombardir.domain.model.BillingType
import com.alimapps.senbombardir.ui.utils.debounceEffect
import com.alimapps.senbombardir.utils.analytics.Analytics
import com.alimapps.senbombardir.utils.analytics.AnalyticsEvent
import com.alimapps.senbombardir.utils.analytics.AnalyticsParam
import com.alimapps.senbombardir.utils.analytics.analyticsValue
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.awaitCustomerInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ActivationViewModel(
    private val source: String,
    private val billingRepository: BillingRepository,
    private val analytics: Analytics,
) : ViewModel() {

    private var isPaywallViewTracked = false

    private val _uiState = MutableStateFlow(ActivationUiState())
    val uiState: StateFlow<ActivationUiState> = _uiState

    private val _effect = MutableSharedFlow<ActivationEffect>()
    val effect: Flow<ActivationEffect> get() = _effect.debounceEffect()

    init {
        viewModelScope.launch {
            billingRepository.billingTypeFlow.collect { fetchActivation() }
        }
        viewModelScope.launch {
            billingRepository.isPurchaseInProgress.collect { inProgress ->
                _uiState.update { it.copy(isPurchasing = inProgress) }
            }
        }
    }

    fun action(action: ActivationAction) {
        when (action) {
            is ActivationAction.OnBackClicked -> onBackClicked()
            is ActivationAction.BuyButtonClicked -> onBuyButtonClicked()
            is ActivationAction.ManageSubscriptionsButtonClicked -> {
                analytics.track(AnalyticsEvent.MANAGE_SUBSCRIPTION_TAPPED)
                setEffectSafely(ActivationEffect.OpenGooglePlaySubscriptions)
            }
            is ActivationAction.RestorePurchasesClicked -> setEffectSafely(ActivationEffect.RestorePurchases)
            is ActivationAction.OnActivationPlanItemClicked -> onActivationPlanItemClicked(action.plan)
        }
    }

    private fun fetchActivation() = viewModelScope.launch {
        val billingType = billingRepository.getCurrentBillingType()
        if (billingType == BillingType.Limited && !isPaywallViewTracked) {
            isPaywallViewTracked = true
            analytics.track(AnalyticsEvent.PAYWALL_VIEWED, AnalyticsParam.SOURCE to source)
        }
        setState(
            uiState.value.copy(
                billingType = billingType,
                monthlyPrice = billingRepository.getMonthlyPrice(),
                yearlyPrice = billingRepository.getYearlyPrice(),
                unlimitedPrice = billingRepository.getUnlimitedPrice(),
                onedayPrice = billingRepository.getOnedayPrice(),
                onedayExpirationDate = if (billingType == BillingType.OneDay) billingRepository.getOnedayExpirationDate() else null,
            )
        )
        if (billingType == BillingType.Subscribe) {
            fetchSubscriptionPlan()
        }
    }

    private fun fetchSubscriptionPlan() = viewModelScope.launch {
        if (!Purchases.isConfigured) return@launch
        val activeSubscriptions = runCatching { Purchases.sharedInstance.awaitCustomerInfo() }
            .getOrNull()
            ?.activeSubscriptions
            ?: return@launch

        fun isActive(plan: ActivationPlan) = activeSubscriptions.any { it == plan.productId || it.startsWith("${plan.productId}:") }

        val plan = when {
            isActive(ActivationPlan.Yearly) -> ActivationPlan.Yearly
            isActive(ActivationPlan.Monthly) -> ActivationPlan.Monthly
            else -> null
        }
        _uiState.update { it.copy(subscriptionPlan = plan) }
    }

    private fun onBackClicked() {
        setEffectSafely(ActivationEffect.CloseScreen)
    }

    private fun onBuyButtonClicked() {
        analytics.track(AnalyticsEvent.PURCHASE_STARTED, AnalyticsParam.PLAN to uiState.value.selectedPlan.analyticsValue)
        setEffectSafely(ActivationEffect.BuySelectedPlan(uiState.value.selectedPlan))
    }

    private fun onActivationPlanItemClicked(plan: ActivationPlan) {
        if (plan != uiState.value.selectedPlan) {
            analytics.track(AnalyticsEvent.PLAN_SELECTED, AnalyticsParam.PLAN to plan.analyticsValue)
        }
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