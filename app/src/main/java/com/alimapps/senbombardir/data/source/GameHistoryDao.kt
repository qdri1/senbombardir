package com.alimapps.senbombardir.data.source

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alimapps.senbombardir.data.model.GameHistoryActionEventModel
import com.alimapps.senbombardir.data.model.GameHistoryEntryModel

@Dao
interface GameHistoryDao {

    @Query("SELECT * FROM game_history_entries WHERE gameId = :gameId ORDER BY gameNumber ASC")
    suspend fun getGameHistoryEntries(gameId: Long): List<GameHistoryEntryModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGameHistoryEntry(entry: GameHistoryEntryModel): Long

    @Query("SELECT * FROM game_history_action_events WHERE historyEntryId = :historyEntryId ORDER BY elapsedSeconds ASC")
    suspend fun getGameHistoryActionEvents(historyEntryId: Long): List<GameHistoryActionEventModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGameHistoryActionEvent(event: GameHistoryActionEventModel)

    @Query("DELETE FROM game_history_entries WHERE gameId = :gameId")
    suspend fun deleteGameHistory(gameId: Long)
}
