package com.alimapps.senbombardir.ui.model.types

import com.alimapps.senbombardir.R

enum class PitchSurface(val stringRes: Int) {
    Grass(stringRes = R.string.pitch_surface_grass),
    Parquet(stringRes = R.string.pitch_surface_parquet),
    ;

    companion object {

        fun from(name: String?): PitchSurface = entries.find { it.name == name } ?: Grass
    }
}
