package com.alimapps.senbombardir.ui.screen.add.game

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alimapps.senbombardir.R
import com.alimapps.senbombardir.data.model.GameModel
import com.alimapps.senbombardir.data.model.LiveGameModel
import com.alimapps.senbombardir.data.model.PlayerHistoryModel
import com.alimapps.senbombardir.data.model.PlayerModel
import com.alimapps.senbombardir.data.model.TeamModel
import com.alimapps.senbombardir.data.model.toPlayerHistoryModel
import com.alimapps.senbombardir.data.model.toTeamHistoryModel
import com.alimapps.senbombardir.data.repository.GameRepository
import com.alimapps.senbombardir.data.repository.LiveGameRepository
import com.alimapps.senbombardir.data.repository.PlayerHistoryRepository
import com.alimapps.senbombardir.data.repository.PlayerRepository
import com.alimapps.senbombardir.data.repository.TeamHistoryRepository
import com.alimapps.senbombardir.data.repository.TeamRepository
import com.alimapps.senbombardir.ui.model.GameUiModel
import com.alimapps.senbombardir.ui.model.TeamUiModel
import com.alimapps.senbombardir.ui.model.toGameModel
import com.alimapps.senbombardir.ui.model.toLiveGameModel
import com.alimapps.senbombardir.ui.model.toTeamHistoryModel
import com.alimapps.senbombardir.ui.model.toTeamModel
import com.alimapps.senbombardir.ui.model.types.GameFormat
import com.alimapps.senbombardir.ui.model.types.GameRule
import com.alimapps.senbombardir.ui.model.types.GameRuleTeam2
import com.alimapps.senbombardir.ui.model.types.GameRuleTeam3
import com.alimapps.senbombardir.ui.model.types.GameRuleTeam4
import com.alimapps.senbombardir.ui.model.types.TeamColor
import com.alimapps.senbombardir.ui.model.types.TeamQuantity
import com.alimapps.senbombardir.ui.utils.debounceEffect
import com.alimapps.senbombardir.utils.empty
import com.alimapps.senbombardir.utils.orDefault
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

private const val DEFAULT_TIME_IN_MINUTES = 7
private const val DEFAULT_TAB_INDEX = 0

enum class ScreenStateType {
    Add, Update
}

class AddGameViewModel(
    private val gameId: Long?,
    private val gameRepository: GameRepository,
    private val liveGameRepository: LiveGameRepository,
    private val teamRepository: TeamRepository,
    private val teamHistoryRepository: TeamHistoryRepository,
    private val playerRepository: PlayerRepository,
    private val playerHistoryRepository: PlayerHistoryRepository,
) : ViewModel() {

    private val _screenStateType = mutableStateOf(ScreenStateType.Add)
    val screenStateType: State<ScreenStateType> = _screenStateType

    private val _gameNameFieldState = mutableStateOf(String.empty)
    val gameNameFieldState: State<String> = _gameNameFieldState

    private val _timeInMinuteFieldState = mutableStateOf(DEFAULT_TIME_IN_MINUTES.toString())
    val timeInMinuteFieldState: State<String> = _timeInMinuteFieldState

    private val _gameFormatState = mutableStateOf(GameFormat.Format5x5)
    val gameFormatState: State<GameFormat> = _gameFormatState

    private val _teamQuantityState = mutableStateOf(TeamQuantity.Team3)
    val teamQuantityState: State<TeamQuantity> = _teamQuantityState

    private val _gameRuleState = mutableStateOf<GameRule>(GameRuleTeam3.ONLY_2_GAMES)
    val gameRuleState: State<GameRule> = _gameRuleState

    private val _selectedTeamTabIndex = mutableIntStateOf(DEFAULT_TAB_INDEX)
    val selectedTeamTabIndex: State<Int> = _selectedTeamTabIndex

    var teamColors by mutableStateOf(
        List(teamQuantityState.value.quantity) { index ->
            mutableStateOf(TeamColor.entries[index])
        }
    )
    var teamNameFields by mutableStateOf(
        List(teamQuantityState.value.quantity) {
            mutableStateOf(String.empty)
        }
    )
    var playersTextFields by mutableStateOf(
        List(teamQuantityState.value.quantity) {
            mutableStateListOf<String>().apply {
                repeat(gameFormatState.value.playerQuantity) {
                    add(String.empty)
                }
            }
        }
    )

    private val _effect = MutableSharedFlow<AddGameEffect>()
    val effect: Flow<AddGameEffect> get() = _effect.debounceEffect()

    private var gameUiModel: GameUiModel? = null
    private var teamUiModelList: List<TeamUiModel> = emptyList()

    init {
        _screenStateType.value = if (gameId == null) ScreenStateType.Add else ScreenStateType.Update
        fetchGame()
    }

    fun action(action: AddGameAction) {
        when (action) {
            is AddGameAction.CloseScreen -> setEffectSafely(AddGameEffect.CloseScreen)
            is AddGameAction.OnGameTextValueChanged -> _gameNameFieldState.value = action.value
            is AddGameAction.OnTimeTextValueChanged -> _timeInMinuteFieldState.value = action.value
            is AddGameAction.OnGameFormatSelected -> onGameFormatSelected(action.format)
            is AddGameAction.OnTeamQuantitySelected -> onTeamQuantitySelected(action.teamQuantity)
            is AddGameAction.OnGameRuleSelected -> _gameRuleState.value = action.rule
            is AddGameAction.OnTeamTabClicked -> _selectedTeamTabIndex.intValue = action.tabIndex
            is AddGameAction.OnTeamColorClicked -> setEffectSafely(AddGameEffect.ShowColorsBottomSheet)
            is AddGameAction.OnTeamColorSelected -> onTeamColorSelected(action.color)
            is AddGameAction.OnTeamNameValueChanged -> onTeamNameValueChanged(action.tabIndex, action.value)
            is AddGameAction.OnPlayerNameValueChanged -> onPlayerNameValueChanged(action.tabIndex, action.fieldIndex, action.value)
            is AddGameAction.OnAddPlayerClicked -> addPlayerFieldToTab(action.tabIndex)
            is AddGameAction.OnFinishClicked -> onFinishClicked()
        }
    }

    private fun fetchGame() {
        gameId?.let { gameId ->
            viewModelScope.launch {
                gameUiModel = gameRepository.getGame(gameId)
                gameUiModel?.let { gameUiModel ->
                    _gameNameFieldState.value = gameUiModel.name
                    _timeInMinuteFieldState.value = gameUiModel.timeInMinutes.toString()
                    _gameFormatState.value = gameUiModel.gameFormat
                    _teamQuantityState.value = gameUiModel.teamQuantity
                    _gameRuleState.value = gameUiModel.gameRule

                    teamUiModelList = teamRepository.getTeams(gameUiModel.id)
                    teamColors = List(teamUiModelList.size) { index -> mutableStateOf(teamUiModelList[index].color) }
                    teamNameFields = List(teamUiModelList.size) { index -> mutableStateOf(teamUiModelList[index].name) }

                    playersTextFields = List(teamUiModelList.size) { index ->
                        val playerUiModelList = playerRepository.getPlayers(teamUiModelList[index].id)
                        mutableStateListOf<String>().apply {
                            repeat(playerUiModelList.size) { index ->
                                add(playerUiModelList[index].name)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun onGameFormatSelected(format: GameFormat) {
        _gameFormatState.value = format
        playersTextFields = List(teamQuantityState.value.quantity) {
            mutableStateListOf<String>().apply { repeat(format.playerQuantity) { add(String.empty) } }
        }
    }

    private fun onTeamQuantitySelected(teamQuantity: TeamQuantity) {
        if (selectedTeamTabIndex.value >= teamQuantity.quantity ) {
            _selectedTeamTabIndex.intValue = DEFAULT_TAB_INDEX
        }
        _teamQuantityState.value = teamQuantity

        _gameRuleState.value = when (teamQuantity) {
            TeamQuantity.Team2 -> GameRuleTeam2.AFTER_TIME_CHANGE_SIDE
            TeamQuantity.Team3 -> GameRuleTeam3.ONLY_2_GAMES
            TeamQuantity.Team4 -> GameRuleTeam4.ONLY_3_GAMES
        }

        teamColors = List(teamQuantity.quantity) { index -> mutableStateOf(TeamColor.entries[index]) }
        teamNameFields = List(teamQuantity.quantity) { mutableStateOf(String.empty) }
        playersTextFields = List(teamQuantity.quantity) {
            mutableStateListOf<String>().apply { repeat(gameFormatState.value.playerQuantity) { add(String.empty) } }
        }
    }

    private fun onTeamColorSelected(color: TeamColor) {
        teamColors[selectedTeamTabIndex.value].value = color
    }

    private fun onTeamNameValueChanged(tabIndex: Int, value: String) {
        teamNameFields[tabIndex].value = value
    }

    private fun onPlayerNameValueChanged(tabIndex: Int, fieldIndex: Int, value: String) {
        playersTextFields[tabIndex][fieldIndex] = value
    }

    private fun addPlayerFieldToTab(tabIndex: Int) {
        playersTextFields[tabIndex].add(String.empty)
    }

    private fun onFinishClicked() = viewModelScope.launch {
        if (checkRequiredFields().not()) {
            return@launch
        }

        when (screenStateType.value) {
            ScreenStateType.Add -> addGame()
            ScreenStateType.Update -> updateGame()
        }
    }

    private suspend fun addGame() {
        val rule = when (val gameRule = gameRuleState.value) {
            is GameRuleTeam2 -> gameRule.name
            is GameRuleTeam3 -> gameRule.name
            is GameRuleTeam4 -> gameRule.name
            else -> String.empty
        }

        val gameModel = GameModel(
            name = gameNameFieldState.value.trim(),
            format = gameFormatState.value.format,
            teamQuantity = teamQuantityState.value.quantity,
            rule = rule,
            timeInMinutes = timeInMinuteFieldState.value.toIntOrNull() ?: DEFAULT_TIME_IN_MINUTES,
            modifiedAt = System.currentTimeMillis(),
        )
        val gameId = gameRepository.saveGame(gameModel)

        teamNameFields.forEachIndexed { index, teamNameValue ->
            val teamModel = TeamModel(
                gameId = gameId,
                name = teamNameValue.value.trim(),
                color = teamColors.getOrNull(index)?.value?.hexColor.orEmpty(),
                games = 0,
                wins = 0,
                draws = 0,
                loses = 0,
                goals = 0,
                conceded = 0,
                points = 0,
            )
            val teamId = teamRepository.saveTeam(teamModel)
            teamHistoryRepository.saveTeamHistory(teamModel.toTeamHistoryModel(teamId))

            playersTextFields.getOrNull(index)?.let { players ->
                players.filter { it.isNotEmpty() }.map { playerNameValue ->
                    PlayerModel(
                        teamId = teamId,
                        name = playerNameValue.trim(),
                        goals = 0,
                        assists = 0,
                        dribbles = 0,
                        passes = 0,
                        shots = 0,
                        saves = 0,
                    )
                }.forEach { playerModel ->
                    val playerId = playerRepository.savePlayer(playerModel)
                    playerHistoryRepository.savePlayerHistory(playerModel.toPlayerHistoryModel(playerId))
                }
            }
        }

        val teams = teamRepository.getTeams(gameId)
        val leftTeam = teams.getOrNull(0)
        val rightTeam = teams.getOrNull(1)

        val liveGameModel = LiveGameModel(
            gameId = gameId,
            leftTeamId = leftTeam?.id.orDefault(),
            leftTeamName = leftTeam?.name.orEmpty(),
            leftTeamColor = leftTeam?.color?.hexColor.orEmpty(),
            leftTeamGoals = 0,
            leftTeamWinCount = 0,
            rightTeamId = rightTeam?.id.orDefault(),
            rightTeamName = rightTeam?.name.orEmpty(),
            rightTeamColor = rightTeam?.color?.hexColor.orEmpty(),
            rightTeamGoals = 0,
            rightTeamWinCount = 0,
            gameCount = 0,
            isLive = false,
        )
        liveGameRepository.saveLiveGame(liveGameModel)

        setEffect(AddGameEffect.OpenGameScreen(gameId))
    }

    private suspend fun updateGame() {
        gameId?.let { gameId ->
            gameUiModel?.let { gameUiModel ->
                val gameModel = gameUiModel.toGameModel().copy(
                    name = gameNameFieldState.value.trim(),
                    timeInMinutes = timeInMinuteFieldState.value.toIntOrNull() ?: DEFAULT_TIME_IN_MINUTES,
                    modifiedAt = System.currentTimeMillis(),
                )
                gameRepository.updateGame(gameModel)
            }

            teamNameFields.forEachIndexed { index, teamNameValue ->
                teamUiModelList.getOrNull(index)?.toTeamModel()?.copy(
                    name = teamNameValue.value.trim(),
                    color = teamColors.getOrNull(index)?.value?.hexColor.orEmpty(),
                )?.let { teamModel ->
                    teamRepository.updateTeam(teamModel)

                    teamHistoryRepository.getTeamHistory(teamModel.id)?.let { teamHistoryUiModel ->
                        val copyTeamHistoryModel = teamHistoryUiModel.toTeamHistoryModel().copy(
                            name = teamModel.name,
                            color = teamModel.color,
                        )
                        teamHistoryRepository.updateTeamHistory(copyTeamHistoryModel)
                    }

                    playersTextFields.getOrNull(index)?.let { playerNameValues ->
                        val playerUiModelList = playerRepository.getPlayers(teamModel.id)
                        playerNameValues.forEachIndexed { index, playerNameValue ->
                            val playerUiModel = playerUiModelList.getOrNull(index)

                            if (playerUiModel != null) {
                                if (playerNameValue.isNotEmpty()) {
                                    if (playerNameValue != playerUiModel.name) {
                                        playerRepository.deletePlayer(playerUiModel.id)
                                        val newPlayerModel = PlayerModel(
                                            teamId = teamModel.id,
                                            name = playerNameValue.trim(),
                                            goals = playerUiModel.goals,
                                            assists = playerUiModel.assists,
                                            dribbles = playerUiModel.dribbles,
                                            passes = playerUiModel.passes,
                                            shots = playerUiModel.shots,
                                            saves = playerUiModel.saves,
                                        )
                                        val playerId = playerRepository.savePlayer(newPlayerModel)

                                        val playerHistoryUiModel = playerHistoryRepository.getPlayerHistory(
                                            teamId = newPlayerModel.teamId,
                                            playerName = newPlayerModel.name,
                                        )
                                        val playerHistoryModel = if (playerHistoryUiModel != null) {
                                            playerHistoryRepository.deletePlayerHistory(playerHistoryUiModel.id)
                                            PlayerHistoryModel(
                                                id = playerId,
                                                teamId = newPlayerModel.teamId,
                                                name = newPlayerModel.name,
                                                goals = playerHistoryUiModel.goals,
                                                assists = playerHistoryUiModel.assists,
                                                dribbles = playerHistoryUiModel.dribbles,
                                                passes = playerHistoryUiModel.passes,
                                                shots = playerHistoryUiModel.shots,
                                                saves = playerHistoryUiModel.saves,
                                            )
                                        } else {
                                            PlayerHistoryModel(
                                                id = playerId,
                                                teamId = newPlayerModel.teamId,
                                                name = newPlayerModel.name,
                                                goals = 0,
                                                assists = 0,
                                                dribbles = 0,
                                                passes = 0,
                                                shots = 0,
                                                saves = 0,
                                            )
                                        }
                                        playerHistoryRepository.savePlayerHistory(playerHistoryModel)
                                    }
                                } else {
                                    playerRepository.deletePlayer(playerUiModel.id)
                                }
                            } else if (playerNameValue.isNotEmpty()) {
                                val newPlayerModel = PlayerModel(
                                    teamId = teamModel.id,
                                    name = playerNameValue.trim(),
                                    goals = 0,
                                    assists = 0,
                                    dribbles = 0,
                                    passes = 0,
                                    shots = 0,
                                    saves = 0,
                                )
                                val playerId = playerRepository.savePlayer(newPlayerModel)

                                val playerHistoryUiModel = playerHistoryRepository.getPlayerHistory(
                                    teamId = newPlayerModel.teamId,
                                    playerName = newPlayerModel.name,
                                )
                                val playerHistoryModel = if (playerHistoryUiModel != null) {
                                    playerHistoryRepository.deletePlayerHistory(playerHistoryUiModel.id)
                                    PlayerHistoryModel(
                                        id = playerId,
                                        teamId = newPlayerModel.teamId,
                                        name = newPlayerModel.name,
                                        goals = playerHistoryUiModel.goals,
                                        assists = playerHistoryUiModel.assists,
                                        dribbles = playerHistoryUiModel.dribbles,
                                        passes = playerHistoryUiModel.passes,
                                        shots = playerHistoryUiModel.shots,
                                        saves = playerHistoryUiModel.saves,
                                    )
                                } else {
                                    PlayerHistoryModel(
                                        id = playerId,
                                        teamId = newPlayerModel.teamId,
                                        name = newPlayerModel.name,
                                        goals = 0,
                                        assists = 0,
                                        dribbles = 0,
                                        passes = 0,
                                        shots = 0,
                                        saves = 0,
                                    )
                                }
                                playerHistoryRepository.savePlayerHistory(playerHistoryModel)
                            }
                        }
                    }
                }
            }

            liveGameRepository.getLiveGame(gameId)?.let { liveGameUiModel ->
                val teams = teamRepository.getTeams(gameId)
                val leftTeam = teams.find { it.id == liveGameUiModel.leftTeamId }
                val rightTeam = teams.find { it.id == liveGameUiModel.rightTeamId }

                val liveGameModel = liveGameUiModel.toLiveGameModel().copy(
                    leftTeamName = leftTeam?.name.orEmpty(),
                    leftTeamColor = leftTeam?.color?.hexColor.orEmpty(),
                    rightTeamName = rightTeam?.name.orEmpty(),
                    rightTeamColor = rightTeam?.color?.hexColor.orEmpty(),
                )
                liveGameRepository.updateLiveGame(liveGameModel)
            }

            setEffect(AddGameEffect.CloseScreenWithResult)
        }
    }

    private fun checkRequiredFields(): Boolean {
        when {
            gameNameFieldState.value.isEmpty() -> R.string.game_name_empty_text
            timeInMinuteFieldState.value.isEmpty() -> R.string.game_time_empty_text
            teamNameFields.any { it.value.isEmpty() } -> R.string.team_name_empty_text
            else -> null
        }?.let { stringRes ->
            setEffectSafely(AddGameEffect.ShowSnackbar(stringRes))
            return false
        }
        return true
    }

    private suspend fun setEffect(effect: AddGameEffect) {
        _effect.emit(effect)
    }

    private fun setEffectSafely(effect: AddGameEffect) {
        viewModelScope.launch {
            _effect.emit(effect)
        }
    }
}