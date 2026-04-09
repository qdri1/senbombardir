package com.alimapps.senbombardir.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "game_history_action_events",
    foreignKeys = [
        ForeignKey(
            entity = GameHistoryEntryModel::class,
            parentColumns = ["id"],
            childColumns = ["historyEntryId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index(value = ["historyEntryId"])],
)
data class GameHistoryActionEventModel(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val historyEntryId: Long,
    val teamName: String,
    val teamColor: String,
    val playerName: String,
    val actionType: String,
    val elapsedSeconds: Int,
)
