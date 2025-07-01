package com.alimapps.senbombardir.data.source

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.alimapps.senbombardir.data.model.TeamModel

@Dao
interface TeamDao {

    @Query("SELECT * FROM teams WHERE gameId = :gameId")
    suspend fun getTeams(gameId: Long): List<TeamModel>

    @Query("SELECT * FROM teams WHERE id = :teamId")
    suspend fun getTeam(teamId: Long): TeamModel?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeam(teamModel: TeamModel): Long

    @Update
    suspend fun updateTeam(teamModel: TeamModel)
}
