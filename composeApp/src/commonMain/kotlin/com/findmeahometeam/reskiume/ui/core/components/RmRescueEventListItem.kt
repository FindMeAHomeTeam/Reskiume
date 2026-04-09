package com.findmeahometeam.reskiume.ui.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimal
import com.findmeahometeam.reskiume.domain.model.rescueEvent.NeedToCover
import com.findmeahometeam.reskiume.domain.model.rescueEvent.toStringResource
import com.findmeahometeam.reskiume.domain.model.toEmoji
import com.findmeahometeam.reskiume.domain.model.toStringResource
import com.findmeahometeam.reskiume.ui.core.backgroundColorForItems
import com.findmeahometeam.reskiume.ui.core.primaryGreen
import com.findmeahometeam.reskiume.ui.core.tertiaryGreen
import org.jetbrains.compose.resources.stringResource
import reskiume.composeapp.generated.resources.Res
import reskiume.composeapp.generated.resources.rescue_event_list_item_km
import reskiume.composeapp.generated.resources.rescue_event_list_item_more_non_human_animal
import reskiume.composeapp.generated.resources.rescue_event_list_item_needs_to_cover
import reskiume.composeapp.generated.resources.rescue_event_list_item_non_human_animal_to_rescue

@Composable
fun RmRescueEventListItem(
    modifier: Modifier = Modifier,
    title: String,
    imageUrl: String,
    allNonHumanAnimalsToRescue: List<NonHumanAnimal>,
    allNeedsToCover: List<NeedToCover>,
    distance: Double?,
    city: String,
    containerColor: Color = backgroundColorForItems,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors().copy(containerColor = containerColor),
        onClick = rmDebouncer(onClick)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            RmAvatar(RmListAvatarType.Image(imageUrl, 120.dp))
            Spacer(modifier = Modifier.width(5.dp))

            Column(modifier = Modifier.fillMaxWidth()) {

                Column(
                    modifier = Modifier
                        .background(
                            color = primaryGreen,
                            shape = RoundedCornerShape(15.dp)
                        ).padding(8.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center
                ) {
                    RmText(
                        modifier = Modifier.fillMaxWidth(),
                        text = title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(5.dp))

                    RmText(
                        modifier = Modifier.fillMaxWidth(),
                        text = if (distance == null) {
                            city
                        } else {
                            stringResource(
                                Res.string.rescue_event_list_item_km,
                                distance,
                                city
                            )
                        },
                        color = Color.White,
                        maxLines = 1
                    )
                }
                Spacer(modifier = Modifier.height(5.dp))

                Column(
                    modifier = Modifier
                        .background(
                            color = tertiaryGreen,
                            shape = RoundedCornerShape(15.dp)
                        ).padding(8.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center
                ) {
                    RmText(
                        text = stringResource(Res.string.rescue_event_list_item_non_human_animal_to_rescue),
                        color = Color.Black
                    )
                    ListNonHumanAnimalsToRescue(allNonHumanAnimalsToRescue)
                }
                Spacer(modifier = Modifier.height(5.dp))

                Column(
                    modifier = Modifier
                        .background(
                            color = tertiaryGreen,
                            shape = RoundedCornerShape(15.dp)
                        ).padding(8.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center
                ) {
                    RmText(
                        text = stringResource(resource = Res.string.rescue_event_list_item_needs_to_cover),
                        color = Color.Black
                    )
                    ListNeedsToCover(allNeedsToCover)
                }
            }
        }
    }
}

@Composable
private fun ListNonHumanAnimalsToRescue(allNonHumanAnimalsToRescueForRescueEvent: List<NonHumanAnimal>) {

    allNonHumanAnimalsToRescueForRescueEvent.forEachIndexed { index, residentNonHumanAnimal ->
        if (index < 3) {
            Spacer(modifier = Modifier.height(5.dp))
            RmText(
                modifier = Modifier.fillMaxWidth(),
                text = residentNonHumanAnimal.nonHumanAnimalType.toEmoji()
                        + " " + residentNonHumanAnimal.name
                        + " · " + stringResource(residentNonHumanAnimal.nonHumanAnimalType.toStringResource()).lowercase(),
                fontSize = 16.sp,
                color = Color.Black
            )
        }
    }
    if (allNonHumanAnimalsToRescueForRescueEvent.size > 3) {
        Spacer(modifier = Modifier.height(5.dp))
        RmText(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(Res.string.rescue_event_list_item_more_non_human_animal),
            fontSize = 16.sp,
            color = Color.Black,
            maxLines = 1
        )
    }
}

@Composable
private fun ListNeedsToCover(allNeedsToCover: List<NeedToCover>) {

    allNeedsToCover.forEach { needToCover ->

        val rescueNeed: String = stringResource(needToCover.rescueNeed.toStringResource())

        Spacer(modifier = Modifier.height(5.dp))
        RmTextBold(
            modifier = Modifier.fillMaxWidth(),
            text = rescueNeed,
            textToBold = rescueNeed,
            fontSize = 16.sp,
            color = Color.Black,
            maxLines = 1
        )
    }
}
