package com.alimapps.senbombardir.ui.screen.game

import com.alimapps.senbombardir.ui.model.BestPlayerUiModel
import com.alimapps.senbombardir.ui.model.LiveGameResultUiModel
import com.alimapps.senbombardir.ui.model.OptionPlayersUiModel
import com.alimapps.senbombardir.ui.model.PlayerResultUiModel
import com.alimapps.senbombardir.ui.utils.DebounceEffect

sealed interface GameEffect : DebounceEffect {
    data object CloseScreen : GameEffect
    data object CloseScreenWithResult : GameEffect
    class OpenUpdateGame(val gameId: Long) : GameEffect
    class OpenGameResultsScreen(val gameId: Long) : GameEffect
    class ShowOptionPlayersBottomSheet(val optionPlayersUiModel: OptionPlayersUiModel) : GameEffect
    class ShowPlayerResultBottomSheet(val playerResultUiModel: PlayerResultUiModel) : GameEffect
    class ShowLiveGameResultBottomSheet(val liveGameResultUiModel: LiveGameResultUiModel) : GameEffect
    data object ShowStayTeamSelectionBottomSheet : GameEffect
    data object ShowDeleteGameConfirmationBottomSheet : GameEffect
    data object ShowClearResultsConfirmationBottomSheet : GameEffect
    data object ShowFinishGameConfirmationBottomSheet : GameEffect
    data object ShowGoBackConfirmationBottomSheet : GameEffect
    data object ShowGameInfoBottomSheet : GameEffect
    data object OpenActivationScreen : GameEffect
    class ShowBestPlayersBottomSheet(val bestPlayers: List<BestPlayerUiModel>) : GameEffect
    class ShowSnackbar(val stringRes: Int) : GameEffect
}