package com.alimapps.senbombardir.ui.screen.add.game

import com.alimapps.senbombardir.ui.model.types.GameFormat
import com.alimapps.senbombardir.ui.model.types.GameRule
import com.alimapps.senbombardir.ui.model.types.TeamColor
import com.alimapps.senbombardir.ui.model.types.TeamQuantity

sealed interface AddGameAction {
    data object CloseScreen : AddGameAction
    class OnGameTextValueChanged(val value: String) : AddGameAction
    class OnTimeTextValueChanged(val value: String) : AddGameAction
    class OnGameFormatSelected(val format: GameFormat) : AddGameAction
    class OnTeamQuantitySelected(val teamQuantity: TeamQuantity) : AddGameAction
    class OnGameRuleSelected(val rule: GameRule) : AddGameAction
    class OnTeamTabClicked(val tabIndex: Int) : AddGameAction
    data object OnTeamColorClicked : AddGameAction
    class OnTeamColorSelected(val color: TeamColor) : AddGameAction
    class OnTeamNameValueChanged(val tabIndex: Int, val value: String) : AddGameAction
    class OnPlayerNameValueChanged(val tabIndex: Int, val fieldIndex: Int, val value: String) : AddGameAction
    class OnAddPlayerClicked(val tabIndex: Int) : AddGameAction
    data object OnFinishClicked : AddGameAction
}