package com.alimapps.senbombardir.ui.model.types

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.ui.graphics.vector.ImageVector
import com.alimapps.senbombardir.R

enum class GameResultsFunction(val icon: ImageVector, val stringRes: Int) {
    BestPlayers(icon = Icons.Filled.ThumbUp, stringRes = R.string.function_best_players_all_games),
    ClearResults(icon = Icons.Filled.Refresh, stringRes = R.string.function_clear_result_all_games),
}
