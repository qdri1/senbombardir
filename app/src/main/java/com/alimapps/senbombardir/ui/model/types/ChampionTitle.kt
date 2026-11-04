package com.alimapps.senbombardir.ui.model.types

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.EventSeat
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.NorthEast
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.TheaterComedy
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Weekend
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.ui.graphics.vector.ImageVector
import com.alimapps.senbombardir.R

enum class ChampionTitle(
    val stringRes: Int,
    val descriptionRes: Int,
    val icon: ImageVector,
    val accentHex: String,
) {
    MatchKing(R.string.champion_title_match_king, R.string.champion_title_match_king_description, Icons.Filled.WorkspacePremium, "#FFC83D"),
    Sniper(R.string.champion_title_sniper, R.string.champion_title_sniper_description, Icons.Filled.GpsFixed, "#FF5A5F"),
    Playmaker(R.string.champion_title_playmaker, R.string.champion_title_playmaker_description, Icons.Filled.AutoFixHigh, "#4FC3F7"),
    Wall(R.string.champion_title_wall, R.string.champion_title_wall_description, Icons.Filled.Shield, "#7E8CE0"),
    Pitbull(R.string.champion_title_pitbull, R.string.champion_title_pitbull_description, Icons.Filled.Bolt, "#FF8A3D"),
    Magician(R.string.champion_title_magician, R.string.champion_title_magician_description, Icons.Filled.AutoAwesome, "#C77DFF"),
    Maestro(R.string.champion_title_maestro, R.string.champion_title_maestro_description, Icons.Filled.MusicNote, "#2EC4B6"),
    Cannon(R.string.champion_title_cannon, R.string.champion_title_cannon_description, Icons.Filled.LocalFireDepartment, "#FF6B35"),
    AllRounder(R.string.champion_title_all_rounder, R.string.champion_title_all_rounder_description, Icons.Filled.Stars, "#80ED99"),
    OffTarget(R.string.champion_title_off_target, R.string.champion_title_off_target_description, Icons.Filled.NorthEast, "#A0A4AB"),
    Hothead(R.string.champion_title_hothead, R.string.champion_title_hothead_description, Icons.Filled.Warning, "#E63946"),
    Workhorse(R.string.champion_title_workhorse, R.string.champion_title_workhorse_description, Icons.Filled.Construction, "#D4A373"),
    Engine(R.string.champion_title_engine, R.string.champion_title_engine_description, Icons.Filled.Speed, "#F4A261"),
    GreyCardinal(R.string.champion_title_grey_cardinal, R.string.champion_title_grey_cardinal_description, Icons.Filled.TheaterComedy, "#B8C0FF"),
    TeamPlayer(R.string.champion_title_team_player, R.string.champion_title_team_player_description, Icons.Filled.Groups, "#5DADE2"),
    Shadow(R.string.champion_title_shadow, R.string.champion_title_shadow_description, Icons.Filled.DarkMode, "#7D8597"),
    Ghost(R.string.champion_title_ghost, R.string.champion_title_ghost_description, Icons.Filled.VisibilityOff, "#8D99AE"),
    Tourist(R.string.champion_title_tourist, R.string.champion_title_tourist_description, Icons.Filled.PhotoCamera, "#48CAE4"),
    Spectator(R.string.champion_title_spectator, R.string.champion_title_spectator_description, Icons.Filled.EventSeat, "#90BE6D"),
    Furniture(R.string.champion_title_furniture, R.string.champion_title_furniture_description, Icons.Filled.Weekend, "#BC8A5F"),
    Statue(R.string.champion_title_statue, R.string.champion_title_statue_description, Icons.Filled.Accessibility, "#ADB5BD"),
    ;

    companion object {

        val activeFallbacks = listOf(Workhorse, Engine, GreyCardinal, TeamPlayer, Shadow)

        val emptyFallbacks = listOf(Ghost, Tourist, Spectator, Furniture, Statue)
    }
}
