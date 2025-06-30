package com.alimapps.senbombardir.ui.model

import com.alimapps.senbombardir.ui.model.types.TeamOption

data class OptionPlayersUiModel(
    val option: TeamOption,
    val teamId: Long,
    val playerUiModelList: List<PlayerUiModel>,
)