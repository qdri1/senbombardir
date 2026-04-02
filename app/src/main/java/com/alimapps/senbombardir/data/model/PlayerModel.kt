package com.alimapps.senbombardir.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "players",
    foreignKeys = [
        ForeignKey(
            entity = TeamModel::class,
            parentColumns = ["id"],
            childColumns = ["teamId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["teamId"]),
    ]
)
data class PlayerModel(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val teamId: Long,
    val name: String,
    val goals: Int,
    val assists: Int,
    val dribbles: Int,
    val passes: Int,
    val shots: Int,
    val saves: Int,
    val yellowCards: Int,
    val redCards: Int,
)

fun PlayerModel.toPlayerHistoryModel(playerId: Long): PlayerHistoryModel =
    PlayerHistoryModel(
        id = playerId,
        teamId = teamId,
        name = name,
        goals = goals,
        assists = assists,
        dribbles = dribbles,
        passes = passes,
        shots = shots,
        saves = saves,
        yellowCards = yellowCards,
        redCards = redCards,
    )