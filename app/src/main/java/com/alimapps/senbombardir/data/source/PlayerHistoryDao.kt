package com.alimapps.senbombardir.data.source

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.alimapps.senbombardir.data.model.PlayerHistoryModel

@Dao
interface PlayerHistoryDao {

    @Query("SELECT * FROM players_history WHERE teamId = :teamId")
    suspend fun getPlayersHistories(teamId: Long): List<PlayerHistoryModel>

    @Query("SELECT * FROM players_history WHERE id = :playerId")
    suspend fun getPlayerHistory(playerId: Long): PlayerHistoryModel?

    @Query("SELECT * FROM players_history WHERE teamId = :teamId AND name = :playerName LIMIT 1")
    suspend fun getPlayerHistory(teamId: Long, playerName: String): PlayerHistoryModel?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayerHistory(playerHistoryModel: PlayerHistoryModel)

    @Update
    suspend fun updatePlayerHistory(playerHistoryModel: PlayerHistoryModel)

    @Delete
    suspend fun deletePlayerHistory(playerHistoryModel: PlayerHistoryModel)

    @Query("DELETE FROM players_history WHERE id = :playerId")
    suspend fun deletePlayerHistory(playerId: Long)
}
