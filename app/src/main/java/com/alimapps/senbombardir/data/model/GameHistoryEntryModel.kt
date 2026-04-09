package com.alimapps.senbombardir.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "game_history_entries",
    foreignKeys = [
        ForeignKey(
            entity = GameModel::class,
            parentColumns = ["id"],
            childColumns = ["gameId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index(value = ["gameId"])],
)
data class GameHistoryEntryModel(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val gameId: Long,
    val gameNumber: Int,
    val leftTeamName: String,
    val leftTeamColor: String,
    val leftTeamGoals: Int,
    val rightTeamName: String,
    val rightTeamColor: String,
    val rightTeamGoals: Int,
    val winnerTeamName: String,
    val durationSeconds: Int,
)
