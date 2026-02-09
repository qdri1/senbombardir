package com.alimapps.senbombardir.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alimapps.senbombardir.data.repository.GameRepository
import com.alimapps.senbombardir.ui.screen.game.result.DeleteGameResult
import com.alimapps.senbombardir.ui.utils.debounceEffect
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val gameRepository: GameRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    private val _effect = MutableSharedFlow<HomeEffect>()
    val effect: Flow<HomeEffect> get() = _effect.debounceEffect()

    init {
        fetchGames()
    }

    fun action(action: HomeAction) {
        when (action) {
            is HomeAction.OnScreenResumed -> refreshGamesSilently()
            is HomeAction.OnRefreshed -> fetchGames(isRefreshing = true)
            is HomeAction.OnRefreshIconClicked -> fetchGames()
            is HomeAction.OnGameCardClicked -> setEffectSafely(HomeEffect.OpenGameScreen(action.gameId))
            is HomeAction.OnAddGameButtonClicked -> setEffectSafely(HomeEffect.OpenAddGameScreen)
            is HomeAction.OnInterceptionNavigationResult -> onInterceptionNavigationResult(action.result)
        }
    }

    private fun fetchGames(isRefreshing: Boolean = false) {
        viewModelScope.launch {
            if (isRefreshing) {
                setState(uiState.value.copy(isRefreshing = true))
            } else {
                setState(uiState.value.copy(isLoading = true))
            }
            delay(500)
            val games = gameRepository.getGames().sortedByDescending { it.modifiedAt }
            setState(uiState.value.copy(games = games, isLoading = false, isRefreshing = false))
        }
    }

    private fun refreshGamesSilently() {
        viewModelScope.launch {
            val games = gameRepository.getGames().sortedByDescending { it.modifiedAt }
            setState(uiState.value.copy(games = games, isLoading = false, isRefreshing = false))
        }
    }

    private fun onInterceptionNavigationResult(result: Any) {
        when (result) {
            is DeleteGameResult -> fetchGames()
        }
    }

    private fun setState(state: HomeUiState) {
        _uiState.update { state }
    }

    private fun setEffectSafely(effect: HomeEffect) {
        viewModelScope.launch {
            _effect.emit(effect)
        }
    }
}