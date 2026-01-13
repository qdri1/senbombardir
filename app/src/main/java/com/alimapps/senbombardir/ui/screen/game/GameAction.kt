package com.alimapps.senbombardir.ui.screen.game

import com.alimapps.senbombardir.ui.model.LiveGameResultUiModel
import com.alimapps.senbombardir.ui.model.PlayerResultUiModel
import com.alimapps.senbombardir.ui.model.PlayerUiModel
import com.alimapps.senbombardir.ui.model.types.GameFunction
import com.alimapps.senbombardir.ui.model.types.GameSounds
import com.alimapps.senbombardir.ui.model.types.TeamOption

sealed interface GameAction {
    data object OnBackClicked : GameAction
    data object OnGoBackConfirmationClicked : GameAction
    data object OnDeleteGameConfirmationClicked : GameAction
    data object OnClearResultsConfirmationClicked : GameAction
    data object OnStartFinishButtonClicked : GameAction
    data object OnFinishGameConfirmationClicked : GameAction
    data object OnTimerClicked : GameAction
    data object OnLeftTeamClicked : GameAction
    data object OnRightTeamClicked : GameAction
    class OnLeftTeamOptionSelected(val option: TeamOption?) : GameAction
    class OnRightTeamOptionSelected(val option: TeamOption?) : GameAction

    data object OnTeamChangeIconClicked : GameAction
    class OnLeftTeamChangeClicked(val teamId: Long?) : GameAction
    class OnRightTeamChangeClicked(val teamId: Long?) : GameAction

    class OnOptionPlayersSelected(
        val teamId: Long,
        val playerUiModel: PlayerUiModel,
        val option: TeamOption,
    ) : GameAction

    class OnOptionPlayersAutoGoalSelected(
        val teamId: Long,
    ) : GameAction

    data object OnStayTeamSelectionBottomSheetDismissed : GameAction
    data object OnLeftTeamStayClicked : GameAction
    data object OnRightTeamStayClicked : GameAction

    class OnSoundClicked(val sound: GameSounds) : GameAction
    class OnFunctionClicked(val function: GameFunction) : GameAction
    class OnInterceptionNavigationResult(val result: Any) : GameAction

    class OnPlayerResultClicked(val playerResultUiModel: PlayerResultUiModel) : GameAction
    class OnSavePlayerResultClicked(
        val playerResultUiModel: PlayerResultUiModel,
        val playerResultValue: Int,
    ) : GameAction

    class OnLiveGameResultClicked(val liveGameResultUiModel: LiveGameResultUiModel) : GameAction
    class OnSaveLiveGameResultClicked(
        val liveGameResultUiModel: LiveGameResultUiModel,
        val teamGoalsValue: Int,
    ) : GameAction

    data object OnActivateClicked : GameAction
}