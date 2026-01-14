package com.alimapps.senbombardir.ui.screen.activation

import com.alimapps.senbombardir.domain.model.ActivationPlan
import com.alimapps.senbombardir.domain.model.BillingType

data class ActivationUiState(
    val billingType: BillingType = BillingType.Limited,
    val pageIndex: Int = 0,
    val selectedPlan: ActivationPlan? = null,
    val monthlyPrice: String = "990",
    val yearlyPrice: String = "9 990",
    val unlimitedPrice: String = "19 990",
)