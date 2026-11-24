package com.alimapps.senbombardir.ui.screen.onboarding

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.alimapps.senbombardir.R
import com.alimapps.senbombardir.data.source.OnboardingStorage
import com.alimapps.senbombardir.domain.model.AppLanguage
import com.alimapps.senbombardir.ui.utils.areNotificationsEnabled
import com.alimapps.senbombardir.ui.utils.debounceEffect
import com.alimapps.senbombardir.utils.analytics.Analytics
import com.alimapps.senbombardir.utils.analytics.AnalyticsEvent
import com.alimapps.senbombardir.utils.analytics.AnalyticsParam
import com.alimapps.senbombardir.utils.analytics.analyticsValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.UUID

class OnboardingViewModel(
    private val onboardingStorage: OnboardingStorage,
    private val analytics: Analytics,
    private val context: Context,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        OnboardingUiState(
            // Below Android 13 notifications are on by default, so the step is only shown when needed.
            steps = OnboardingStep.entries.filter {
                it != OnboardingStep.Notifications || !context.areNotificationsEnabled()
            }
        )
    )
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<OnboardingEffect>()
    val effect: Flow<OnboardingEffect> get() = _effect.debounceEffect()

    private val mediaPlayer: ExoPlayer = ExoPlayer.Builder(context).build()
    private var textToSpeech: TextToSpeech? = null
    private var textToSpeechLanguage: AppLanguage? = null
    private var voiceDemoJob: Job? = null
    private var isFinished = false

    init {
        // "Started" is saved when the language is picked, so a recreated activity doesn't count it twice.
        if (!onboardingStorage.isStarted()) {
            analytics.track(AnalyticsEvent.ONBOARDING_STARTED)
            trackStepViewed()
        }
    }

    private fun trackStepViewed() {
        val state = uiState.value
        analytics.track(
            AnalyticsEvent.ONBOARDING_STEP_VIEWED,
            AnalyticsParam.STEP to state.step.analyticsValue,
            AnalyticsParam.INDEX to state.stepIndex,
        )
    }

    fun action(action: OnboardingAction) {
        when (action) {
            is OnboardingAction.OnContinueClicked -> onContinueClicked()
            is OnboardingAction.OnBackClicked -> onBackClicked()
            is OnboardingAction.OnLanguageSelected -> onLanguageSelected(action.language)
            is OnboardingAction.OnVoiceDemoClicked -> playVoiceDemo()
            is OnboardingAction.OnLanguageRequested -> analytics.track(
                AnalyticsEvent.LANGUAGE_REQUESTED,
                AnalyticsParam.LANGUAGE to action.language,
                AnalyticsParam.SOURCE to "onboarding",
            )
            is OnboardingAction.OnNotificationsAnswered -> {
                analytics.track(AnalyticsEvent.NOTIFICATION_PERMISSION_RESULT, AnalyticsParam.GRANTED to action.granted)
                finish()
            }
        }
    }

    private fun onContinueClicked() {
        val state = uiState.value
        if (!state.canContinue) return
        if (state.isLastStep) {
            finish()
            return
        }
        stopAudio()
        _uiState.update { it.copy(stepIndex = it.stepIndex + 1) }
        trackStepViewed()
    }

    private fun onBackClicked() {
        if (uiState.value.stepIndex == 0) return
        stopAudio()
        _uiState.update { it.copy(stepIndex = it.stepIndex - 1) }
    }

    private fun onLanguageSelected(language: AppLanguage) {
        onboardingStorage.setStarted()
        _uiState.update { it.copy(selectedLanguage = language) }
    }

    private fun finish() {
        if (isFinished) return
        isFinished = true
        stopAudio()
        onboardingStorage.setCompleted()
        analytics.track(AnalyticsEvent.ONBOARDING_COMPLETED)
        analytics.setOnboardingCompleted(true)
        viewModelScope.launch { _effect.emit(OnboardingEffect.Finish) }
    }

    // region Voice demo: "Goal by Alikhan" + celebration sound

    private fun playVoiceDemo() {
        if (uiState.value.isVoicePlaying) return
        _uiState.update { it.copy(isVoicePlaying = true) }

        val text = context.getString(R.string.text_to_speech_goal, context.getString(R.string.onboarding_sample_player_1))
        val language = uiState.value.selectedLanguage ?: AppLanguage.KZ

        val tts = textToSpeech
        if (tts != null && textToSpeechLanguage == language) {
            speak(tts, text)
        } else {
            tts?.shutdown()
            textToSpeechLanguage = language
            var created: TextToSpeech? = null
            created = TextToSpeech(context) { status ->
                val engine = created ?: return@TextToSpeech
                if (status == TextToSpeech.SUCCESS) {
                    applyLanguage(engine, language)
                    speak(engine, text)
                } else {
                    playCelebrationSound()
                }
            }
            textToSpeech = created
        }
    }

    private fun applyLanguage(engine: TextToSpeech, language: AppLanguage) {
        // Same as in the game: Kazakh text is read by the Russian voice.
        val voiceLanguage = if (language == AppLanguage.KZ) AppLanguage.RU else language
        engine.language = Locale.forLanguageTag(voiceLanguage.code)
        engine.voices
            ?.find { it.name.contains(voiceLanguage.code, ignoreCase = true) }
            ?.let { engine.voice = it }
    }

    private fun speak(engine: TextToSpeech, text: String) {
        engine.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {}
            override fun onDone(utteranceId: String?) { playCelebrationSound() }
            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) { playCelebrationSound() }
        })
        engine.speak(text, TextToSpeech.QUEUE_FLUSH, Bundle(), UUID.randomUUID().toString())
    }

    /** Called from the TTS thread, so it hops to the main thread for ExoPlayer and state. */
    private fun playCelebrationSound() {
        voiceDemoJob?.cancel()
        voiceDemoJob = viewModelScope.launch(Dispatchers.Main) {
            if (!uiState.value.isVoicePlaying) return@launch
            val uri = "android.resource://${context.packageName}/${R.raw.suiii_full}"
            mediaPlayer.setMediaItem(MediaItem.fromUri(uri))
            mediaPlayer.prepare()
            mediaPlayer.play()
            delay(2_500)
            _uiState.update { it.copy(isVoicePlaying = false) }
        }
    }

    private fun stopAudio() {
        if (!uiState.value.isVoicePlaying) return
        voiceDemoJob?.cancel()
        textToSpeech?.stop()
        mediaPlayer.stop()
        _uiState.update { it.copy(isVoicePlaying = false) }
    }

    // endregion

    override fun onCleared() {
        textToSpeech?.shutdown()
        mediaPlayer.release()
        super.onCleared()
    }
}
