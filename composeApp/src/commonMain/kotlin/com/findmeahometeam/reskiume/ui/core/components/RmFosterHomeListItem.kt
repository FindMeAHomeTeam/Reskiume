package com.findmeahometeam.reskiume.ui.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
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
import com.findmeahometeam.reskiume.domain.model.Gender
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimal
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimalType
import com.findmeahometeam.reskiume.domain.model.fosterHome.AcceptedNonHumanAnimalForFosterHome
import com.findmeahometeam.reskiume.domain.model.toEmoji
import com.findmeahometeam.reskiume.domain.model.toStringResource
import com.findmeahometeam.reskiume.ui.core.backgroundColorForItems
import com.findmeahometeam.reskiume.ui.core.lightGray
import com.findmeahometeam.reskiume.ui.core.primaryGreen
import com.findmeahometeam.reskiume.ui.core.tertiaryGreen
import org.jetbrains.compose.resources.stringResource
import reskiume.composeapp.generated.resources.Res
import reskiume.composeapp.generated.resources.check_all_foster_homes_screen_km
import reskiume.composeapp.generated.resources.foster_home_list_item_accepted_both_genders_non_human_animal
import reskiume.composeapp.generated.resources.foster_home_list_item_accepted_non_human_animal
import reskiume.composeapp.generated.resources.foster_home_list_item_disabled_foster_home
import reskiume.composeapp.generated.resources.foster_home_list_item_more_non_human_animal
import reskiume.composeapp.generated.resources.foster_home_list_item_residents_non_human_animal

@Composable
fun RmFosterHomeListItem(
    modifier: Modifier = Modifier,
    title: String,
    imageUrl: String,
    allAcceptedNonHumanAnimals: List<AcceptedNonHumanAnimalForFosterHome>,
    allResidentNonHumanAnimals: List<NonHumanAnimal>,
    distance: Double?,
    city: String,
    isEnabled: Boolean = true,
    containerColor: Color = backgroundColorForItems,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors().copy(containerColor = containerColor),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                RmAvatar(RmListAvatarType.Image(imageUrl, 120.dp))
                if (!isEnabled) {
                    Box(
                        modifier = Modifier.wrapContentSize()
                            .background(color = lightGray, shape = RoundedCornerShape(15.dp))
                            .padding(8.dp)
                    ) {
                        RmText(
                            text = stringResource(Res.string.foster_home_list_item_disabled_foster_home),
                            color = Color.Black
                        )
                    }
                }
            }
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
                                Res.string.check_all_foster_homes_screen_km,
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
                        text = stringResource(resource = Res.string.foster_home_list_item_accepted_non_human_animal),
                        color = Color.Black
                    )
                    ListAcceptedNonHumanAnimals(allAcceptedNonHumanAnimals)
                }

                if (allResidentNonHumanAnimals.isNotEmpty()) {

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
                        ListResidentNonHumanAnimals(allResidentNonHumanAnimals)
                    }
                }
            }
        }
    }
}

@Composable
private fun ListAcceptedNonHumanAnimals(allAcceptedNonHumanAnimals: List<AcceptedNonHumanAnimalForFosterHome>) {

    val nonHumanAnimalHashMap = HashMap<NonHumanAnimalType, Set<Gender>>()
    var counter = 0

    allAcceptedNonHumanAnimals.forEach { accepted ->

        nonHumanAnimalHashMap[accepted.acceptedNonHumanAnimalType] =
            nonHumanAnimalHashMap[accepted.acceptedNonHumanAnimalType]?.plus(accepted.acceptedNonHumanAnimalGender)
                ?: setOf(accepted.acceptedNonHumanAnimalGender)
    }

    nonHumanAnimalHashMap.forEach { (nonHumanAnimalType, genders) ->

        if (counter <= 2) {

            var genderText = ""
            val nonHumanAnimalText: String =
                nonHumanAnimalType.toEmoji() + " " + stringResource(nonHumanAnimalType.toStringResource())

            genders.forEach {

                val gender = stringResource(it.toStringResource()).lowercase()

                genderText = if (genderText.isBlank()) {
                    gender
                } else {
                    stringResource(
                        Res.string.foster_home_list_item_accepted_both_genders_non_human_animal,
                        genderText,
                        gender
                    )
                }
            }
            Spacer(modifier = Modifier.height(5.dp))
            RmTextBold(
                modifier = Modifier.fillMaxWidth(),
                text = "$nonHumanAnimalText: $genderText",
                textToBold = nonHumanAnimalText,
                fontSize = 16.sp,
                color = Color.Black,
                maxLines = 1
            )
        }
        counter++
    }
    if (counter > 3) {
        Spacer(modifier = Modifier.height(5.dp))
        RmText(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(Res.string.foster_home_list_item_more_non_human_animal),
            fontSize = 16.sp,
            color = Color.Black
        )
    }
}

@Composable
private fun ListResidentNonHumanAnimals(allResidentNonHumanAnimalForFosterHome: List<NonHumanAnimal>) {

    allResidentNonHumanAnimalForFosterHome.forEachIndexed { index, residentNonHumanAnimal ->
        if (index < 3) {
            Spacer(modifier = Modifier.height(5.dp))
            RmText(
                modifier = Modifier.fillMaxWidth(),
                text = residentNonHumanAnimal.nonHumanAnimalType.toEmoji() + " " + residentNonHumanAnimal.name,
                fontSize = 16.sp,
                color = Color.Black
            )
        }
    }
    if (allResidentNonHumanAnimalForFosterHome.size > 3) {
        Spacer(modifier = Modifier.height(5.dp))
        RmText(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(Res.string.foster_home_list_item_more_non_human_animal),
            fontSize = 16.sp,
            color = Color.Black,
            maxLines = 1
        )
    }
}
