package com.alimapps.senbombardir.data.source

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.alimapps.senbombardir.data.model.GameModel

@Dao
interface GameDao {

    @Query("SELECT * FROM games")
    suspend fun getGames(): List<GameModel>

    @Query("SELECT * FROM games WHERE id = :gameId")
    suspend fun getGame(gameId: Long): GameModel?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGame(gameModel: GameModel): Long

    @Update
    suspend fun updateGame(gameModel: GameModel)

    @Delete
    suspend fun deleteGame(gameModel: GameModel)

    @Query("DELETE FROM games WHERE id = :gameId")
    suspend fun deleteGame(gameId: Long)
}
