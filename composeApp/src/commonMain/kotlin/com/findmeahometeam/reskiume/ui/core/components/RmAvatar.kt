package com.findmeahometeam.reskiume.ui.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.findmeahometeam.reskiume.ui.core.primaryGreen
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import reskiume.composeapp.generated.resources.Res
import reskiume.composeapp.generated.resources.reskiume

sealed class RmListAvatarType {
    data class Icon(val backgroundColor: Color, val icon: DrawableResource, val iconColor: Color) :
        RmListAvatarType()

    data class Image(val resource: String) : RmListAvatarType()
}

@Composable
fun RmAvatar(listAvatarType: RmListAvatarType) {

    when (listAvatarType) {
        is RmListAvatarType.Icon ->
            Box(
                modifier = Modifier.size(55.dp)
                    .background(
                        color = listAvatarType.backgroundColor,
                        shape = RoundedCornerShape(15.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier.size(30.dp),
                    painter = painterResource(listAvatarType.icon),
                    contentDescription = null,
                    tint = listAvatarType.iconColor
                )
            }

        is RmListAvatarType.Image -> {
            if (listAvatarType.resource.isBlank()) {
                Icon(
                    modifier = Modifier.size(55.dp).clip(RoundedCornerShape(15.dp)),
                    painter = painterResource(Res.drawable.reskiume),
                    contentDescription = null,
                    tint = primaryGreen
                )
            } else {
                AsyncImage(
                    modifier = Modifier.size(55.dp).clip(RoundedCornerShape(15.dp)),
                    model = listAvatarType.resource,
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
            }
        }
    }

}