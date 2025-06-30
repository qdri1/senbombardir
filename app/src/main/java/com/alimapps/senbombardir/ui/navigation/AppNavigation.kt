package com.alimapps.senbombardir.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.alimapps.senbombardir.ui.screen.add.game.AddGameScreen
import com.alimapps.senbombardir.ui.screen.game.GameScreen
import com.alimapps.senbombardir.ui.screen.home.HomeScreen
import com.alimapps.senbombardir.ui.screen.news.NewsScreen
import com.alimapps.senbombardir.ui.screen.settings.SettingsScreen
import com.alimapps.senbombardir.utils.orDefault
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val bottomNavItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.News,
        BottomNavItem.Settings,
    )

    Scaffold(
        bottomBar = {
            if (bottomNavItems.any { bottomNavItem -> bottomNavItem.route == currentRoute }) {
                NavigationBar(
                    containerColor = Color.White,
                ) {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            selected = currentRoute == item.route,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = item.route) },
                            label = { Text(text = stringResource(id = item.label)) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavHost(
                navController = navController,
                startDestination = BottomNavItem.Home.route,
                enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) + fadeIn() },
                exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }) + fadeOut() },
                popEnterTransition = { slideInHorizontally(initialOffsetX = { -1000 }) + fadeIn() },
                popExitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }) + fadeOut() },
            ) {
                composable(
                    route = BottomNavItem.Home.route,
                    enterTransition = { fadeIn(tween(300)) },
                    exitTransition = { fadeOut(tween(300)) },
                    popEnterTransition = { fadeIn(tween(300)) },
                    popExitTransition = { fadeOut(tween(300)) },
                ) {
                    HomeScreen(navController = navController)
                }
                composable(
                    route = BottomNavItem.News.route,
                    enterTransition = { fadeIn(tween(300)) },
                    exitTransition = { fadeOut(tween(300)) },
                    popEnterTransition = { fadeIn(tween(300)) },
                    popExitTransition = { fadeOut(tween(300)) },
                ) {
                    NewsScreen(navController = navController)
                }
                composable(
                    route = BottomNavItem.Settings.route,
                    enterTransition = { fadeIn(tween(300)) },
                    exitTransition = { fadeOut(tween(300)) },
                    popEnterTransition = { fadeIn(tween(300)) },
                    popExitTransition = { fadeOut(tween(300)) },
                ) {
                    SettingsScreen(navController = navController)
                }

                composable(NavigationItem.AddGame.route) { backStackEntry ->
                    val gameId = backStackEntry.arguments?.getString("gameId")?.toLongOrNull()
                    AddGameScreen(
                        navController = navController,
                        viewModel = koinViewModel { parametersOf(gameId) }
                    )
                }
                composable(NavigationItem.Game.route) { backStackEntry ->
                    val gameId = backStackEntry.arguments?.getString("gameId")?.toLongOrNull().orDefault()
                    GameScreen(
                        navController = navController,
                        viewModel = koinViewModel { parametersOf(gameId) }
                    )
                }
            }
        }
    }
}