package com.alimapps.senbombardir.data.source

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import com.alimapps.senbombardir.data.model.GameModel
import com.alimapps.senbombardir.data.model.LiveGameModel
import com.alimapps.senbombardir.data.model.PlayerHistoryModel
import com.alimapps.senbombardir.data.model.PlayerModel
import com.alimapps.senbombardir.data.model.TeamHistoryModel
import com.alimapps.senbombardir.data.model.TeamModel

@Database(
    entities = [
        GameModel::class,
        LiveGameModel::class,
        TeamModel::class,
        PlayerModel::class,
        TeamHistoryModel::class,
        PlayerHistoryModel::class,
    ],
    version = 2,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun gameDao(): GameDao
    abstract fun liveGameDao(): LiveGameDao
    abstract fun teamDao(): TeamDao
    abstract fun playerDao(): PlayerDao
    abstract fun teamHistoryDao(): TeamHistoryDao
    abstract fun playerHistoryDao(): PlayerHistoryDao

    companion object {
        val MIGRATIONS = arrayOf<Migration>()
    }
}