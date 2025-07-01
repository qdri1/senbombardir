package com.alimapps.senbombardir.data.repository

import com.alimapps.senbombardir.data.model.LiveGameModel
import com.alimapps.senbombardir.data.source.LiveGameDao
import com.alimapps.senbombardir.data.source.Prefs
import com.alimapps.senbombardir.ui.model.LiveGameUiModel
import com.alimapps.senbombardir.ui.model.types.TeamColor

class LiveGameRepository(
    private val liveGameDao: LiveGameDao,
    private val prefs: Prefs,
) {

    suspend fun getLiveGame(gameId: Long): LiveGameUiModel? {
        val liveGameUiModel = liveGameDao.getLiveGame(gameId)?.let {
            LiveGameUiModel(
                id = it.id,
                gameId = it.gameId,
                leftTeamId = it.leftTeamId,
                leftTeamName = it.leftTeamName,
                leftTeamColor = TeamColor.getTeamColor(it.leftTeamColor),
                leftTeamGoals = it.leftTeamGoals,
                leftTeamWinCount = it.leftTeamWinCount,
                rightTeamId = it.rightTeamId,
                rightTeamName = it.rightTeamName,
                rightTeamColor = TeamColor.getTeamColor(it.rightTeamColor),
                rightTeamGoals = it.rightTeamGoals,
                rightTeamWinCount = it.rightTeamWinCount,
                gameCount = it.gameCount,
                isLive = it.isLive,
            )
        }

        return liveGameUiModel
    }
    
    suspend fun saveLiveGame(liveGameModel: LiveGameModel): Long = liveGameDao.insertLiveGame(liveGameModel)

    suspend fun updateLiveGame(liveGameModel: LiveGameModel) { liveGameDao.updateLiveGame(liveGameModel) }

    fun saveTimerValue(value: Long) {
        prefs.timerValue = value
    }

    fun clearTimerValue() {
        prefs.timerValue = 0L
    }

    fun getTimerValue(): Long = prefs.timerValue
}