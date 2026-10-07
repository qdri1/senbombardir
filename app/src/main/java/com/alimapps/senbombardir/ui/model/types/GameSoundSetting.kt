package com.alimapps.senbombardir.ui.model.types

import com.alimapps.senbombardir.R

/**
 * Automatic sounds and voice-overs that GameScreen plays on its own
 * (manual sound buttons in SoundsBlock are not affected).
 */
enum class GameSoundSetting(val titleRes: Int, val descriptionRes: Int) {
    StartMatch(
        titleRes = R.string.sound_setting_start_match,
        descriptionRes = R.string.sound_setting_start_match_description,
    ),
    FinishMatch(
        titleRes = R.string.sound_setting_finish_match,
        descriptionRes = R.string.sound_setting_finish_match_description,
    ),
    OneMinuteLeft(
        titleRes = R.string.sound_setting_one_minute_left,
        descriptionRes = R.string.sound_setting_one_minute_left_description,
    ),
    TenSecondsLeft(
        titleRes = R.string.sound_setting_ten_seconds_left,
        descriptionRes = R.string.sound_setting_ten_seconds_left_description,
    ),
    ActionVoice(
        titleRes = R.string.sound_setting_action_voice,
        descriptionRes = R.string.sound_setting_action_voice_description,
    ),
    ActionSounds(
        titleRes = R.string.sound_setting_action_sounds,
        descriptionRes = R.string.sound_setting_action_sounds_description,
    ),
}
