package com.alimapps.senbombardir.ui.screen.game.widget.block

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.alimapps.senbombardir.R
import com.alimapps.senbombardir.ui.model.types.GameSounds
import com.alimapps.senbombardir.ui.screen.game.GameAction

@Composable
fun SoundsBlock(
    uiLimited: Boolean,
    onAction: (GameAction) -> Unit,
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        items(GameSounds.entries) { gameSound ->
            val isLocked = uiLimited &&
                gameSound != GameSounds.DopTep &&
                gameSound != GameSounds.Whistle &&
                gameSound != GameSounds.StartMatch

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .clickable {
                        if (isLocked) {
                            onAction(GameAction.OnActivateClicked)
                        } else {
                            onAction(GameAction.OnSoundClicked(gameSound))
                        }
                    },
            ) {
                Text(
                    text = stringResource(id = gameSound.stringRes),
                    color = if (isLocked) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                )
                if (isLocked) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_lock),
                        contentDescription = "GameScreenLockIcon",
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp)
                            .size(10.dp),
                    )
                }
            }
        }
    }
}
