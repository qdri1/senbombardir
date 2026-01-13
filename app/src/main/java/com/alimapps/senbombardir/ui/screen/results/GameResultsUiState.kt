package com.alimapps.senbombardir.ui.screen.results

import com.alimapps.senbombardir.domain.model.BillingType
import com.alimapps.senbombardir.ui.model.PlayerUiModel
import com.alimapps.senbombardir.ui.model.TeamUiModel

data class GameResultsUiState(
    val teamUiModelList: List<TeamUiModel> = emptyList(),
    val playerUiModelList: List<PlayerUiModel> = emptyList(),
    val billingType: BillingType = BillingType.Limited,
    val uiLimited: Boolean = true,
)