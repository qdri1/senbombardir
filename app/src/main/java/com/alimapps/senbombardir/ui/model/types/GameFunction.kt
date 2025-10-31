package com.alimapps.senbombardir.ui.model.types

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.ui.graphics.vector.ImageVector
import com.alimapps.senbombardir.R

enum class GameFunction(val icon: ImageVector, val stringRes: Int) {
    BestPlayers(icon = Icons.Filled.ThumbUp, stringRes = R.string.function_best_players),
    Edit(icon = Icons.Filled.Edit, stringRes = R.string.function_edit),
    ClearResults(icon = Icons.Filled.Refresh, stringRes = R.string.function_clear_result),
    Info(icon = Icons.Filled.Info, stringRes = R.string.function_info),
    AllResults(icon = Icons.Filled.DateRange, stringRes = R.string.function_all_results),
    Delete(icon = Icons.Filled.Delete, stringRes = R.string.function_remove),
}
