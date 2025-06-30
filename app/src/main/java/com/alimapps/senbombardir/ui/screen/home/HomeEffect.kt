package com.alimapps.senbombardir.ui.screen.home

import com.alimapps.senbombardir.ui.utils.DebounceEffect

sealed interface HomeEffect : DebounceEffect {
    data object OpenAddGameScreen : HomeEffect
    class OpenGameScreen(val gameId: Long) : HomeEffect
}