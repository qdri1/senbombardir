package com.alimapps.senbombardir.ui.screen.activation

import com.alimapps.senbombardir.domain.model.ActivationPlan

sealed interface ActivationAction {
    data object OnBackClicked : ActivationAction
    data object ShowPriceButtonClicked : ActivationAction
    data object BuyButtonClicked : ActivationAction
    data object ManageSubscriptionsButtonClicked : ActivationAction
    class OnActivationPlanItemClicked(val plan: ActivationPlan) : ActivationAction
}