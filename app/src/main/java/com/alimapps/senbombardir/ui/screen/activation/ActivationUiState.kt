package com.alimapps.senbombardir.ui.screen.activation

import com.alimapps.senbombardir.domain.model.ActivationPlan
import com.alimapps.senbombardir.domain.model.BillingType

data class ActivationUiState(
    val billingType: BillingType = BillingType.Limited,
    val selectedPlan: ActivationPlan = ActivationPlan.Yearly,
    val monthlyPrice: String? = null,
    val yearlyPrice: String? = null,
    val unlimitedPrice: String? = null,
    val onedayPrice: String? = null,
    val onedayExpirationDate: Long? = null,
)