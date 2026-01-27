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
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.findmeahometeam.reskiume.domain.model.Gender
import com.findmeahometeam.reskiume.domain.model.fosterHome.AcceptedNonHumanAnimalGenderForFosterHome
import com.findmeahometeam.reskiume.domain.model.fosterHome.AcceptedNonHumanAnimalTypeForFosterHome
import com.findmeahometeam.reskiume.domain.model.fosterHome.ResidentNonHumanAnimalForFosterHome
import com.findmeahometeam.reskiume.domain.model.toStringResource
import com.findmeahometeam.reskiume.ui.core.backgroundColorForItems
import com.findmeahometeam.reskiume.ui.core.primaryBlue
import com.findmeahometeam.reskiume.ui.core.primaryGreen
import com.findmeahometeam.reskiume.ui.core.primaryPink
import com.findmeahometeam.reskiume.ui.core.tertiaryGreen
import org.jetbrains.compose.resources.stringResource
import reskiume.composeapp.generated.resources.Res
import reskiume.composeapp.generated.resources.foster_home_list_item_accepted_more_non_human_animal
import reskiume.composeapp.generated.resources.foster_home_list_item_accepted_non_human_animal
import reskiume.composeapp.generated.resources.foster_home_list_item_residents_non_human_animal

@Composable
fun RmFosterHomeListItem(
    modifier: Modifier = Modifier,
    title: String,
    imageUrl: String,
    allAcceptedNonHumanAnimalTypes: List<AcceptedNonHumanAnimalTypeForFosterHome>,
    allAcceptedNonHumanAnimalGenders: List<AcceptedNonHumanAnimalGenderForFosterHome>,
    allResidentNonHumanAnimalForFosterHome: List<ResidentNonHumanAnimalForFosterHome>,
    distance: Double?,
    city: String,
    isEnabled: Boolean = true,
    containerColor: Color = backgroundColorForItems,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier,
        enabled = isEnabled,
        colors = CardDefaults.cardColors().copy(containerColor = containerColor),
        onClick = onClick
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
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(5.dp))

                    RmText(
                        modifier = Modifier.fillMaxWidth(),
                        text = if (distance == null) city else "$distance · $city",
                        color = Color.White
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
                    val genderColor = getGenderColor(allAcceptedNonHumanAnimalGenders)
                    RmText(
                        text = stringResource(
                            resource = Res.string.foster_home_list_item_accepted_non_human_animal,
                            if (allAcceptedNonHumanAnimalGenders.size == 1) {
                                val gender =
                                    stringResource(allAcceptedNonHumanAnimalGenders[0].acceptedNonHumanAnimalGender.toStringResource())
                                gender
                                    .substring(3)
                                    .toLowerCase(Locale.current)
                            } else {
                                ""
                            }
                        ),
                        color = genderColor
                    )
                    ListAcceptedNonHumanAnimals(
                        allAcceptedNonHumanAnimalTypes = allAcceptedNonHumanAnimalTypes,
                        color = genderColor
                    )
                }

                if (allResidentNonHumanAnimalForFosterHome.isNotEmpty()) {

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
                            text = stringResource(Res.string.foster_home_list_item_residents_non_human_animal),
                            color = Color.Black
                        )
                        ListResidentNonHumanAnimals(allResidentNonHumanAnimalForFosterHome)
                    }
                }
            }
        }
    }
}

private fun getGenderColor(allAcceptedNonHumanAnimalGenders: List<AcceptedNonHumanAnimalGenderForFosterHome>): Color {
    return if (allAcceptedNonHumanAnimalGenders.size == 1) {
        if (allAcceptedNonHumanAnimalGenders[0].acceptedNonHumanAnimalGender == Gender.FEMALE) {
            primaryPink
        } else {
            primaryBlue
        }
    } else {
        Color.Black
    }
}

@Composable
private fun ListAcceptedNonHumanAnimals(
    allAcceptedNonHumanAnimalTypes: List<AcceptedNonHumanAnimalTypeForFosterHome>,
    color: Color
) {

    allAcceptedNonHumanAnimalTypes.forEachIndexed { index, accepted ->
        if (index <= 2) {
            Spacer(modifier = Modifier.height(5.dp))
            RmText(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(accepted.acceptedNonHumanAnimalType.toStringResource()),
                fontSize = 16.sp,
                color = color
            )
        }
    }
    if (allAcceptedNonHumanAnimalTypes.size > 3) {
        Spacer(modifier = Modifier.height(5.dp))
        RmText(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(Res.string.foster_home_list_item_accepted_more_non_human_animal),
            fontSize = 16.sp,
            color = color
        )
    }
}

@Composable
private fun ListResidentNonHumanAnimals(allResidentNonHumanAnimalForFosterHome: List<ResidentNonHumanAnimalForFosterHome>) {

    allResidentNonHumanAnimalForFosterHome.forEachIndexed { index, resident ->
        if (index < 3) {
            Spacer(modifier = Modifier.height(5.dp))
            RmText(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(resident.residentNonHumanAnimal!!.nonHumanAnimalType.toStringResource())
                    .take(2) + " " + resident.residentNonHumanAnimal.name,
                fontSize = 16.sp,
                color = Color.Black
            )
        }
    }
    if (allResidentNonHumanAnimalForFosterHome.size >= 3) {
        Spacer(modifier = Modifier.height(5.dp))
        RmText(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(Res.string.foster_home_list_item_accepted_more_non_human_animal),
            fontSize = 16.sp,
            color = Color.Black
        )
    }
}
