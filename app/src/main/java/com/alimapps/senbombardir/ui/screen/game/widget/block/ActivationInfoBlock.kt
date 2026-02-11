package com.alimapps.senbombardir.ui.screen.game.widget.block

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.alimapps.senbombardir.R
import com.alimapps.senbombardir.ui.screen.game.GameAction
import com.alimapps.senbombardir.ui.utils.parseHexColor

@Composable
fun ActivationInfoBlock(
    onAction: (GameAction) -> Unit,
) {
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .background(parseHexColor("#FFA500"))
            .padding(vertical = 16.dp)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.End,
    ) {
        Text(
            text = stringResource(id = R.string.activation_orange_text),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            text = stringResource(id = R.string.activation_button),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable { onAction(GameAction.OnActivateClicked) }
                .padding(12.dp)
        )
    }
}