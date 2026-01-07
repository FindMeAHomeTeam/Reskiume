package com.findmeahometeam.reskiume.domain.model

import androidx.compose.ui.graphics.Color
import com.findmeahometeam.reskiume.ui.core.primaryBlue
import com.findmeahometeam.reskiume.ui.core.primaryGreen
import com.findmeahometeam.reskiume.ui.core.primaryRed
import com.findmeahometeam.reskiume.ui.core.secondaryBlue
import com.findmeahometeam.reskiume.ui.core.secondaryRed
import com.findmeahometeam.reskiume.ui.core.tertiaryGreen
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import reskiume.composeapp.generated.resources.Res
import reskiume.composeapp.generated.resources.ic_care
import reskiume.composeapp.generated.resources.ic_foster_homes
import reskiume.composeapp.generated.resources.ic_rescue_events

data class Advice(
    val title: StringResource,
    val description: StringResource,
    val image: AdviceImage = AdviceImage.RESCUE,
    val authorId: String? = null
)

enum class AdviceImage(val backgroundColor: Color, val icon: DrawableResource, val iconColor: Color) {
    RESCUE(
        backgroundColor = secondaryRed,
        icon = Res.drawable.ic_rescue_events,
        iconColor = primaryRed
    ),
    REHOME(
        backgroundColor = tertiaryGreen,
        icon = Res.drawable.ic_foster_homes,
        iconColor = primaryGreen
    ),
    CARE(
        backgroundColor = secondaryBlue,
        icon = Res.drawable.ic_care,
        iconColor = primaryBlue
    )
}

fun String.toAdviceImage(): AdviceImage {
    return when (this) {
        "RESCUE" -> AdviceImage.RESCUE
        "REHOME" -> AdviceImage.REHOME
        "CARE" -> AdviceImage.CARE
        else -> AdviceImage.RESCUE
    }
}
