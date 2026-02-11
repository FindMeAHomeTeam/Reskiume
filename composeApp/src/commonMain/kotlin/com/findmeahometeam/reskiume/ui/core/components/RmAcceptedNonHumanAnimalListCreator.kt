package com.findmeahometeam.reskiume.ui.core.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.findmeahometeam.reskiume.domain.model.Gender
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimalType
import com.findmeahometeam.reskiume.domain.model.fosterHome.AcceptedNonHumanAnimalForFosterHome
import com.findmeahometeam.reskiume.domain.model.toEmoji
import com.findmeahometeam.reskiume.domain.model.toStringResource
import com.findmeahometeam.reskiume.ui.core.backgroundColorForItems
import com.findmeahometeam.reskiume.ui.core.primaryGreen
import com.findmeahometeam.reskiume.ui.core.primaryRed
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import reskiume.composeapp.generated.resources.Res
import reskiume.composeapp.generated.resources.accepted_non_human_animal_list_creator_add_content_description
import reskiume.composeapp.generated.resources.accepted_non_human_animal_list_creator_delete_content_description
import reskiume.composeapp.generated.resources.accepted_non_human_animal_list_creator_non_human_animal_gender_label
import reskiume.composeapp.generated.resources.accepted_non_human_animal_list_creator_non_human_animal_type_label
import reskiume.composeapp.generated.resources.accepted_non_human_animal_list_creator_title
import reskiume.composeapp.generated.resources.ic_add
import reskiume.composeapp.generated.resources.ic_delete
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun RmAcceptedNonHumanAnimalListCreator(
    fosterHomeId: String,
    acceptedNonHumanAnimals: List<AcceptedNonHumanAnimalForFosterHome>,
    onAddAcceptedNonHumanAnimal: (List<AcceptedNonHumanAnimalForFosterHome>) -> Unit
) {
    var itemsAdded: List<AcceptedNonHumanAnimalForFosterHome> by remember {
        mutableStateOf(
            acceptedNonHumanAnimals
        )
    }
    var nonHumanAnimalType: NonHumanAnimalType by remember { mutableStateOf(NonHumanAnimalType.UNSELECTED) }
    var gender: Gender by remember { mutableStateOf(Gender.UNSELECTED) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColorForItems, shape = RoundedCornerShape(15.dp))
            .border(BorderStroke(1.dp, Color.Black), shape = RoundedCornerShape(15.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ) {
        RmText(
            text = stringResource(Res.string.accepted_non_human_animal_list_creator_title),
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            RmDropDownMenu(
                modifier = Modifier.weight(1f),
                dropDownLabel = stringResource(Res.string.accepted_non_human_animal_list_creator_non_human_animal_type_label),
                defaultElementText = nonHumanAnimalType.toEmoji() + " " + stringResource(
                    nonHumanAnimalType.toStringResource()
                ),
                items = NonHumanAnimalType.entries.mapNotNull {
                    if (it != NonHumanAnimalType.UNSELECTED) {
                        Pair(it, it.toEmoji() + " " + stringResource(it.toStringResource()))
                    } else {
                        null
                    }
                },
                textStyleForTextField = LocalTextStyle.current.copy(fontSize = 12.sp),
                onClick = { nonHumanAnimalType = it },
            )

            Spacer(modifier = Modifier.width(16.dp))
            RmDropDownMenu(
                modifier = Modifier.weight(1f),
                dropDownLabel = stringResource(Res.string.accepted_non_human_animal_list_creator_non_human_animal_gender_label),
                defaultElementText = gender.toEmoji() + " " + stringResource(gender.toStringResource()),
                items = Gender.entries.mapNotNull {
                    if (it != Gender.UNSELECTED) {
                        Pair(it, it.toEmoji() + " " + stringResource(it.toStringResource()))
                    } else {
                        null
                    }
                },
                textStyleForTextField = LocalTextStyle.current.copy(fontSize = 12.sp),
                onClick = { gender = it },
            )

            IconButton(
                modifier = Modifier
                    .padding(start = 16.dp, top = 8.dp)
                    .size(32.dp),
                onClick = {
                    if (nonHumanAnimalType != NonHumanAnimalType.UNSELECTED && gender != Gender.UNSELECTED) {

                        val existingItems = itemsAdded.filter {
                            it.acceptedNonHumanAnimalType == nonHumanAnimalType
                                    && it.acceptedNonHumanAnimalGender == gender
                        }
                        if (existingItems.isEmpty()) {
                            val acceptedNonHumanAnimal = AcceptedNonHumanAnimalForFosterHome(
                                acceptedNonHumanAnimalId = Clock.System.now().epochSeconds,
                                fosterHomeId = fosterHomeId,
                                acceptedNonHumanAnimalType = nonHumanAnimalType,
                                acceptedNonHumanAnimalGender = gender
                            )
                            itemsAdded += acceptedNonHumanAnimal
                            onAddAcceptedNonHumanAnimal(itemsAdded)
                            nonHumanAnimalType = NonHumanAnimalType.UNSELECTED
                            gender = Gender.UNSELECTED
                        }
                    }
                }
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_add),
                    contentDescription = stringResource(Res.string.accepted_non_human_animal_list_creator_add_content_description),
                    tint = primaryGreen,
                    modifier = Modifier.size(24.dp),
                )
            }
        }
    }

    if (itemsAdded.isNotEmpty()) {
        Spacer(modifier = Modifier.height(8.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(backgroundColorForItems, shape = RoundedCornerShape(15.dp))
                .border(BorderStroke(1.dp, Color.Black), shape = RoundedCornerShape(15.dp))
                .padding(16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            itemsAdded.forEachIndexed { index, acceptedNonHumanAnimalForFosterHome ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RmText(
                        text = acceptedNonHumanAnimalForFosterHome.acceptedNonHumanAnimalType.toEmoji()
                                + " " + stringResource(acceptedNonHumanAnimalForFosterHome.acceptedNonHumanAnimalType.toStringResource())
                                + " · " + acceptedNonHumanAnimalForFosterHome.acceptedNonHumanAnimalGender.toEmoji()
                                + " " + stringResource(acceptedNonHumanAnimalForFosterHome.acceptedNonHumanAnimalGender.toStringResource()),
                        fontSize = 16.sp
                    )

                    IconButton(
                        modifier = Modifier.size(32.dp),
                        onClick = {
                            itemsAdded.first {
                                it.acceptedNonHumanAnimalType == acceptedNonHumanAnimalForFosterHome.acceptedNonHumanAnimalType
                                        && it.acceptedNonHumanAnimalGender == acceptedNonHumanAnimalForFosterHome.acceptedNonHumanAnimalGender
                            }.also {
                                itemsAdded -= it
                                onAddAcceptedNonHumanAnimal(itemsAdded)
                            }
                        }
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_delete),
                            contentDescription = stringResource(Res.string.accepted_non_human_animal_list_creator_delete_content_description),
                            tint = primaryRed,
                            modifier = Modifier.size(24.dp),
                        )
                    }
                }
                if (index < itemsAdded.size - 1) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}
