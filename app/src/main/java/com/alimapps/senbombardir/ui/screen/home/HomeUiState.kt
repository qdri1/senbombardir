package com.alimapps.senbombardir.ui.screen.home

import com.alimapps.senbombardir.ui.model.GameUiModel

data class HomeUiState(
    val games: List<GameUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
)