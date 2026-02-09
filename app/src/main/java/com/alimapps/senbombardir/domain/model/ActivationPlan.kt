package com.alimapps.senbombardir.domain.model

enum class ActivationPlan(val productId: String) {
    Monthly("monthly_premium_upgrade"),
    Yearly("yearly_premium_upgrade"),
    Unlimited("premium_upgrade"),
}