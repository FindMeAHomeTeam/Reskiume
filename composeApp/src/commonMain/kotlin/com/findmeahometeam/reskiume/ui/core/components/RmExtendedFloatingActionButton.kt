package com.findmeahometeam.reskiume.ui.core.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.findmeahometeam.reskiume.ui.core.primaryGreen
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun RmExtendedFloatingActionButton(
    modifier: Modifier = Modifier,
    drawableResource: DrawableResource,
    text: String,
    expanded: Boolean,
    onClick: () -> Unit
) {
    ExtendedFloatingActionButton(
        modifier = Modifier.border(
            BorderStroke(1.dp, Color.Black),
            shape = RoundedCornerShape(15.dp)
        ),
        icon = {
            Icon(
                modifier = modifier.then(Modifier.size(24.dp)),
                painter = painterResource(drawableResource),
                contentDescription = null,
                tint = Color.Black
            )
        },
        text = {
            RmText(
                text = text,
                color = Color.Black
            )
        },
        containerColor = primaryGreen,
        expanded = expanded,
        onClick = rmDebouncer(onClick)
    )
}
