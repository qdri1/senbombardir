package com.alimapps.senbombardir.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "games",
)
data class GameModel(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val format: String,
    val teamQuantity: Int,
    val rule: String,
    val timeInMinutes: Int,
)