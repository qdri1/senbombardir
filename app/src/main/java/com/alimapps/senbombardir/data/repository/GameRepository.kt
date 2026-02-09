package com.alimapps.senbombardir.data.repository

import com.alimapps.senbombardir.data.model.GameModel
import com.alimapps.senbombardir.data.source.GameDao
import com.alimapps.senbombardir.ui.model.types.GameFormat
import com.alimapps.senbombardir.ui.model.types.GameRule
import com.alimapps.senbombardir.ui.model.GameUiModel
import com.alimapps.senbombardir.ui.model.types.TeamQuantity

class GameRepository(
    private val gameDao: GameDao,
) {

    suspend fun getGames(): List<GameUiModel> {
        val games = gameDao.getGames().map { gameModel ->
            val format = GameFormat.getGameFormat(gameModel.format)
            val teamQuantity = TeamQuantity.getTeamQuantity(gameModel.teamQuantity)
            val rule = GameRule.getGameRule(
                teamQuantity = teamQuantity,
                rule = gameModel.rule
            )

            GameUiModel(
                id = gameModel.id,
                name = gameModel.name,
                gameFormat = format,
                teamQuantity = teamQuantity,
                gameRule = rule,
                timeInMinutes = gameModel.timeInMinutes,
                modifiedAt = gameModel.modifiedAt,
            )
        }

        return games
    }

    suspend fun getGame(gameId: Long): GameUiModel? {
        val gameUiModel = gameDao.getGame(gameId)?.let { gameModel ->
            val format = GameFormat.getGameFormat(gameModel.format)
            val teamQuantity = TeamQuantity.getTeamQuantity(gameModel.teamQuantity)
            val rule = GameRule.getGameRule(
                teamQuantity = teamQuantity,
                rule = gameModel.rule
            )

            GameUiModel(
                id = gameModel.id,
                name = gameModel.name,
                gameFormat = format,
                teamQuantity = teamQuantity,
                gameRule = rule,
                timeInMinutes = gameModel.timeInMinutes,
                modifiedAt = gameModel.modifiedAt,
            )
        }

        return gameUiModel
    }
    
    suspend fun saveGame(gameModel: GameModel): Long = gameDao.insertGame(gameModel)

    suspend fun updateGame(gameModel: GameModel) { gameDao.updateGame(gameModel) }

    suspend fun deleteGame(gameModel: GameModel) { gameDao.deleteGame(gameModel) }

    suspend fun deleteGame(gameId: Long) { gameDao.deleteGame(gameId) }
}