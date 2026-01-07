package com.findmeahometeam.reskiume.ui.core.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.findmeahometeam.reskiume.ui.core.backgroundColorForItems

@Composable
fun RmListItem(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    isEnabled: Boolean = true,
    containerColor: Color = backgroundColorForItems,
    listAvatarType: RmListAvatarType,
    descriptionMaxLines: Int = 3,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier,
        enabled = isEnabled,
        colors = CardDefaults.cardColors().copy(containerColor = containerColor),
        onClick = { onClick() }
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            RmAvatar(listAvatarType)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.fillMaxWidth()) {
                RmText(
                    modifier = Modifier.fillMaxWidth(),
                    text = title,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(5.dp))
                RmSecondaryText(
                    modifier = Modifier.fillMaxWidth(),
                    text = description,
                    maxLines = descriptionMaxLines
                )
            }
        }
    }
}

