package com.alimapps.senbombardir.ui.screen.game

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.annotation.RawRes
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.alimapps.senbombardir.R
import com.alimapps.senbombardir.data.repository.GameRepository
import com.alimapps.senbombardir.data.repository.LiveGameRepository
import com.alimapps.senbombardir.data.repository.PlayerRepository
import com.alimapps.senbombardir.data.repository.TeamRepository
import com.alimapps.senbombardir.ui.model.GameUiModel
import com.alimapps.senbombardir.ui.model.LiveGameUiModel
import com.alimapps.senbombardir.ui.model.OptionPlayersUiModel
import com.alimapps.senbombardir.ui.model.PlayerUiModel
import com.alimapps.senbombardir.ui.model.TeamUiModel
import com.alimapps.senbombardir.ui.model.toLiveGameModel
import com.alimapps.senbombardir.ui.model.toPlayerModel
import com.alimapps.senbombardir.ui.model.toTeamModel
import com.alimapps.senbombardir.ui.model.types.GameFunction
import com.alimapps.senbombardir.ui.model.types.GameRuleTeam2
import com.alimapps.senbombardir.ui.model.types.GameRuleTeam3
import com.alimapps.senbombardir.ui.model.types.GameRuleTeam4
import com.alimapps.senbombardir.ui.model.types.GameSounds
import com.alimapps.senbombardir.ui.model.types.TeamOption
import com.alimapps.senbombardir.ui.model.types.TeamQuantity
import com.alimapps.senbombardir.ui.screen.add.game.result.UpdateGameResult
import com.alimapps.senbombardir.ui.utils.debounceEffect
import com.alimapps.senbombardir.utils.empty
import com.alimapps.senbombardir.utils.orDefault
import com.alimapps.senbombardir.utils.toMillis
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.UUID
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class GameViewModel(
    private val gameId: Long,
    private val gameRepository: GameRepository,
    private val liveGameRepository: LiveGameRepository,
    private val teamRepository: TeamRepository,
    private val playerRepository: PlayerRepository,
    private val context: Context,
) : ViewModel(), TextToSpeech.OnInitListener {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState

    private val _effect = MutableSharedFlow<GameEffect>()
    val effect: Flow<GameEffect> get() = _effect.debounceEffect()

    private val _timerValueState = mutableStateOf(String.empty)
    val timerValueState: State<String> = _timerValueState

    private var timer: CountDownTimer? = null
    private var timerValue: Long = 0L

    private val mediaPlayer: ExoPlayer = ExoPlayer.Builder(context).build()
    private var textToSpeech: TextToSpeech = TextToSpeech(context, this)
    private var isTextToSpeechInitialized = false

    private val isLive: Boolean
        get() = uiState.value.liveGameUiModel?.isLive ?: false

    private val timeInMinutes: Int
        get() = uiState.value.gameUiModel?.timeInMinutes.orDefault()

    private var oldTeamId: Long = 0L

    init {
        fetchGame()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech.setLanguage(Locale("ru"))
            textToSpeech.voices
                ?.find { it.name.contains("ru", ignoreCase = true) }
                ?.let { textToSpeech.voice = it }
            isTextToSpeechInitialized = true
        }
    }

    fun action(action: GameAction) {
        when (action) {
            is GameAction.OnBackClicked -> onBackClicked()
            is GameAction.OnGoBackConfirmationClicked -> setEffectSafely(GameEffect.CloseScreen)
            is GameAction.OnDeleteGameConfirmationClicked -> onDeleteGameConfirmationClicked()
            is GameAction.OnClearResultsConfirmationClicked -> onClearResultsConfirmationClicked()
            is GameAction.OnStartFinishButtonClicked -> onStartFinishButtonClicked()
            is GameAction.OnFinishGameConfirmationClicked -> finishGame()
            is GameAction.OnTimerClicked -> onTimerClicked()
            is GameAction.OnLeftTeamClicked -> onLeftTeamClicked()
            is GameAction.OnRightTeamClicked -> onRightTeamClicked()
            is GameAction.OnLeftTeamOptionSelected -> onLeftTeamOptionSelected(action.option)
            is GameAction.OnRightTeamOptionSelected -> onRightTeamOptionSelected(action.option)
            is GameAction.OnTeamChangeIconClicked -> onTeamChangeIconClicked()
            is GameAction.OnLeftTeamChangeClicked -> onLeftTeamChangeClicked(action.teamId)
            is GameAction.OnRightTeamChangeClicked -> onRightTeamChangeClicked(action.teamId)
            is GameAction.OnOptionPlayersSelected -> onOptionPlayersSelected(action.teamId, action.playerUiModel, action.option)
            is GameAction.OnOptionPlayersAutoGoalSelected -> onOptionPlayersAutoGoalSelected(action.teamId)
            is GameAction.OnStayTeamSelectionBottomSheetDismissed -> onStayTeamSelectionBottomSheetDismissed()
            is GameAction.OnLeftTeamStayClicked -> onLeftTeamStayClicked()
            is GameAction.OnRightTeamStayClicked -> onRightTeamStayClicked()
            is GameAction.OnSoundClicked -> onSoundClicked(action.sound)
            is GameAction.OnFunctionClicked -> onFunctionClicked(action.function)
            is GameAction.OnInterceptionNavigationResult -> onInterceptionNavigationResult(action.result)
        }
    }

    private fun speak(text: String, onComplete: () -> Unit) {
        if (isTextToSpeechInitialized) {
            val utteranceId = UUID.randomUUID().toString()

            textToSpeech.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {}
                override fun onDone(utteranceId: String?) { onComplete() }
                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {}
            })

            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, Bundle(), utteranceId)
        }
    }

    private fun fetchGame() {
        viewModelScope.launch {
            gameRepository.getGame(gameId)?.let { gameUiModel ->
                val teamUiModelList = teamRepository.getTeams(gameUiModel.id)
                    .sortedWith(compareByDescending<TeamUiModel> { it.points }
                        .thenByDescending { it.goalsDifference }
                        .thenBy { it.name }
                    )
                val playerUiModelList = teamUiModelList.flatMap { teamUiModel ->
                    playerRepository.getPlayers(teamUiModel.id)
                }.sortedWith(compareByDescending<PlayerUiModel> { it.goals }
                    .thenByDescending { it.assists }
                    .thenByDescending { it.saves }
                    .thenByDescending { it.dribbles }
                    .thenByDescending { it.shots }
                    .thenByDescending { it.passes }
                    .thenByDescending { it.teamPoints }
                    .thenByDescending { it.teamGoalsDifference }
                    .thenBy { it.teamName }
                    .thenBy { it.name }
                )
                val liveGameUiModel = liveGameRepository.getLiveGame(gameId)
                setState(
                    uiState.value.copy(
                        gameUiModel = gameUiModel,
                        teamUiModelList = teamUiModelList,
                        playerUiModelList = playerUiModelList,
                        liveGameUiModel = liveGameUiModel,
                    )
                )
                setTimerValue()
            }
        }
    }

    private fun setTimerValue() {
        if (isLive) {
            val savedTimerValue = liveGameRepository.getTimerValue()
            timerValue = if (savedTimerValue > 0L) {
                savedTimerValue
            } else {
                timeInMinutes.toMillis()
            }
            _timerValueState.value = timerValue.toStringTime()
        } else {
            timerValue = timeInMinutes.toMillis()
            _timerValueState.value = timerValue.toStringTime()
        }
    }

    private fun onBackClicked() {
        if (isLive) {
            setEffectSafely(GameEffect.ShowGoBackConfirmationBottomSheet)
        } else {
            setEffectSafely(GameEffect.CloseScreen)
        }
    }

    private fun onDeleteGameClicked() {
        if (isLive) {
            setEffectSafely(GameEffect.ShowSnackbar(R.string.delete_game_snackbar_text))
            return
        }
        setEffectSafely(GameEffect.ShowDeleteGameConfirmationBottomSheet)
    }

    private fun onDeleteGameConfirmationClicked() = viewModelScope.launch {
        gameRepository.deleteGame(gameId)
        setEffect(GameEffect.CloseScreenWithResult)
    }

    private fun onClearResultsClicked() {
        if (isLive) {
            setEffectSafely(GameEffect.ShowSnackbar(R.string.finish_game_snackbar_text))
            return
        }
        setEffectSafely(GameEffect.ShowClearResultsConfirmationBottomSheet)
    }

    private fun onClearResultsConfirmationClicked() = viewModelScope.launch {
        teamRepository.getTeams(gameId).forEach { teamUiModel ->
            val teamModel = teamUiModel.copy(
                games = 0,
                wins = 0,
                draws = 0,
                loses = 0,
                goals = 0,
                conceded = 0,
                points = 0,
            ).toTeamModel()
            teamRepository.updateTeam(teamModel)

            playerRepository.getPlayers(teamUiModel.id).forEach { playerUiModel ->
                val playerModel = playerUiModel.copy(
                    goals = 0,
                    assists = 0,
                    dribbles = 0,
                    passes = 0,
                    shots = 0,
                    saves = 0,
                ).toPlayerModel()
                playerRepository.updatePlayer(playerModel)
            }
        }

        liveGameRepository.getLiveGame(gameId)?.let { liveGameUiModel ->
            val liveGameModel = liveGameUiModel.copy(
                leftTeamGoals = 0,
                leftTeamWinCount = 0,
                rightTeamGoals = 0,
                rightTeamWinCount = 0,
                gameCount = 0,
                isLive = false,
            ).toLiveGameModel()

            liveGameRepository.updateLiveGame(liveGameModel)
        }

        fetchGame()
    }

    private fun onEditGameClicked() {
        if (isLive) {
            setEffectSafely(GameEffect.ShowSnackbar(R.string.update_game_snackbar_text))
            return
        }
        setEffectSafely(GameEffect.OpenUpdateGame(gameId))
    }

    private fun onInfoClicked() {
        setEffectSafely(GameEffect.ShowGameInfoBottomSheet)
    }

    private fun onStartFinishButtonClicked() {
        uiState.value.liveGameUiModel?.let { liveGameUiModel ->
            if (liveGameUiModel.isLive.not()) {
                startGame(liveGameUiModel)
            } else {
                setEffectSafely(GameEffect.ShowFinishGameConfirmationBottomSheet)
            }
        }
    }

    private fun onTimerClicked() {
        if (isLive) {
            if (timer != null) {
                stopTimer()
            } else {
                startTimer()
            }
        }
    }

    private fun onTeamChangeIconClicked() = viewModelScope.launch {
        uiState.value.liveGameUiModel?.let { liveGameUiModel ->
            val copyLiveGameUiModel = liveGameUiModel.copy(
                leftTeamId = liveGameUiModel.rightTeamId,
                leftTeamName = liveGameUiModel.rightTeamName,
                leftTeamColor = liveGameUiModel.rightTeamColor,
                leftTeamGoals = liveGameUiModel.rightTeamGoals,
                leftTeamWinCount = liveGameUiModel.rightTeamWinCount,
                rightTeamId = liveGameUiModel.leftTeamId,
                rightTeamName = liveGameUiModel.leftTeamName,
                rightTeamColor = liveGameUiModel.leftTeamColor,
                rightTeamGoals = liveGameUiModel.leftTeamGoals,
                rightTeamWinCount = liveGameUiModel.leftTeamWinCount,
            )
            setState(uiState.value.copy(liveGameUiModel = copyLiveGameUiModel))
            liveGameRepository.updateLiveGame(copyLiveGameUiModel.toLiveGameModel())
        }
    }

    private fun onLeftTeamClicked() {
        if (isLive) {
            setState(uiState.value.copy(showLeftTeamOptionsDropdown = true))
        } else {
            if (uiState.value.teamUiModelList.size > 2) {
                setState(uiState.value.copy(showLeftTeamChangeDropdown = true))
            }
        }
    }

    private fun onRightTeamClicked() {
        if (isLive) {
            setState(uiState.value.copy(showRightTeamOptionsDropdown = true))
        } else {
            if (uiState.value.teamUiModelList.size > 2) {
                setState(uiState.value.copy(showRightTeamChangeDropdown = true))
            }
        }
    }

    private fun onLeftTeamOptionSelected(option: TeamOption?) {
        setState(uiState.value.copy(showLeftTeamOptionsDropdown = false))
        option?.let {
            uiState.value.liveGameUiModel?.let { liveGameUiModel ->
                val playerUiModelList = uiState.value.playerUiModelList.filter { playerUiModel ->
                    playerUiModel.teamId == liveGameUiModel.leftTeamId
                }.sortedBy { it.name }
                val optionPlayersUiModel = OptionPlayersUiModel(
                    option = option,
                    teamId = liveGameUiModel.leftTeamId,
                    playerUiModelList = playerUiModelList,
                )
                setEffectSafely(GameEffect.ShowOptionPlayersBottomSheet(optionPlayersUiModel))
            }
        }
    }

    private fun onLeftTeamChangeClicked(teamId: Long?) {
        setState(uiState.value.copy(showLeftTeamChangeDropdown = false))
        teamId?.let {
            uiState.value.liveGameUiModel?.let { liveGameUiModel ->
                viewModelScope.launch {
                    uiState.value.teamUiModelList.find { it.id == teamId }?.let { nextTeam ->
                        val copyLiveGameUiModel = liveGameUiModel.copy(
                            leftTeamId = nextTeam.id,
                            leftTeamName = nextTeam.name,
                            leftTeamColor = nextTeam.color,
                            leftTeamGoals = 0,
                            leftTeamWinCount = 0,
                            rightTeamGoals = 0,
                            rightTeamWinCount = 0,
                        )
                        setState(uiState.value.copy(liveGameUiModel = copyLiveGameUiModel))
                        liveGameRepository.updateLiveGame(copyLiveGameUiModel.toLiveGameModel())
                    }
                }
            }
        }
    }

    private fun onRightTeamChangeClicked(teamId: Long?) {
        setState(uiState.value.copy(showRightTeamChangeDropdown = false))
        teamId?.let {
            uiState.value.liveGameUiModel?.let { liveGameUiModel ->
                viewModelScope.launch {
                    uiState.value.teamUiModelList.find { it.id == teamId }?.let { nextTeam ->
                        val copyLiveGameUiModel = liveGameUiModel.copy(
                            leftTeamGoals = 0,
                            leftTeamWinCount = 0,
                            rightTeamId = nextTeam.id,
                            rightTeamName = nextTeam.name,
                            rightTeamColor = nextTeam.color,
                            rightTeamGoals = 0,
                            rightTeamWinCount = 0,
                        )
                        setState(uiState.value.copy(liveGameUiModel = copyLiveGameUiModel))
                        liveGameRepository.updateLiveGame(copyLiveGameUiModel.toLiveGameModel())
                    }
                }
            }
        }
    }

    private fun onRightTeamOptionSelected(option: TeamOption?) {
        setState(uiState.value.copy(showRightTeamOptionsDropdown = false))
        option?.let {
            uiState.value.liveGameUiModel?.let { liveGameUiModel ->
                val playerUiModelList = uiState.value.playerUiModelList.filter { playerUiModel ->
                    playerUiModel.teamId == liveGameUiModel.rightTeamId
                }.sortedBy { it.name }
                val optionPlayersUiModel = OptionPlayersUiModel(
                    option = option,
                    teamId = liveGameUiModel.rightTeamId,
                    playerUiModelList = playerUiModelList,
                )
                setEffectSafely(GameEffect.ShowOptionPlayersBottomSheet(optionPlayersUiModel))
            }
        }
    }

    private fun onStayTeamSelectionBottomSheetDismissed() = viewModelScope.launch {
        uiState.value.liveGameUiModel?.let { liveGameUiModel ->
            val ids = listOf(liveGameUiModel.leftTeamId, liveGameUiModel.rightTeamId)
            findNextTeam(ids)?.let { nextTeam ->
                updateLiveGameRightTeam(nextTeam, liveGameUiModel)
                updateLiveGameBlock()
            }
        }
    }

    private fun onLeftTeamStayClicked() = viewModelScope.launch {
        uiState.value.liveGameUiModel?.let { liveGameUiModel ->
            val ids = listOf(liveGameUiModel.leftTeamId, liveGameUiModel.rightTeamId)
            findNextTeam(ids)?.let { nextTeam ->
                updateLiveGameRightTeam(nextTeam, liveGameUiModel)
                updateLiveGameBlock()
            }
        }
    }

    private fun onRightTeamStayClicked() = viewModelScope.launch {
        uiState.value.liveGameUiModel?.let { liveGameUiModel ->
            val ids = listOf(liveGameUiModel.leftTeamId, liveGameUiModel.rightTeamId)
            findNextTeam(ids)?.let { nextTeam ->
                updateLiveGameLeftTeam(nextTeam, liveGameUiModel)
                updateLiveGameBlock()
            }
        }
    }

    private fun onOptionPlayersSelected(teamId: Long, playerUiModel: PlayerUiModel, option: TeamOption) = viewModelScope.launch {
        when (option) {
            TeamOption.Goal -> {
                uiState.value.liveGameUiModel?.let { liveGameUiModel ->
                    when (teamId) {
                        liveGameUiModel.leftTeamId -> {
                            val copyLiveGameUiModel = liveGameUiModel.copy(leftTeamGoals = liveGameUiModel.leftTeamGoals + 1)
                            setState(uiState.value.copy(liveGameUiModel = copyLiveGameUiModel))
                            liveGameRepository.updateLiveGame(copyLiveGameUiModel.toLiveGameModel())
                        }
                        liveGameUiModel.rightTeamId -> {
                            val copyLiveGameUiModel = liveGameUiModel.copy(rightTeamGoals = liveGameUiModel.rightTeamGoals + 1)
                            setState(uiState.value.copy(liveGameUiModel = copyLiveGameUiModel))
                            liveGameRepository.updateLiveGame(copyLiveGameUiModel.toLiveGameModel())
                        }
                        else -> Unit
                    }
                }
                val copyPlayerUiModel = playerUiModel.copy(goals = playerUiModel.goals + 1)
                playerRepository.updatePlayer(copyPlayerUiModel.toPlayerModel())
                speak(
                    text = context.getString(R.string.text_to_speech_goal, playerUiModel.name),
                    onComplete = { playGoalSound() },
                )
            }
            TeamOption.Assist -> {
                val copyPlayerUiModel = playerUiModel.copy(assists = playerUiModel.assists + 1)
                playerRepository.updatePlayer(copyPlayerUiModel.toPlayerModel())
                speak(
                    text = context.getString(R.string.text_to_speech_assist, playerUiModel.name),
                    onComplete = { playGirlsApplauseSound() },
                )
            }
            TeamOption.Dribble -> {
                val copyPlayerUiModel = playerUiModel.copy(dribbles = playerUiModel.dribbles + 1)
                playerRepository.updatePlayer(copyPlayerUiModel.toPlayerModel())
                speak(
                    text = context.getString(R.string.text_to_speech_dribble, playerUiModel.name),
                    onComplete = { playBilgeninIstepJatyrSound() },
                )
            }
            TeamOption.Pass -> {
                val copyPlayerUiModel = playerUiModel.copy(passes = playerUiModel.passes + 1)
                playerRepository.updatePlayer(copyPlayerUiModel.toPlayerModel())
                speak(
                    text = context.getString(R.string.text_to_speech_pass, playerUiModel.name),
                    onComplete = { playStadiumApplauseSound() },
                )
            }
            TeamOption.Shot -> {
                val copyPlayerUiModel = playerUiModel.copy(shots = playerUiModel.shots + 1)
                playerRepository.updatePlayer(copyPlayerUiModel.toPlayerModel())
                speak(
                    text = context.getString(R.string.text_to_speech_shot, playerUiModel.name),
                    onComplete = { playSuiiiSound() },
                )
            }
            TeamOption.Save -> {
                val copyPlayerUiModel = playerUiModel.copy(saves = playerUiModel.saves + 1)
                playerRepository.updatePlayer(copyPlayerUiModel.toPlayerModel())
                speak(
                    text = context.getString(R.string.text_to_speech_save, playerUiModel.name),
                    onComplete = { playGoalSaveSound() },
                )
            }
        }
        updatePlayersBlock()
    }

    private fun onOptionPlayersAutoGoalSelected(teamId: Long) = viewModelScope.launch {
        uiState.value.liveGameUiModel?.let { liveGameUiModel ->
            when (teamId) {
                liveGameUiModel.leftTeamId -> {
                    val copyLiveGameUiModel = liveGameUiModel.copy(leftTeamGoals = liveGameUiModel.leftTeamGoals + 1)
                    setState(uiState.value.copy(liveGameUiModel = copyLiveGameUiModel))
                    liveGameRepository.updateLiveGame(copyLiveGameUiModel.toLiveGameModel())
                }
                liveGameUiModel.rightTeamId -> {
                    val copyLiveGameUiModel = liveGameUiModel.copy(rightTeamGoals = liveGameUiModel.rightTeamGoals + 1)
                    setState(uiState.value.copy(liveGameUiModel = copyLiveGameUiModel))
                    liveGameRepository.updateLiveGame(copyLiveGameUiModel.toLiveGameModel())
                }
                else -> Unit
            }
        }
        speak(
            text = context.getString(R.string.team_option_players_auto_goal),
            onComplete = { playGoalSound() },
        )
    }

    private fun startGame(liveGameUiModel: LiveGameUiModel) {
        playStartMatchSound()
        startTimer()
        viewModelScope.launch {
            val copyLiveGameUiModel = liveGameUiModel.copy(isLive = true)
            setState(uiState.value.copy(liveGameUiModel = copyLiveGameUiModel))
            liveGameRepository.updateLiveGame(copyLiveGameUiModel.toLiveGameModel())
        }
    }

    private fun finishGame() {
        playFinishSound()
        resetTimer()
        uiState.value.gameUiModel?.let { gameUiModel ->
            uiState.value.liveGameUiModel?.let { liveGameUiModel ->
                when (gameUiModel.teamQuantity) {
                    TeamQuantity.Team2 -> finishTeam2Game(gameUiModel, liveGameUiModel)
                    TeamQuantity.Team3 -> finishTeam3Game(gameUiModel, liveGameUiModel)
                    TeamQuantity.Team4 -> finishTeam4Game(gameUiModel, liveGameUiModel)
                }
            }
        }
    }

    private fun finishTeam2Game(
        gameUiModel: GameUiModel,
        liveGameUiModel: LiveGameUiModel,
    ) = viewModelScope.launch {
        when (gameUiModel.gameRule) {
            GameRuleTeam2.AFTER_TIME_CHANGE_SIDE -> {
                val copyLiveGameUiModel = liveGameUiModel.copy(
                    leftTeamId = liveGameUiModel.rightTeamId,
                    leftTeamName = liveGameUiModel.rightTeamName,
                    leftTeamColor = liveGameUiModel.rightTeamColor,
                    leftTeamGoals = liveGameUiModel.rightTeamGoals,
                    leftTeamWinCount = 0,
                    rightTeamId = liveGameUiModel.leftTeamId,
                    rightTeamName = liveGameUiModel.leftTeamName,
                    rightTeamColor = liveGameUiModel.leftTeamColor,
                    rightTeamGoals = liveGameUiModel.leftTeamGoals,
                    rightTeamWinCount = 0,
                    gameCount = liveGameUiModel.gameCount + 1,
                    isLive = false,
                )
                setState(uiState.value.copy(liveGameUiModel = copyLiveGameUiModel))
                liveGameRepository.updateLiveGame(copyLiveGameUiModel.toLiveGameModel())
            }
            GameRuleTeam2.AFTER_TIME_STAY_SIDE -> {
                val copyLiveGameUiModel = liveGameUiModel.copy(
                    gameCount = liveGameUiModel.gameCount + 1,
                    isLive = false,
                )
                setState(uiState.value.copy(liveGameUiModel = copyLiveGameUiModel))
                liveGameRepository.updateLiveGame(copyLiveGameUiModel.toLiveGameModel())
            }
        }
    }

    private fun finishTeam3Game(
        gameUiModel: GameUiModel,
        liveGameUiModel: LiveGameUiModel,
    ) = viewModelScope.launch {
        val ids = listOf(liveGameUiModel.leftTeamId, liveGameUiModel.rightTeamId)

        when (gameUiModel.gameRule) {
            GameRuleTeam3.ONLY_2_GAMES -> {
                when {
                    liveGameUiModel.isLeftTeamWin -> updateLeftTeamWin(liveGameUiModel)
                    liveGameUiModel.isRightTeamWin -> updateRightTeamWin(liveGameUiModel)
                    else -> updateTeamsDraw(liveGameUiModel)
                }
                findNextTeam(ids)?.let { nextTeam ->
                    if (liveGameUiModel.leftTeamWinCount > liveGameUiModel.rightTeamWinCount) {
                        updateLiveGameLeftTeam(nextTeam, liveGameUiModel)
                    } else {
                        updateLiveGameRightTeam(nextTeam, liveGameUiModel)
                    }
                }
            }
            GameRuleTeam3.WINNER_STAY_2 -> {
                finishGameRuleTeam3WinnerStay(
                    liveGameUiModel = liveGameUiModel,
                    ids = ids,
                    winCount = 1,
                )
            }
            GameRuleTeam3.WINNER_STAY_3 -> {
                finishGameRuleTeam3WinnerStay(
                    liveGameUiModel = liveGameUiModel,
                    ids = ids,
                    winCount = 2,
                )
            }
            GameRuleTeam3.WINNER_STAY_4 -> {
                finishGameRuleTeam3WinnerStay(
                    liveGameUiModel = liveGameUiModel,
                    ids = ids,
                    winCount = 3,
                )
            }
            GameRuleTeam3.WINNER_STAY_UNLIMITED -> {
                when {
                    liveGameUiModel.isLeftTeamWin -> {
                        updateLeftTeamWin(liveGameUiModel)
                        findNextTeam(ids)?.let { nextTeam -> updateLiveGameRightTeam(nextTeam, liveGameUiModel) }
                    }
                    liveGameUiModel.isRightTeamWin -> {
                        updateRightTeamWin(liveGameUiModel)
                        findNextTeam(ids)?.let { nextTeam -> updateLiveGameLeftTeam(nextTeam, liveGameUiModel) }
                    }
                    else -> {
                        updateTeamsDraw(liveGameUiModel)
                        updateLiveGameRuleTeam3WhenDraw(liveGameUiModel, ids)
                    }
                }
            }
        }
        updateTeamsBlock()
        updatePlayersBlock()
        updateLiveGameBlock()
    }

    private fun finishTeam4Game(
        gameUiModel: GameUiModel,
        liveGameUiModel: LiveGameUiModel,
    ) = viewModelScope.launch {
        val ids = listOf(liveGameUiModel.leftTeamId, liveGameUiModel.rightTeamId, oldTeamId)

        when (gameUiModel.gameRule) {
            GameRuleTeam4.ONLY_3_GAMES -> {
                when {
                    liveGameUiModel.isLeftTeamWin -> updateLeftTeamWin(liveGameUiModel)
                    liveGameUiModel.isRightTeamWin -> updateRightTeamWin(liveGameUiModel)
                    else -> updateTeamsDraw(liveGameUiModel)
                }
                findNextTeam(ids)?.let { nextTeam ->
                    when {
                        liveGameUiModel.leftTeamWinCount == 0 && liveGameUiModel.rightTeamWinCount == 0 -> {
                            oldTeamId = liveGameUiModel.rightTeamId
                            updateLiveGameRightTeam(nextTeam, liveGameUiModel)
                        }
                        liveGameUiModel.leftTeamWinCount == 1 -> {
                            oldTeamId = liveGameUiModel.rightTeamId
                            updateLiveGameRightTeam(nextTeam, liveGameUiModel)
                        }
                        liveGameUiModel.leftTeamWinCount == 2 -> {
                            oldTeamId = liveGameUiModel.leftTeamId
                            updateLiveGameLeftTeam(nextTeam, liveGameUiModel)
                        }
                        liveGameUiModel.rightTeamWinCount == 1 -> {
                            oldTeamId = liveGameUiModel.leftTeamId
                            updateLiveGameLeftTeam(nextTeam, liveGameUiModel)
                        }
                        liveGameUiModel.rightTeamWinCount == 2 -> {
                            oldTeamId = liveGameUiModel.rightTeamId
                            updateLiveGameRightTeam(nextTeam, liveGameUiModel)
                        }
                    }
                }
            }
            GameRuleTeam4.WINNER_STAY_3 -> {
                finishGameRuleTeam4WinnerStay(
                    liveGameUiModel = liveGameUiModel,
                    ids = ids,
                    winCount = 2,
                )
            }
            GameRuleTeam4.WINNER_STAY_4 -> {
                finishGameRuleTeam4WinnerStay(
                    liveGameUiModel = liveGameUiModel,
                    ids = ids,
                    winCount = 3,
                )
            }
            GameRuleTeam4.WINNER_STAY_5 -> {
                finishGameRuleTeam4WinnerStay(
                    liveGameUiModel = liveGameUiModel,
                    ids = ids,
                    winCount = 4,
                )
            }
            GameRuleTeam4.WINNER_STAY_6 -> {
                finishGameRuleTeam4WinnerStay(
                    liveGameUiModel = liveGameUiModel,
                    ids = ids,
                    winCount = 5,
                )
            }
            GameRuleTeam4.WINNER_STAY_UNLIMITED -> {
                when {
                    liveGameUiModel.isLeftTeamWin -> {
                        updateLeftTeamWin(liveGameUiModel)
                        findNextTeam(ids)?.let { nextTeam ->
                            oldTeamId = liveGameUiModel.rightTeamId
                            updateLiveGameRightTeam(nextTeam, liveGameUiModel)
                        }
                    }
                    liveGameUiModel.isRightTeamWin -> {
                        updateRightTeamWin(liveGameUiModel)
                        findNextTeam(ids)?.let { nextTeam ->
                            oldTeamId = liveGameUiModel.leftTeamId
                            updateLiveGameLeftTeam(nextTeam, liveGameUiModel)
                        }
                    }
                    else -> {
                        updateTeamsDraw(liveGameUiModel)
                        updateLiveGameRuleTeam4WhenDraw(liveGameUiModel)
                    }
                }
            }
        }
        updateTeamsBlock()
        updatePlayersBlock()
        updateLiveGameBlock()
    }

    private suspend fun finishGameRuleTeam3WinnerStay(
        liveGameUiModel: LiveGameUiModel,
        ids: List<Long>,
        winCount: Int,
    ) {
        when {
            liveGameUiModel.isLeftTeamWin -> {
                updateLeftTeamWin(liveGameUiModel)
                findNextTeam(ids)?.let { nextTeam ->
                    if (liveGameUiModel.leftTeamWinCount >= winCount) {
                        updateLiveGameLeftTeam(nextTeam, liveGameUiModel)
                    } else {
                        updateLiveGameRightTeam(nextTeam, liveGameUiModel)
                    }
                }
            }
            liveGameUiModel.isRightTeamWin -> {
                updateRightTeamWin(liveGameUiModel)
                findNextTeam(ids)?.let { nextTeam ->
                    if (liveGameUiModel.rightTeamWinCount >= winCount) {
                        updateLiveGameRightTeam(nextTeam, liveGameUiModel)
                    } else {
                        updateLiveGameLeftTeam(nextTeam, liveGameUiModel)
                    }
                }
            }
            else -> {
                updateTeamsDraw(liveGameUiModel)
                updateLiveGameRuleTeam3WhenDraw(liveGameUiModel, ids)
            }
        }
    }

    private suspend fun updateLiveGameRuleTeam3WhenDraw(
        liveGameUiModel: LiveGameUiModel,
        ids: List<Long>,
    ) {
        findNextTeam(ids)?.let { nextTeam ->
            when {
                liveGameUiModel.leftTeamWinCount > liveGameUiModel.rightTeamWinCount -> {
                    updateLiveGameLeftTeam(nextTeam, liveGameUiModel)
                }
                liveGameUiModel.leftTeamWinCount < liveGameUiModel.rightTeamWinCount -> {
                    updateLiveGameRightTeam(nextTeam, liveGameUiModel)
                }
                else -> setEffectSafely(GameEffect.ShowStayTeamSelectionBottomSheet)
            }
        }
    }

    private suspend fun finishGameRuleTeam4WinnerStay(
        liveGameUiModel: LiveGameUiModel,
        ids: List<Long>,
        winCount: Int,
    ) {
        when {
            liveGameUiModel.isLeftTeamWin -> {
                updateLeftTeamWin(liveGameUiModel)
                findNextTeam(ids)?.let { nextTeam ->
                    if (liveGameUiModel.leftTeamWinCount >= winCount) {
                        oldTeamId = liveGameUiModel.leftTeamId
                        updateLiveGameLeftTeam(nextTeam, liveGameUiModel)
                    } else {
                        oldTeamId = liveGameUiModel.rightTeamId
                        updateLiveGameRightTeam(nextTeam, liveGameUiModel)
                    }
                }
            }
            liveGameUiModel.isRightTeamWin -> {
                updateRightTeamWin(liveGameUiModel)
                findNextTeam(ids)?.let { nextTeam ->
                    if (liveGameUiModel.rightTeamWinCount >= winCount) {
                        oldTeamId = liveGameUiModel.rightTeamId
                        updateLiveGameRightTeam(nextTeam, liveGameUiModel)
                    } else {
                        oldTeamId = liveGameUiModel.leftTeamId
                        updateLiveGameLeftTeam(nextTeam, liveGameUiModel)
                    }
                }
            }
            else -> {
                updateTeamsDraw(liveGameUiModel)
                updateLiveGameRuleTeam4WhenDraw(liveGameUiModel)
            }
        }
    }

    private suspend fun updateLiveGameRuleTeam4WhenDraw(
        liveGameUiModel: LiveGameUiModel,
    ) {
        val nextTeams = uiState.value.teamUiModelList
            .filter { it.id !in listOf(liveGameUiModel.leftTeamId, liveGameUiModel.rightTeamId) }

        val (newTeam, oldTeam) = when (oldTeamId) {
            nextTeams.getOrNull(0)?.id -> Pair(nextTeams.getOrNull(1), nextTeams.getOrNull(0))
            nextTeams.getOrNull(1)?.id -> Pair(nextTeams.getOrNull(0), nextTeams.getOrNull(1))
            else -> Pair(nextTeams.getOrNull(0), nextTeams.getOrNull(1))
        }
        if (newTeam != null && oldTeam != null) {
            oldTeamId = when {
                liveGameUiModel.leftTeamWinCount > liveGameUiModel.rightTeamWinCount -> {
                    liveGameUiModel.leftTeamId
                }
                liveGameUiModel.leftTeamWinCount < liveGameUiModel.rightTeamWinCount -> {
                    liveGameUiModel.rightTeamId
                }
                else -> liveGameUiModel.rightTeamId
            }
            updateLiveGameBothTeam(
                newTeam = newTeam,
                oldTeam = oldTeam,
                liveGameUiModel = liveGameUiModel,
            )
        }
    }

    private fun findNextTeam(ids: List<Long>): TeamUiModel? = uiState.value.teamUiModelList.find { it.id !in ids }

    private suspend fun updateLiveGameLeftTeam(nextTeam: TeamUiModel, liveGameUiModel: LiveGameUiModel) {
        val copyLiveGameUiModel = liveGameUiModel.copy(
            leftTeamId = nextTeam.id,
            leftTeamName = nextTeam.name,
            leftTeamColor = nextTeam.color,
            leftTeamGoals = 0,
            leftTeamWinCount = 0,
            rightTeamGoals = 0,
            rightTeamWinCount = liveGameUiModel.rightTeamWinCount + 1,
            gameCount = liveGameUiModel.gameCount + 1,
            isLive = false,
        )
        liveGameRepository.updateLiveGame(copyLiveGameUiModel.toLiveGameModel())
    }

    private suspend fun updateLiveGameRightTeam(nextTeam: TeamUiModel, liveGameUiModel: LiveGameUiModel) {
        val copyLiveGameUiModel = liveGameUiModel.copy(
            leftTeamGoals = 0,
            leftTeamWinCount = liveGameUiModel.leftTeamWinCount + 1,
            rightTeamId = nextTeam.id,
            rightTeamName = nextTeam.name,
            rightTeamColor = nextTeam.color,
            rightTeamGoals = 0,
            rightTeamWinCount = 0,
            gameCount = liveGameUiModel.gameCount + 1,
            isLive = false,
        )
        liveGameRepository.updateLiveGame(copyLiveGameUiModel.toLiveGameModel())
    }

    private suspend fun updateLiveGameBothTeam(
        newTeam: TeamUiModel,
        oldTeam: TeamUiModel,
        liveGameUiModel: LiveGameUiModel,
    ) {
        val copyLiveGameUiModel = liveGameUiModel.copy(
            leftTeamId = newTeam.id,
            leftTeamName = newTeam.name,
            leftTeamColor = newTeam.color,
            leftTeamGoals = 0,
            leftTeamWinCount = 0,
            rightTeamId = oldTeam.id,
            rightTeamName = oldTeam.name,
            rightTeamColor = oldTeam.color,
            rightTeamGoals = 0,
            rightTeamWinCount = 1,
            gameCount = liveGameUiModel.gameCount + 1,
            isLive = false,
        )
        liveGameRepository.updateLiveGame(copyLiveGameUiModel.toLiveGameModel())
    }

    private suspend fun updateLeftTeamWin(liveGameUiModel: LiveGameUiModel) {
        uiState.value.teamUiModelList.find { it.id == liveGameUiModel.leftTeamId }?.let { winnerTeam ->
            val team = winnerTeam.copy(
                games = winnerTeam.games + 1,
                wins = winnerTeam.wins + 1,
                goals = winnerTeam.goals + liveGameUiModel.leftTeamGoals,
                conceded = winnerTeam.conceded + liveGameUiModel.rightTeamGoals,
                points = winnerTeam.points + 3,
            )
            teamRepository.updateTeam(team.toTeamModel())
        }
        uiState.value.teamUiModelList.find { it.id == liveGameUiModel.rightTeamId }?.let { loserTeam ->
            val team = loserTeam.copy(
                games = loserTeam.games + 1,
                loses = loserTeam.loses + 1,
                goals = loserTeam.goals + liveGameUiModel.rightTeamGoals,
                conceded = loserTeam.conceded + liveGameUiModel.leftTeamGoals,
            )
            teamRepository.updateTeam(team.toTeamModel())
        }
    }

    private suspend fun updateRightTeamWin(liveGameUiModel: LiveGameUiModel) {
        uiState.value.teamUiModelList.find { it.id == liveGameUiModel.rightTeamId }?.let { winnerTeam ->
            val team = winnerTeam.copy(
                games = winnerTeam.games + 1,
                wins = winnerTeam.wins + 1,
                goals = winnerTeam.goals + liveGameUiModel.rightTeamGoals,
                conceded = winnerTeam.conceded + liveGameUiModel.leftTeamGoals,
                points = winnerTeam.points + 3,
            )
            teamRepository.updateTeam(team.toTeamModel())
        }
        uiState.value.teamUiModelList.find { it.id == liveGameUiModel.leftTeamId }?.let { loserTeam ->
            val team = loserTeam.copy(
                games = loserTeam.games + 1,
                loses = loserTeam.loses + 1,
                goals = loserTeam.goals + liveGameUiModel.leftTeamGoals,
                conceded = loserTeam.conceded + liveGameUiModel.rightTeamGoals,
            )
            teamRepository.updateTeam(team.toTeamModel())
        }
    }

    private suspend fun updateTeamsDraw(liveGameUiModel: LiveGameUiModel) {
        uiState.value.teamUiModelList.find { it.id == liveGameUiModel.leftTeamId }?.let { drawerTeam ->
            val team = drawerTeam.copy(
                games = drawerTeam.games + 1,
                draws = drawerTeam.draws + 1,
                goals = drawerTeam.goals + liveGameUiModel.leftTeamGoals,
                conceded = drawerTeam.conceded + liveGameUiModel.rightTeamGoals,
                points = drawerTeam.points + 1,
            )
            teamRepository.updateTeam(team.toTeamModel())
        }
        uiState.value.teamUiModelList.find { it.id == liveGameUiModel.rightTeamId }?.let { drawerTeam ->
            val team = drawerTeam.copy(
                games = drawerTeam.games + 1,
                draws = drawerTeam.draws + 1,
                goals = drawerTeam.goals + liveGameUiModel.rightTeamGoals,
                conceded = drawerTeam.conceded + liveGameUiModel.leftTeamGoals,
                points = drawerTeam.points + 1,
            )
            teamRepository.updateTeam(team.toTeamModel())
        }
    }

    private suspend fun updateLiveGameBlock() {
        val liveGameUiModel = liveGameRepository.getLiveGame(gameId)
        setState(uiState.value.copy(liveGameUiModel = liveGameUiModel))
    }

    private suspend fun updateTeamsBlock() {
        val teamUiModelList = teamRepository.getTeams(gameId)
            .sortedWith(compareByDescending<TeamUiModel> { it.points }
                .thenByDescending { it.goalsDifference }
                .thenBy { it.name }
            )
        setState(uiState.value.copy(teamUiModelList = teamUiModelList))
    }

    private suspend fun updatePlayersBlock() {
        val playerUiModelList = uiState.value.teamUiModelList.flatMap { teamUiModel ->
            playerRepository.getPlayers(teamUiModel.id)
        }.sortedWith(compareByDescending<PlayerUiModel> { it.goals }
            .thenByDescending { it.assists }
            .thenByDescending { it.saves }
            .thenByDescending { it.dribbles }
            .thenByDescending { it.shots }
            .thenByDescending { it.passes }
            .thenByDescending { it.teamPoints }
            .thenByDescending { it.teamGoalsDifference }
            .thenBy { it.teamName }
            .thenBy { it.name }
        )
        setState(uiState.value.copy(playerUiModelList = playerUiModelList))
    }

    private fun onInterceptionNavigationResult(result: Any) {
        when (result) {
            is UpdateGameResult -> fetchGame()
        }
    }

    private fun startTimer() {
        setState(uiState.value.copy(isTimerPlay = true))
        timer = object : CountDownTimer(timerValue, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                when (millisUntilFinished) {
                    in 60000L..60999L -> playMinutaSound()
                    in 10000L..10999L -> playDoAutaSound()
                }
                timerValue = millisUntilFinished
                _timerValueState.value = millisUntilFinished.toStringTime()
            }
            override fun onFinish() {}
        }
        timer?.start()
    }

    private fun stopTimer() {
        setState(uiState.value.copy(isTimerPlay = false))
        timer?.cancel()
        timer = null
    }

    private fun resetTimer() {
        stopTimer()
        timerValue = timeInMinutes.toMillis()
        _timerValueState.value = timerValue.toStringTime()
        liveGameRepository.clearTimerValue()
    }

    private fun playGoalSound() {
        when (Random.nextInt(0, 3)) {
            0 -> playMedia(resId = R.raw.oooi_kandai_gol)
            1 -> playMedia(resId = R.raw.suiii_full)
            2 -> playMedia(resId = R.raw.gol_gol_gol)
        }
    }

    private fun playStartMatchSound() {
        playMedia(resId = R.raw.start_match)
    }

    private fun playFinishSound() {
        playMedia(resId = R.raw.finish)
    }

    private fun playMinutaSound() {
        playMedia(resId = R.raw.minuta)
    }

    private fun playDoAutaSound() {
        playMedia(resId = R.raw.do_auta)
    }

    private fun playGirlsApplauseSound() {
        playMedia(resId = R.raw.girls_applause)
    }

    private fun playStadiumApplauseSound() {
        playMedia(resId = R.raw.stadium_applause)
    }

    private fun playBilgeninIstepJatyrSound() {
        playMedia(resId = R.raw.bilgenin_istep_jatyr)
    }

    private fun playGoalSaveSound() {
        playMedia(resId = R.raw.goal_save)
    }

    private fun playModrichtynPasySound() {
        playMedia(resId = R.raw.modrichtyn_pasy)
    }

    private fun playTondyrypTastaganSound() {
        playMedia(resId = R.raw.tondyryp_tastagan)
    }

    private fun playSuiiiSound() {
        playMedia(resId = R.raw.suiiiii)
    }

    private fun playWhistle() {
        playMedia(resId = R.raw.whistle)
    }

    private fun onSoundClicked(sound: GameSounds) {
        when (sound) {
            GameSounds.Whistle -> playWhistle()
            GameSounds.StadiumApplause -> playStadiumApplauseSound()
            GameSounds.GirlsApplause -> playGirlsApplauseSound()
            GameSounds.BilgeninIstepJatyr -> playBilgeninIstepJatyrSound()
            GameSounds.GoalSave -> playGoalSaveSound()
            GameSounds.Suiii -> playSuiiiSound()
            GameSounds.ModrichtynPasy -> playModrichtynPasySound()
            GameSounds.TondyrypTastagan -> playTondyrypTastaganSound()
        }
    }

    private fun onFunctionClicked(function: GameFunction) {
        when (function) {
            GameFunction.Edit -> onEditGameClicked()
            GameFunction.ClearResults -> onClearResultsClicked()
            GameFunction.Info -> onInfoClicked()
            GameFunction.Delete -> onDeleteGameClicked()
        }
    }

    private fun playMedia(@RawRes resId: Int) = Handler(Looper.getMainLooper()).post {
        val url = "android.resource://${context.packageName}/$resId"

        val mediaItem = MediaItem.Builder()
            .setUri(url)
            .build()

        mediaPlayer.setMediaItem(mediaItem)
        mediaPlayer.prepare()
        mediaPlayer.play()
    }

    private fun setState(state: GameUiState) {
        _uiState.update { state }
    }

    private suspend fun setEffect(effect: GameEffect) {
        _effect.emit(effect)
    }

    private fun setEffectSafely(effect: GameEffect) {
        viewModelScope.launch {
            _effect.emit(effect)
        }
    }

    private fun Long.toStringTime(): String =
        String.format(
            "%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(this) % TimeUnit.HOURS.toMinutes(1),
            TimeUnit.MILLISECONDS.toSeconds(this) % TimeUnit.MINUTES.toSeconds(1)
        )

    override fun onCleared() {
        if (isLive) {
            liveGameRepository.saveTimerValue(timerValue)
        }
        stopTimer()
        mediaPlayer.release()
        textToSpeech.stop()
        textToSpeech.shutdown()

        super.onCleared()
    }
}