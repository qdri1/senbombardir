package com.alimapps.senbombardir.ui.navigation

import androidx.annotation.DrawableRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.alimapps.senbombardir.R

sealed class BottomNavItem(
    val route: String,
    val label: Int,
    val iconVector: ImageVector? = null,
    @param:DrawableRes val iconRes: Int = 0,
) {
    data object Home : BottomNavItem("home", R.string.tab_home, iconRes = R.drawable.ic_games)
    data object Settings : BottomNavItem("settings", R.string.tab_settings, iconVector = Icons.Default.Settings)
}

sealed class NavigationItem(val route: String) {
    data object AddGame : NavigationItem("addGame/{gameId}") {
        fun createRoute(gameId: Long? = null) = "addGame/$gameId"
    }
    data object Game : NavigationItem("game/{gameId}") {
        fun createRoute(gameId: Long) = "game/$gameId"
    }
    data object GameResults : NavigationItem("gameResults/{gameId}") {
        fun createRoute(gameId: Long) = "gameResults/$gameId"
    }
    data object Activation : NavigationItem("activation")
}