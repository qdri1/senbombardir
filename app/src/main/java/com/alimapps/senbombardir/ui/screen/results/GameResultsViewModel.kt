package com.alimapps.senbombardir.ui.screen.results

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alimapps.senbombardir.R
import com.alimapps.senbombardir.data.repository.BillingRepository
import com.alimapps.senbombardir.data.repository.PlayerHistoryRepository
import com.alimapps.senbombardir.data.repository.PlayerRepository
import com.alimapps.senbombardir.data.repository.TeamHistoryRepository
import com.alimapps.senbombardir.domain.model.BillingType
import com.alimapps.senbombardir.ui.model.PlayerResultUiModel
import com.alimapps.senbombardir.ui.model.PlayerUiModel
import com.alimapps.senbombardir.ui.model.TeamUiModel
import com.alimapps.senbombardir.ui.model.toPlayerHistoryModel
import com.alimapps.senbombardir.ui.model.toTeamHistoryModel
import com.alimapps.senbombardir.ui.model.types.TeamOption
import com.alimapps.senbombardir.ui.utils.debounceEffect
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GameResultsViewModel(
    private val gameId: Long,
    private val teamHistoryRepository: TeamHistoryRepository,
    private val playerHistoryRepository: PlayerHistoryRepository,
    private val playerRepository: PlayerRepository,
    private val billingRepository: BillingRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameResultsUiState())
    val uiState: StateFlow<GameResultsUiState> = _uiState

    private val _effect = MutableSharedFlow<GameResultsEffect>()
    val effect: Flow<GameResultsEffect> get() = _effect.debounceEffect()

    init {
        fetchGameHistory()
    }

    fun action(action: GameResultsAction) {
        when (action) {
            is GameResultsAction.OnBackClicked -> setEffectSafely(GameResultsEffect.CloseScreen)
            is GameResultsAction.OnClearResultsClicked -> setEffectSafely(GameResultsEffect.ShowClearResultsConfirmationBottomSheet)
            is GameResultsAction.OnClearResultsConfirmationClicked -> onClearResultsConfirmationClicked()
            is GameResultsAction.OnSavePlayerResultClicked -> onSavePlayerResultClicked(action.playerResultUiModel, action.playerResultValue)
        }
    }

    private fun fetchGameHistory() = viewModelScope.launch {
        val teamUiModelList = teamHistoryRepository.getTeamsHistories(gameId)
            .sortedWith(compareByDescending<TeamUiModel> { it.points }
                .thenByDescending { it.goalsDifference }
                .thenBy { it.name }
            )
        val playerUiModelList = teamUiModelList.flatMap { teamUiModel ->
            playerHistoryRepository.getPlayersHistories(teamUiModel.id)
        }.sortedWith(compareByDescending<PlayerUiModel> { it.goals }
            .thenByDescending { it.assists }
            .thenByDescending { it.saves + it.dribbles + it.shots + it.passes }
            .thenByDescending { it.teamPoints }
            .thenByDescending { it.teamGoalsDifference }
            .thenBy { it.teamName }
            .thenBy { it.name }
        )
        val billingType = billingRepository.getCurrentBillingType()
        val clearResultsRemainingCount = billingRepository.getClearResultsRemainingCount()
        val uiLimited = billingType == BillingType.Limited && clearResultsRemainingCount <= 0
        setState(
            uiState.value.copy(
                teamUiModelList = teamUiModelList,
                playerUiModelList = playerUiModelList,
                uiLimited = uiLimited,
            )
        )
    }

    private fun onClearResultsConfirmationClicked() = viewModelScope.launch {
        teamHistoryRepository.getTeamsHistories(gameId).forEach { teamUiModel ->
            val teamHistoryModel = teamUiModel.copy(
                games = 0,
                wins = 0,
                draws = 0,
                loses = 0,
                goals = 0,
                conceded = 0,
                points = 0,
            ).toTeamHistoryModel()
            teamHistoryRepository.updateTeamHistory(teamHistoryModel)

            playerHistoryRepository.getPlayersHistories(teamUiModel.id).forEach { playerHistoryUiModel ->
                val playerUiModel = playerRepository.getPlayer(playerHistoryUiModel.id)
                if (playerUiModel == null) {
                    playerHistoryRepository.deletePlayerHistory(playerHistoryUiModel.id)
                } else {
                    val playerHistoryModel = playerHistoryUiModel.copy(
                        goals = 0,
                        assists = 0,
                        dribbles = 0,
                        passes = 0,
                        shots = 0,
                        saves = 0,
                    ).toPlayerHistoryModel()
                    playerHistoryRepository.updatePlayerHistory(playerHistoryModel)
                }
            }
        }
        fetchGameHistory()
    }

    private fun onSavePlayerResultClicked(
        playerResultUiModel: PlayerResultUiModel,
        playerResultValue: Int,
    ) {
        val playerUiModel = when (playerResultUiModel.option) {
            TeamOption.Goal -> playerResultUiModel.playerUiModel.copy(goals = playerResultValue)
            TeamOption.Assist -> playerResultUiModel.playerUiModel.copy(assists = playerResultValue)
            TeamOption.Save -> playerResultUiModel.playerUiModel.copy(saves = playerResultValue)
            TeamOption.Dribble -> playerResultUiModel.playerUiModel.copy(dribbles = playerResultValue)
            TeamOption.Shot -> playerResultUiModel.playerUiModel.copy(shots = playerResultValue)
            TeamOption.Pass -> playerResultUiModel.playerUiModel.copy(passes = playerResultValue)
        }

        viewModelScope.launch {
            playerHistoryRepository.updatePlayerHistory(playerUiModel.toPlayerHistoryModel())
            fetchGameHistory()
            setEffect(GameResultsEffect.ShowSnackbar(R.string.save_success))
        }
    }

    private fun setState(state: GameResultsUiState) {
        _uiState.update { state }
    }

    private suspend fun setEffect(effect: GameResultsEffect) {
        _effect.emit(effect)
    }

    private fun setEffectSafely(effect: GameResultsEffect) {
        viewModelScope.launch {
            _effect.emit(effect)
        }
    }
}