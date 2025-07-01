package com.alimapps.senbombardir.ui.screen.results

import com.alimapps.senbombardir.ui.utils.DebounceEffect

sealed interface GameResultsEffect : DebounceEffect {
    data object CloseScreen : GameResultsEffect
    data object ShowClearResultsConfirmationBottomSheet : GameResultsEffect
}