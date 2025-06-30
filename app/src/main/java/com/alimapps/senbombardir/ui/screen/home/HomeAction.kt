package com.alimapps.senbombardir.ui.screen.home

sealed interface HomeAction {
    data object OnRefreshed : HomeAction
    data object OnRefreshIconClicked : HomeAction
    class OnGameCardClicked(val gameId: Long) : HomeAction
    data object OnAddGameButtonClicked : HomeAction
    class OnInterceptionNavigationResult(val result: Any) : HomeAction
}