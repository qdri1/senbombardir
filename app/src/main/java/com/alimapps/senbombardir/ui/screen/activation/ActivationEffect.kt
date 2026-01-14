package com.alimapps.senbombardir.ui.screen.activation

import com.alimapps.senbombardir.domain.model.ActivationPlan
import com.alimapps.senbombardir.ui.utils.DebounceEffect

sealed interface ActivationEffect : DebounceEffect {
    data object CloseScreen : ActivationEffect
    data object OpenGooglePlaySubscriptions : ActivationEffect
    class ShowSnackbar(val stringRes: Int) : ActivationEffect
    class BuySelectedPlan(val plan: ActivationPlan) : ActivationEffect
}