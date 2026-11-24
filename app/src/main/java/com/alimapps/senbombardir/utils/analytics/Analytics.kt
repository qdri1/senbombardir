package com.alimapps.senbombardir.utils.analytics

import android.app.Application
import com.alimapps.senbombardir.domain.model.ActivationPlan
import com.alimapps.senbombardir.domain.model.BillingType
import com.amplitude.android.Amplitude
import com.amplitude.android.autocaptureOptions
import com.amplitude.core.events.Identify

// Client-side key: it ships inside the app anyway. The Secret Key must never be put in the app.
private const val AMPLITUDE_API_KEY = "e02088d75933e61d44da927f590b7edb"

/**
 * Product analytics (Amplitude). Event names and properties are the same on iOS and Android —
 * keep them in sync with the iOS `Analytics` when adding new ones.
 * No personal data: never send player names or team names.
 */
class Analytics(application: Application) {

    private val amplitude = Amplitude(AMPLITUDE_API_KEY, application) {
        autocapture = autocaptureOptions {
            +sessions       // session_start / session_end — for retention
            +appLifecycles  // Application Installed / Updated / Opened / Backgrounded
        }
    }

    fun track(event: String, vararg properties: Pair<String, Any?>) {
        val props = properties.filter { it.second != null }.associate { it.first to it.second!! }
        amplitude.track(event, props.ifEmpty { null })
    }

    /** RevenueCat App User ID: lets the RevenueCat → Amplitude integration match purchases to the same user. */
    fun setUserId(userId: String?) {
        if (!userId.isNullOrBlank() && amplitude.getUserId() != userId) {
            amplitude.setUserId(userId)
        }
    }

    fun setBillingType(billingType: BillingType) {
        amplitude.identify(Identify().set(UserProperty.BILLING_TYPE, billingType.analyticsValue))
    }

    fun setLanguage(languageCode: String) {
        amplitude.identify(Identify().set(UserProperty.LANGUAGE, languageCode))
    }

    fun setOnboardingCompleted(completed: Boolean) {
        amplitude.identify(Identify().set(UserProperty.ONBOARDING_COMPLETED, completed))
    }
}

object AnalyticsEvent {
    // Onboarding
    const val ONBOARDING_STARTED = "onboarding_started"
    const val ONBOARDING_STEP_VIEWED = "onboarding_step_viewed"
    const val ONBOARDING_COMPLETED = "onboarding_completed"
    const val LANGUAGE_SELECTED = "language_selected"
    const val LANGUAGE_REQUESTED = "language_requested"
    const val NOTIFICATION_PERMISSION_RESULT = "notification_permission_result"

    // Games
    const val GAME_CREATED = "game_created"
    const val GAME_OPENED = "game_opened"
    const val MATCH_STARTED = "match_started"
    const val MATCH_FINISHED = "match_finished"
    const val GAME_ACTION_RECORDED = "game_action_recorded"
    const val SOUND_PLAYED = "sound_played"
    const val FUNCTION_OPENED = "function_opened"
    const val RESULTS_VIEWED = "results_viewed"

    // Monetization
    const val LIMIT_REACHED = "limit_reached"
    const val PAYWALL_VIEWED = "paywall_viewed"
    const val PLAN_SELECTED = "plan_selected"
    const val PURCHASE_STARTED = "purchase_started"
    const val PURCHASE_COMPLETED = "purchase_completed"
    const val PURCHASE_CANCELLED = "purchase_cancelled"
    const val PURCHASE_FAILED = "purchase_failed"
    const val PURCHASE_PENDING = "purchase_pending"
    const val RESTORE_PURCHASES_TAPPED = "restore_purchases_tapped"
    const val MANAGE_SUBSCRIPTION_TAPPED = "manage_subscription_tapped"
}

object AnalyticsParam {
    const val STEP = "step"
    const val INDEX = "index"
    const val LANGUAGE = "language"
    const val SOURCE = "source"
    const val GRANTED = "granted"
    const val TEAM_QUANTITY = "team_quantity"
    const val GAME_FORMAT = "game_format"
    const val MATCH_MINUTES = "match_minutes"
    const val RULE = "rule"
    const val PITCH_SURFACE = "pitch_surface"
    const val MATCHES_PLAYED = "matches_played"
    const val MATCH_NUMBER = "match_number"
    const val RESULT = "result"
    const val DURATION_SEC = "duration_sec"
    const val TYPE = "type"
    const val SOUND = "sound"
    const val FUNCTION = "function"
    const val LOCKED = "locked"
    /** match_started / match_finished: the free limit of this game is used up and the user has no full version. */
    const val LIMITED = "limited"
    const val PLAN = "plan"
    const val REASON = "reason"
}

/** Values of the `source` param of `paywall_viewed`, `function_opened`, `language_selected`. */
object AnalyticsSource {
    const val ONBOARDING = "onboarding"
    const val SETTINGS = "settings"
    const val LIMIT = "limit"
    const val GAME = "game"
    const val RESULTS = "results"
}

private object UserProperty {
    const val BILLING_TYPE = "billing_type"
    const val LANGUAGE = "language"
    const val ONBOARDING_COMPLETED = "onboarding_completed"
}

val BillingType.analyticsValue: String
    get() = when (this) {
        BillingType.Limited -> "free"
        BillingType.Subscribe -> "subscribe"
        BillingType.Lifetime -> "lifetime"
        BillingType.OneDay -> "one_day"
    }

val ActivationPlan.analyticsValue: String
    get() = when (this) {
        ActivationPlan.Monthly -> "monthly"
        ActivationPlan.Yearly -> "yearly"
        ActivationPlan.Unlimited -> "lifetime"
        ActivationPlan.OneDay -> "one_day"
    }

/** "PitchSurface.ArtificialGrass" → "artificial_grass" — stable, language-independent values for enums. */
val Enum<*>.analyticsValue: String
    get() = name.replace(Regex("([a-z0-9])([A-Z])"), "$1_$2").lowercase()
