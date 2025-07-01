package com.alimapps.senbombardir.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "players_history",
    foreignKeys = [
        ForeignKey(
            entity = TeamHistoryModel::class,
            parentColumns = ["id"],
            childColumns = ["teamId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["teamId"]),
    ]
)
data class PlayerHistoryModel(
    @PrimaryKey
    val id: Long,
    val teamId: Long,
    val name: String,
    val goals: Int,
    val assists: Int,
    val dribbles: Int,
    val passes: Int,
    val shots: Int,
    val saves: Int,
)