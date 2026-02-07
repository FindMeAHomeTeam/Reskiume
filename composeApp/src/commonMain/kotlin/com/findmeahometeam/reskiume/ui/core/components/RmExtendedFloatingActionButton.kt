package com.findmeahometeam.reskiume.ui.core.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.findmeahometeam.reskiume.ui.core.primaryGreen
import com.findmeahometeam.reskiume.ui.core.tertiaryGreen
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun RmExtendedFloatingActionButton(
    modifier: Modifier = Modifier,
    drawableResource: DrawableResource,
    text: String,
    onClick: () -> Unit
) {
    ExtendedFloatingActionButton(
        icon = {
            Icon(
                modifier = modifier.then(Modifier.size(24.dp)),
                painter = painterResource(drawableResource),
                contentDescription = null,
                tint = primaryGreen
            )
        },
        text = {
            RmText(
                text = text,
                color = Color.Black
            )
        },
        containerColor = tertiaryGreen,
        onClick = onClick
    )
}
