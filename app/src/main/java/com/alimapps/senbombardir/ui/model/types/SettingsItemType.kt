package com.alimapps.senbombardir.ui.model.types

import com.alimapps.senbombardir.R

enum class SettingsItemType(val iconRes: Int, val stringRes: Int) {
    Share(iconRes = R.drawable.ic_share, stringRes = R.string.settings_item_share),
    Evaluate(iconRes = R.drawable.ic_star, stringRes = R.string.settings_item_star),
    Telegram(iconRes = R.drawable.ic_telegram, stringRes = R.string.settings_item_telegram),
}