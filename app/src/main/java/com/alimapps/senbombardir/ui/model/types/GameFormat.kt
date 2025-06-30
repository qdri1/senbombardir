package com.alimapps.senbombardir.ui.model.types

enum class GameFormat(val format: String, val playerQuantity: Int) {
    Format4x4(format = "4x4", playerQuantity = 4),
    Format5x5(format = "5x5", playerQuantity = 5),
    Format6x6(format = "6x6", playerQuantity = 6),
    Format7x7(format = "7x7", playerQuantity = 7),
    Format8x8(format = "8x8", playerQuantity = 8),
    Format9x9(format = "9x9", playerQuantity = 9),
    Format10x10(format = "10x10", playerQuantity = 10),
    Format11x11(format = "11x11", playerQuantity = 11),
    ;

    companion object {

        fun getGameFormat(
            format: String,
            default: GameFormat = Format5x5,
        ): GameFormat = entries.find { it.format == format } ?: default
    }
}