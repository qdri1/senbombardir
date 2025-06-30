package com.alimapps.senbombardir.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "teams",
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
data class TeamModel(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val gameId: Long,
    val name: String,
    val color: String,
    val games: Int,
    val wins: Int,
    val draws: Int,
    val loses: Int,
    val goals: Int,
    val conceded: Int,
    val points: Int,
)