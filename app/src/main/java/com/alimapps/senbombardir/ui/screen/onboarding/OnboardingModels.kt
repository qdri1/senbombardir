package com.alimapps.senbombardir.ui.screen.onboarding

import com.alimapps.senbombardir.domain.model.AppLanguage
import com.alimapps.senbombardir.ui.utils.DebounceEffect

enum class OnboardingStep {
    Language,
    Welcome,
    Rotation,
    Stats,
    Sounds,
    Notifications,
}

data class OnboardingUiState(
    val steps: List<OnboardingStep> = OnboardingStep.entries,
    val stepIndex: Int = 0,
    val selectedLanguage: AppLanguage? = null,
    val isVoicePlaying: Boolean = false,
) {
    val step: OnboardingStep get() = steps[stepIndex]
    val progress: Float get() = (stepIndex + 1f) / steps.size
    val isLastStep: Boolean get() = stepIndex == steps.lastIndex
    val canContinue: Boolean get() = step != OnboardingStep.Language || selectedLanguage != null
}

sealed interface OnboardingAction {
    data object OnContinueClicked : OnboardingAction
    data object OnBackClicked : OnboardingAction
    class OnLanguageSelected(val language: AppLanguage) : OnboardingAction
    data object OnVoiceDemoClicked : OnboardingAction
    /** The system permission dialog was answered (either way) or the user tapped "Not now". */
    data object OnNotificationsAnswered : OnboardingAction
}

sealed interface OnboardingEffect : DebounceEffect {
    data object Finish : OnboardingEffect
}
