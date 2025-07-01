package com.alimapps.senbombardir.data.source

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.alimapps.senbombardir.data.model.TeamHistoryModel

@Dao
interface TeamHistoryDao {

    @Query("SELECT * FROM teams_history WHERE gameId = :gameId")
    suspend fun getTeamsHistories(gameId: Long): List<TeamHistoryModel>

    @Query("SELECT * FROM teams_history WHERE id = :teamId")
    suspend fun getTeamHistory(teamId: Long): TeamHistoryModel?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeamHistory(teamHistoryModel: TeamHistoryModel)

    @Update
    suspend fun updateTeamHistory(teamHistoryModel: TeamHistoryModel)
}
