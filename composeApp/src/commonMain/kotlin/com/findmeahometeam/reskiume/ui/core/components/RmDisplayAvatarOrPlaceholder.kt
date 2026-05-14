package com.findmeahometeam.reskiume.ui.core.components

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.findmeahometeam.reskiume.ui.core.primaryGreen
import org.jetbrains.compose.resources.painterResource
import reskiume.composeapp.generated.resources.Res
import reskiume.composeapp.generated.resources.reskiume

@Composable
fun RmDisplayAvatarOrPlaceholder(
    modifier: Modifier = Modifier,
    avatar: String,
    avatarSize: Dp = 32.dp,
    contentDescription: String,
    clipShape: Shape = CircleShape,
    onClick: () -> Unit= {}
) {
    if (avatar.isBlank()) {
        Icon(
            modifier = modifier
                .size(avatarSize)
                .debouncedClickable {
                    onClick()
                },
            painter = painterResource(Res.drawable.reskiume),
            contentDescription = contentDescription,
            tint = primaryGreen
        )
    } else {
        RmImage(
            modifier = modifier
                .size(avatarSize)
                .clip(clipShape)
                .debouncedClickable {
                    onClick()
                },
            imagePath = avatar,
            contentDescription = contentDescription
        )
    }
}
