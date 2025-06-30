package com.alimapps.senbombardir.ui.model.types

enum class TeamQuantity(val quantity: Int) {
    Team2(quantity = 2),
    Team3(quantity = 3),
    Team4(quantity = 4),
    ;

    companion object {

        fun getTeamQuantity(
            quantity: Int,
            default: TeamQuantity = Team3,
        ): TeamQuantity = entries.find { it.quantity == quantity } ?: default
    }
}