package com.alimapps.senbombardir.ui.screen.game

import com.alimapps.senbombardir.domain.model.BillingType
import com.alimapps.senbombardir.ui.model.GameUiModel
import com.alimapps.senbombardir.ui.model.LiveGameUiModel
import com.alimapps.senbombardir.ui.model.PlayerUiModel
import com.alimapps.senbombardir.ui.model.TeamUiModel

data class GameUiState(
    val gameUiModel: GameUiModel? = null,
    val liveGameUiModel: LiveGameUiModel? = null,
    val teamUiModelList: List<TeamUiModel> = emptyList(),
    val playerUiModelList: List<PlayerUiModel> = emptyList(),
    val showLeftTeamOptionsDropdown: Boolean = false,
    val showRightTeamOptionsDropdown: Boolean = false,
    val showLeftTeamChangeDropdown: Boolean = false,
    val showRightTeamChangeDropdown: Boolean = false,
    val isTimerPlay: Boolean = false,
    val billingType: BillingType = BillingType.Lifetime,
    val clearResultsRemainingCount: Int = 2,
    val uiLimited: Boolean = false,
)