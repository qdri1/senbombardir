package com.alimapps.senbombardir.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "lives",
    foreignKeys = [
        ForeignKey(
            entity = GameModel::class,
            parentColumns = ["id"],
            childColumns = ["gameId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["gameId"]),
    ]
)
data class LiveGameModel(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val gameId: Long,
    val leftTeamId: Long,
    val leftTeamName: String,
    val leftTeamColor: String,
    val leftTeamGoals: Int,
    val leftTeamWinCount: Int,
    val rightTeamId: Long,
    val rightTeamName: String,
    val rightTeamColor: String,
    val rightTeamGoals: Int,
    val rightTeamWinCount: Int,
    val gameCount: Int,
    val isLive: Boolean,
)