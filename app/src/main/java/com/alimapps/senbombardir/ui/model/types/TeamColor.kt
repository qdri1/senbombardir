package com.alimapps.senbombardir.ui.model.types

enum class TeamColor(val hexColor: String) {
    Red(hexColor = "#EC7063"),
    Blue(hexColor = "#3F51B5"),
    Green(hexColor = "#4CAF50"),
    LightGreen(hexColor = "#8BC34A"),
    Orange(hexColor = "#FF9800"),
    Yellow(hexColor = "#FFEB3B"),
    Black(hexColor = "#000000"),
    White(hexColor = "#FFFFFF"),
    Grey(hexColor = "#9E9E9E"),
    ;

    companion object {

        fun getTeamColor(
            hexColor: String,
            default: TeamColor = Red,
        ): TeamColor = entries.find { it.hexColor == hexColor } ?: default
    }
}
