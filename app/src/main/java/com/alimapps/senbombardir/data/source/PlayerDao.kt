package com.alimapps.senbombardir.data.source

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.alimapps.senbombardir.data.model.PlayerModel

@Dao
interface PlayerDao {

    @Query("SELECT * FROM players WHERE teamId = :teamId")
    suspend fun getPlayers(teamId: Long): List<PlayerModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayer(playerModel: PlayerModel): Long

    @Update
    suspend fun updatePlayer(playerModel: PlayerModel)

    @Delete
    suspend fun deletePlayer(playerModel: PlayerModel)

    @Query("DELETE FROM players WHERE id = :playerId")
    suspend fun deletePlayer(playerId: Long)
}
