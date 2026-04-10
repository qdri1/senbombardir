package com.alimapps.senbombardir.ui.screen.results

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alimapps.senbombardir.R
import com.alimapps.senbombardir.data.repository.BillingRepository
import com.alimapps.senbombardir.data.repository.PlayerHistoryRepository
import com.alimapps.senbombardir.data.repository.PlayerRepository
import com.alimapps.senbombardir.data.repository.TeamHistoryRepository
import com.alimapps.senbombardir.domain.model.BillingType
import com.alimapps.senbombardir.ui.model.BestPlayerUiModel
import com.alimapps.senbombardir.ui.model.PlayerResultUiModel
import com.alimapps.senbombardir.ui.model.PlayerUiModel
import com.alimapps.senbombardir.ui.model.TeamUiModel
import com.alimapps.senbombardir.ui.model.toPlayerHistoryModel
import com.alimapps.senbombardir.ui.model.toTeamHistoryModel
import com.alimapps.senbombardir.ui.model.types.BestPlayerOption
import com.alimapps.senbombardir.ui.model.types.GameResultsFunction
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
            is GameResultsAction.OnClearResultsConfirmationClicked -> onClearResultsConfirmationClicked()
            is GameResultsAction.OnSavePlayerResultClicked -> onSavePlayerResultClicked(action.playerResultUiModel, action.playerResultValue)
            is GameResultsAction.OnFunctionClicked -> onFunctionClicked(action.function)
            is GameResultsAction.OnActivateClicked -> setEffectSafely(GameResultsEffect.OpenActivationScreen)
            is GameResultsAction.OnRemovePlayerHistoryClicked -> setEffectSafely(GameResultsEffect.ShowRemovePlayerConfirmationBottomSheet(action.playerUiModel))
            is GameResultsAction.OnRemovePlayerHistoryConfirmationClicked -> onRemovePlayerHistoryConfirmationClicked(action.playerUiModel)
        }
    }

    private fun fetchGameHistory() = viewModelScope.launch {
        val teamUiModelList = teamHistoryRepository.getTeamsHistories(gameId)
            .sortedWith(compareByDescending<TeamUiModel> { it.points }
                .thenByDescending { it.goalsDifference }
                .thenBy { it.name }
            )
        val playerUiModelList = teamUiModelList.flatMap { teamUiModel ->
            playerHistoryRepository.getPlayersHistories(teamUiModel.id).map { playerUiModel ->
                playerUiModel.copy(existsInPlayerList = playerRepository.getPlayer(playerUiModel.id) != null)
            }
        }.sortedWith(compareByDescending<PlayerUiModel> { it.goals }
            .thenByDescending { it.assists }
            .thenByDescending { it.saves }
            .thenByDescending { it.dribbles + it.shots + it.passes }
            .thenBy { it.redCards }
            .thenBy { it.yellowCards }
            .thenByDescending { it.teamPoints }
            .thenByDescending { it.teamGoalsDifference }
            .thenBy { it.teamName }
            .thenBy { it.name }
        )
        val billingType = billingRepository.getCurrentBillingType()
        val uiLimited = billingType == BillingType.Limited
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

            playerHistoryRepository.getPlayersHistories(teamUiModel.id)
                .forEach { playerHistoryUiModel ->
                    val playerHistoryModel = playerHistoryUiModel.copy(
                        goals = 0,
                        assists = 0,
                        dribbles = 0,
                        passes = 0,
                        shots = 0,
                        saves = 0,
                        yellowCards = 0,
                        redCards = 0,
                    ).toPlayerHistoryModel()

                    playerHistoryRepository.updatePlayerHistory(playerHistoryModel)
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
            TeamOption.YellowCard -> playerResultUiModel.playerUiModel.copy(yellowCards = playerResultValue)
            TeamOption.RedCard -> playerResultUiModel.playerUiModel.copy(redCards = playerResultValue)
        }

        viewModelScope.launch {
            playerHistoryRepository.updatePlayerHistory(playerUiModel.toPlayerHistoryModel())
            fetchGameHistory()
            setEffect(GameResultsEffect.ShowSnackbar(R.string.save_success))
        }
    }

    private fun onRemovePlayerHistoryConfirmationClicked(playerUiModel: PlayerUiModel) = viewModelScope.launch {
        playerHistoryRepository.deletePlayerHistory(playerUiModel.id)
        fetchGameHistory()
    }

    private fun onFunctionClicked(function: GameResultsFunction) {
        when (function) {
            GameResultsFunction.BestPlayers -> onBestPlayersClicked()
            GameResultsFunction.ClearResults -> setEffectSafely(GameResultsEffect.ShowClearResultsConfirmationBottomSheet)
        }
    }

    private fun onBestPlayersClicked() {
        val bestPlayers = mutableListOf<BestPlayerUiModel>()
        val playerList = uiState.value.playerUiModelList

        playerList.maxByOrNull {
            (it.goals * 3) + (it.assists * 2) + (it.saves * 2) + it.dribbles + it.passes + it.shots - it.yellowCards - (it.redCards * 3)
        }?.let { best ->
            bestPlayers.add(
                BestPlayerUiModel(
                    option = BestPlayerOption.BestPlayer,
                    playerUiModel = best
                )
            )
        }

        val statOptions = listOf(
            Triple(BestPlayerOption.Goals, { it: PlayerUiModel -> it.goals > 0 }, { it: PlayerUiModel -> it.goals }),
            Triple(BestPlayerOption.Assists, { it: PlayerUiModel -> it.assists > 0 }, { it: PlayerUiModel -> it.assists }),
            Triple(BestPlayerOption.Saves, { it: PlayerUiModel -> it.saves > 0 }, { it: PlayerUiModel -> it.saves }),
            Triple(BestPlayerOption.Dribbles, { it: PlayerUiModel -> it.dribbles > 0 }, { it: PlayerUiModel -> it.dribbles }),
            Triple(BestPlayerOption.Passes, { it: PlayerUiModel -> it.passes > 0 }, { it: PlayerUiModel -> it.passes }),
            Triple(BestPlayerOption.Shots, { it: PlayerUiModel -> it.shots > 0 }, { it: PlayerUiModel -> it.shots }),
        )

        statOptions.forEach { (option, filter, selector) ->
            playerList.filter(filter).maxByOrNull(selector)?.let { best ->
                bestPlayers.add(
                    BestPlayerUiModel(
                        option = option,
                        playerUiModel = best
                    )
                )
            }
        }

        playerList.filter { it.yellowCards > 0 || it.redCards > 0 }
            .reversed()
            .maxByOrNull { it.yellowCards + (it.redCards * 3) }
            ?.let { aggressivePlayer ->
                bestPlayers.add(
                    BestPlayerUiModel(
                        option = BestPlayerOption.AggressivePlayer,
                        playerUiModel = aggressivePlayer
                    )
                )
            }

        setEffectSafely(GameResultsEffect.ShowBestPlayersBottomSheet(bestPlayers = bestPlayers))
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