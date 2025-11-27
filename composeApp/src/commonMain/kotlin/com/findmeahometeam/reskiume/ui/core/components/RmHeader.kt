package com.findmeahometeam.reskiume.ui.core.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.findmeahometeam.reskiume.domain.model.User
import com.findmeahometeam.reskiume.ui.core.gray
import com.findmeahometeam.reskiume.ui.core.primaryGreen
import com.findmeahometeam.reskiume.ui.core.secondaryGreen
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import reskiume.composeapp.generated.resources.Res
import reskiume.composeapp.generated.resources.ic_indicator
import reskiume.composeapp.generated.resources.profile_screen_activist_title
import reskiume.composeapp.generated.resources.profile_screen_available_label
import reskiume.composeapp.generated.resources.profile_screen_indicator_content_description
import reskiume.composeapp.generated.resources.profile_screen_profile_image_content_description
import reskiume.composeapp.generated.resources.profile_screen_unavailable_label
import reskiume.composeapp.generated.resources.reskiume

@Composable
fun RmHeader(user: User?, displayDescription: Boolean = false) {
    if (user?.image?.isNotBlank() == true) {
        AsyncImage(
            model = user.image,
            contentDescription =
                stringResource(Res.string.profile_screen_profile_image_content_description),
            modifier = Modifier.size(190.dp).clip(CircleShape),
            contentScale = ContentScale.Crop
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

    if (user != null && user.isAvailable) {
        Availability(stringResource(Res.string.profile_screen_available_label), secondaryGreen)
    } else if (user != null) {
        Availability(stringResource(Res.string.profile_screen_unavailable_label), gray)
    }

    if (displayDescription) {
        Spacer(Modifier.height(6.dp))
        RmText(
            modifier = Modifier.fillMaxWidth().padding(10.dp),
            text = user?.description ?: "",
        )
    }
}

@Composable
private fun Availability(availability: String, availabilityColor: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            modifier = Modifier.size(12.dp),
            painter = painterResource(Res.drawable.ic_indicator),
            tint = availabilityColor,
            contentDescription = stringResource(Res.string.profile_screen_indicator_content_description)
        )
        Spacer(modifier = Modifier.width(8.dp))
        RmSecondaryText(availability)
    }
}
