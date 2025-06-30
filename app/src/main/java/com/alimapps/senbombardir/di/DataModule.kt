package com.alimapps.senbombardir.di

import android.content.Context
import androidx.room.Room
import com.alimapps.senbombardir.data.repository.GameRepository
import com.alimapps.senbombardir.data.repository.LiveGameRepository
import com.alimapps.senbombardir.data.repository.PlayerRepository
import com.alimapps.senbombardir.data.repository.TeamRepository
import com.alimapps.senbombardir.data.source.AppDatabase
import com.alimapps.senbombardir.data.source.GameDao
import com.alimapps.senbombardir.data.source.LiveGameDao
import com.alimapps.senbombardir.data.source.PlayerDao
import com.alimapps.senbombardir.data.source.Prefs
import com.alimapps.senbombardir.data.source.TeamDao
import org.koin.dsl.module

val dataModule = module {

    single {
        Room.databaseBuilder(
            context = get(),
            klass = AppDatabase::class.java,
            name = "sen_bombardir_database",
        )
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .addMigrations(*AppDatabase.MIGRATIONS)
            .build()
    }

    single<GameDao> { get<AppDatabase>().gameDao() }
    single<LiveGameDao> { get<AppDatabase>().liveGameDao() }
    single<TeamDao> { get<AppDatabase>().teamDao() }
    single<PlayerDao> { get<AppDatabase>().playerDao() }

    single { Prefs(preferences = get<Context>().getSharedPreferences("PREFS", Context.MODE_PRIVATE)) }

    single {
        GameRepository(gameDao = get())
    }

    single {
        LiveGameRepository(liveGameDao = get(), prefs = get())
    }

    single {
        TeamRepository(teamDao = get())
    }

    single {
        PlayerRepository(teamDao = get(), playerDao = get())
    }
}