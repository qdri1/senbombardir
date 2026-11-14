package com.alimapps.senbombardir.ui.screen.add.game

import com.alimapps.senbombardir.ui.utils.DebounceEffect

sealed interface AddGameEffect : DebounceEffect {
    data object ShowColorsBottomSheet : AddGameEffect
    class ShowSnackbar(val stringRes: Int) : AddGameEffect
    data object CloseScreen : AddGameEffect
    data object CloseScreenWithResult : AddGameEffect
}