package com.alimapps.senbombardir.data.source

import android.content.SharedPreferences
import androidx.core.content.edit

class OnboardingStorage(private val preferences: SharedPreferences) {

    fun isCompleted(): Boolean = preferences.getBoolean(KEY_COMPLETED, false)

    fun setCompleted() {
        preferences.edit { putBoolean(KEY_COMPLETED, true) }
    }

    /**
     * Set once the language is picked, so an onboarding left halfway is shown again on the next launch
     * (the language is already saved by then, so it can't be used to detect a fresh install).
     */
    fun isStarted(): Boolean = preferences.getBoolean(KEY_STARTED, false)

    fun setStarted() {
        preferences.edit { putBoolean(KEY_STARTED, true) }
    }

    private companion object {
        const val KEY_COMPLETED = "onboarding_completed"
        const val KEY_STARTED = "onboarding_started"
    }
}
