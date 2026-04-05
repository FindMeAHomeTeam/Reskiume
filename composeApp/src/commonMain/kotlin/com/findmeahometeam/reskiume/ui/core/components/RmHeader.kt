package com.findmeahometeam.reskiume.ui.core.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.findmeahometeam.reskiume.domain.model.user.User
import com.findmeahometeam.reskiume.ui.core.primaryGreen
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import reskiume.composeapp.generated.resources.Res
import reskiume.composeapp.generated.resources.profile_screen_activist_title
import reskiume.composeapp.generated.resources.profile_screen_profile_image_content_description
import reskiume.composeapp.generated.resources.reskiume

@Composable
fun RmHeader(user: User?, displayDescription: Boolean = false) {
    if (user?.image?.isNotBlank() == true) {
        RmImage(
            imagePath = user.image,
            contentDescription =
                stringResource(Res.string.profile_screen_profile_image_content_description),
            modifier = Modifier.size(190.dp).clip(CircleShape)
        )
    } else {
        Icon(
            modifier = Modifier.size(190.dp),
            painter = painterResource(Res.drawable.reskiume),
            contentDescription =
                stringResource(Res.string.profile_screen_profile_image_content_description),
            tint = primaryGreen
        )
    }
    RmText(
        modifier = Modifier.fillMaxWidth().padding(10.dp),
        text = if (user?.username.isNullOrBlank()) stringResource(Res.string.profile_screen_activist_title) else user.username,
        textAlign = TextAlign.Center,
        fontSize = 24.sp,
        fontWeight = FontWeight.Black
    )
    Spacer(Modifier.height(16.dp))

    if (displayDescription) {
        Spacer(Modifier.height(6.dp))
        RmText(
            modifier = Modifier.fillMaxWidth().padding(10.dp),
            text = user?.description ?: "",
        )
    }
}
