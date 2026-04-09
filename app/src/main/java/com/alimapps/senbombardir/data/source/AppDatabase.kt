package com.alimapps.senbombardir.data.source

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.alimapps.senbombardir.data.model.GameHistoryActionEventModel
import com.alimapps.senbombardir.data.model.GameHistoryEntryModel
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
        GameHistoryEntryModel::class,
        GameHistoryActionEventModel::class,
    ],
    version = 5,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun gameDao(): GameDao
    abstract fun liveGameDao(): LiveGameDao
    abstract fun teamDao(): TeamDao
    abstract fun playerDao(): PlayerDao
    abstract fun teamHistoryDao(): TeamHistoryDao
    abstract fun playerHistoryDao(): PlayerHistoryDao
    abstract fun gameHistoryDao(): GameHistoryDao

    companion object {
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE games ADD COLUMN modifiedAt INTEGER NOT NULL DEFAULT 0")
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE players ADD COLUMN yellowCards INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE players ADD COLUMN redCards INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE players_history ADD COLUMN yellowCards INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE players_history ADD COLUMN redCards INTEGER NOT NULL DEFAULT 0")
            }
        }

        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `game_history_entries` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `gameId` INTEGER NOT NULL,
                        `gameNumber` INTEGER NOT NULL,
                        `leftTeamName` TEXT NOT NULL,
                        `leftTeamColor` TEXT NOT NULL,
                        `leftTeamGoals` INTEGER NOT NULL,
                        `rightTeamName` TEXT NOT NULL,
                        `rightTeamColor` TEXT NOT NULL,
                        `rightTeamGoals` INTEGER NOT NULL,
                        `winnerTeamName` TEXT NOT NULL,
                        `durationSeconds` INTEGER NOT NULL DEFAULT 0,
                        FOREIGN KEY(`gameId`) REFERENCES `games`(`id`) ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_game_history_entries_gameId` ON `game_history_entries` (`gameId`)")
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `game_history_action_events` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `historyEntryId` INTEGER NOT NULL,
                        `teamName` TEXT NOT NULL,
                        `teamColor` TEXT NOT NULL,
                        `playerName` TEXT NOT NULL,
                        `actionType` TEXT NOT NULL,
                        `elapsedSeconds` INTEGER NOT NULL,
                        FOREIGN KEY(`historyEntryId`) REFERENCES `game_history_entries`(`id`) ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_game_history_action_events_historyEntryId` ON `game_history_action_events` (`historyEntryId`)")
            }
        }

        val MIGRATIONS = arrayOf(MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
    }
}