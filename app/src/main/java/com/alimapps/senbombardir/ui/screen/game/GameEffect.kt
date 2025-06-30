package com.alimapps.senbombardir.ui.screen.game

import com.alimapps.senbombardir.ui.model.OptionPlayersUiModel
import com.alimapps.senbombardir.ui.utils.DebounceEffect

sealed interface GameEffect : DebounceEffect {
    data object CloseScreen : GameEffect
    data object CloseScreenWithResult : GameEffect
    class OpenUpdateGame(val gameId: Long) : GameEffect
    class ShowOptionPlayersBottomSheet(val optionPlayersUiModel: OptionPlayersUiModel) : GameEffect
    data object ShowStayTeamSelectionBottomSheet : GameEffect
    data object ShowDeleteGameConfirmationBottomSheet : GameEffect
    data object ShowClearResultsConfirmationBottomSheet : GameEffect
    data object ShowFinishGameConfirmationBottomSheet : GameEffect
    data object ShowGoBackConfirmationBottomSheet : GameEffect
    data object ShowGameInfoBottomSheet : GameEffect
    class ShowSnackbar(val stringRes: Int) : GameEffect
}