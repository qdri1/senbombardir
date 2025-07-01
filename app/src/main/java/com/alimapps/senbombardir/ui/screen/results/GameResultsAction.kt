package com.alimapps.senbombardir.ui.screen.results

sealed interface GameResultsAction {
    data object OnBackClicked : GameResultsAction
    data object OnClearResultsClicked : GameResultsAction
    data object OnClearResultsConfirmationClicked : GameResultsAction
}