package com.alimapps.senbombardir.ui.screen.add.game

import com.alimapps.senbombardir.ui.utils.DebounceEffect

sealed interface AddGameEffect : DebounceEffect {
    data object ShowColorsBottomSheet : AddGameEffect
    class ShowSnackbar(val stringRes: Int) : AddGameEffect
    class OpenGameScreen(val gameId: Long) : AddGameEffect
    data object CloseScreen : AddGameEffect
    data object CloseScreenWithResult : AddGameEffect
}