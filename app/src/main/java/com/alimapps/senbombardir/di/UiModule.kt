package com.alimapps.senbombardir.di

import com.alimapps.senbombardir.ui.navigation.DefaultNavigationResultManager
import com.alimapps.senbombardir.ui.navigation.NavigationResultManager
import com.alimapps.senbombardir.ui.screen.add.game.AddGameViewModel
import com.alimapps.senbombardir.ui.screen.game.GameViewModel
import com.alimapps.senbombardir.ui.screen.home.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val uiModule = module {

    single<NavigationResultManager> {
        DefaultNavigationResultManager()
    }

    viewModel {
        HomeViewModel(
            gameRepository = get(),
            teamRepository = get(),
            playerRepository = get(),
        )
    }

    viewModel { (gameId: Long?) ->
        AddGameViewModel(
            gameId = gameId,
            gameRepository = get(),
            liveGameRepository = get(),
            teamRepository = get(),
            playerRepository = get(),
        )
    }

    viewModel { (gameId: Long) ->
        GameViewModel(
            gameId = gameId,
            gameRepository = get(),
            liveGameRepository = get(),
            teamRepository = get(),
            playerRepository = get(),
            context = get(),
        )
    }
}