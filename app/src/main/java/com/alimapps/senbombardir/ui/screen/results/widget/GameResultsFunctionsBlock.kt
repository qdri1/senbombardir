package com.alimapps.senbombardir.ui.screen.results.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import com.alimapps.senbombardir.R
import com.alimapps.senbombardir.ui.model.types.GameResultsFunction
import com.alimapps.senbombardir.ui.screen.results.GameResultsAction

@Composable
fun GameResultsFunctionsBlock(
    uiLimited: Boolean,
    onAction: (GameResultsAction) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
            .background(MaterialTheme.colorScheme.surface)
            .padding(vertical = 12.dp)
            .padding(horizontal = 12.dp),
    ) {
        GameResultsFunction.entries.forEach { function ->
            val isFunctionLimited = function == GameResultsFunction.BestPlayers && uiLimited
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                        if (isFunctionLimited) {
                            onAction(GameResultsAction.OnActivateClicked)
                        } else {
                            onAction(GameResultsAction.OnFunctionClicked(function))
                        }
                    }
                    .padding(12.dp)
            ) {
                val color = if (isFunctionLimited) {
                    MaterialTheme.colorScheme.outline
                } else {
                    MaterialTheme.colorScheme.secondary
                }
                Icon(
                    imageVector = function.icon,
                    contentDescription = "GameScreenEditIcon",
                    tint = color,
                )
                Text(
                    text = stringResource(id = function.stringRes),
                    color = color,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.weight(1f),
                )
                if (isFunctionLimited) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_lock),
                        contentDescription = "GameScreenLockIcon",
                        tint = MaterialTheme.colorScheme.outline,
                    )
                }
            }
        }
    }
}
