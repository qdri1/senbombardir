package com.alimapps.senbombardir.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.alimapps.senbombardir.R

sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: Int,
) {
    data object Home : BottomNavItem("home", Icons.Default.Home, R.string.tab_home)
    data object News : BottomNavItem("news", Icons.Default.List, R.string.tab_news)
    data object Settings : BottomNavItem("settings", Icons.Default.Settings, R.string.tab_settings)
}

sealed class NavigationItem(val route: String) {
    data object AddGame : NavigationItem("addGame/{gameId}") {
        fun createRoute(gameId: Long? = null) = "addGame/$gameId"
    }
    data object Game : NavigationItem("game/{gameId}") {
        fun createRoute(gameId: Long) = "game/$gameId"
    }
}