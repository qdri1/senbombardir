package com.alimapps.senbombardir.ui.screen.results

import com.alimapps.senbombardir.ui.model.PlayerResultUiModel
import com.alimapps.senbombardir.ui.model.PlayerUiModel
import com.alimapps.senbombardir.ui.model.TeamUiModel
import com.alimapps.senbombardir.ui.model.types.GameResultsFunction

sealed interface GameResultsAction {
    data object OnBackClicked : GameResultsAction
    data object OnClearResultsConfirmationClicked : GameResultsAction
    class OnSavePlayerResultClicked(
        val playerResultUiModel: PlayerResultUiModel,
        val playerResultValue: Int,
    ) : GameResultsAction

    class OnSaveTeamResultClicked(
        val teamUiModel: TeamUiModel,
        val pointsValue: Int,
    ) : GameResultsAction

    class OnFunctionClicked(val function: GameResultsFunction) : GameResultsAction
    data object OnActivateClicked : GameResultsAction
    class OnRemovePlayerHistoryClicked(val playerUiModel: PlayerUiModel) : GameResultsAction
    class OnRemovePlayerHistoryConfirmationClicked(val playerUiModel: PlayerUiModel) : GameResultsAction
}