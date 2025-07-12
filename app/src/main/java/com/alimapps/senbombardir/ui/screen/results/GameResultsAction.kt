package com.alimapps.senbombardir.ui.screen.results

import com.alimapps.senbombardir.ui.model.PlayerResultUiModel

sealed interface GameResultsAction {
    data object OnBackClicked : GameResultsAction
    data object OnClearResultsClicked : GameResultsAction
    data object OnClearResultsConfirmationClicked : GameResultsAction
    class OnSavePlayerResultClicked(
        val playerResultUiModel: PlayerResultUiModel,
        val playerResultValue: Int,
    ) : GameResultsAction
}