package com.alimapps.senbombardir.ui.screen.results

import com.alimapps.senbombardir.ui.model.BestPlayerUiModel
import com.alimapps.senbombardir.ui.model.PlayerUiModel
import com.alimapps.senbombardir.ui.utils.DebounceEffect

sealed interface GameResultsEffect : DebounceEffect {
    data object CloseScreen : GameResultsEffect
    data object OpenActivationScreen : GameResultsEffect
    data object ShowClearResultsConfirmationBottomSheet : GameResultsEffect
    class ShowBestPlayersBottomSheet(val bestPlayers: List<BestPlayerUiModel>) : GameResultsEffect
    class ShowSnackbar(val stringRes: Int) : GameResultsEffect
    class ShowRemovePlayerConfirmationBottomSheet(val playerUiModel: PlayerUiModel) : GameResultsEffect
}