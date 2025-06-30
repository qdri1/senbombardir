package com.alimapps.senbombardir.data.source

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.alimapps.senbombardir.data.model.LiveGameModel

@Dao
interface LiveGameDao {

    @Query("SELECT * FROM lives")
    suspend fun getLiveGames(): List<LiveGameModel>

    @Query("SELECT * FROM lives WHERE gameId = :gameId")
    suspend fun getLiveGame(gameId: Long): LiveGameModel?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLiveGame(liveGameModel: LiveGameModel): Long

    @Update
    suspend fun updateLiveGame(liveGameModel: LiveGameModel)
}
