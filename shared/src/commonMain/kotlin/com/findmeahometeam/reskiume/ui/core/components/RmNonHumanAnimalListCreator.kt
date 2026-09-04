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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimal
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimalListSaver
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimalSaver
import com.findmeahometeam.reskiume.domain.model.toEmoji
import com.findmeahometeam.reskiume.domain.model.toStringResource
import com.findmeahometeam.reskiume.ui.core.backgroundColorForItems
import com.findmeahometeam.reskiume.ui.core.primaryRed
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import reskiume.shared.generated.resources.Res
import reskiume.shared.generated.resources.ic_delete
import reskiume.shared.generated.resources.non_human_animal_list_creator_delete_content_description
import reskiume.shared.generated.resources.non_human_animal_list_creator_non_human_animal_label
import reskiume.shared.generated.resources.non_human_animal_list_creator_unselected_non_human_animal_label
import kotlin.collections.minus
import kotlin.collections.plus

@Composable
fun RmNonHumanAnimalListCreator(
    title: String,
    allAvailableNonHumanAnimals: List<NonHumanAnimal>,
    allSelectedNonHumanAnimals: List<NonHumanAnimal>,
    onAddNonHumanAnimal: (List<NonHumanAnimal>) -> Unit
) {
    // Current non-human animals that can be selected
    var availableNonHumanAnimals: List<NonHumanAnimal> by rememberSaveable(stateSaver = NonHumanAnimalListSaver) {
        mutableStateOf(
            allAvailableNonHumanAnimals
        )
    }

    // Non-human animal choice from dropdown before being added to the list
    var nonHumanAnimalChoiceFromDropdown: NonHumanAnimal? by rememberSaveable(stateSaver = NonHumanAnimalSaver) {
        mutableStateOf(
            null
        )
    }

    // Non-human animals that are added to the list
    var selectedNonHumanAnimals: List<NonHumanAnimal> by rememberSaveable(stateSaver = NonHumanAnimalListSaver) {
        mutableStateOf(
            allSelectedNonHumanAnimals
        )
    }

    LaunchedEffect(allAvailableNonHumanAnimals) {
        availableNonHumanAnimals = allAvailableNonHumanAnimals
    }

    LaunchedEffect(allSelectedNonHumanAnimals) {
        selectedNonHumanAnimals =
            if (allSelectedNonHumanAnimals.any { selectedNonHumanAnimal ->
                    availableNonHumanAnimals.contains(selectedNonHumanAnimal)
                }
            ) {
                emptyList()
            } else {
                allSelectedNonHumanAnimals
            }
    }

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
            text = title,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            RmDropDownMenu(
                modifier = Modifier.fillMaxWidth(),
                dropDownLabel = stringResource(Res.string.non_human_animal_list_creator_non_human_animal_label),
                defaultElementText = if (nonHumanAnimalChoiceFromDropdown == null) {
                    stringResource(Res.string.non_human_animal_list_creator_unselected_non_human_animal_label)
                } else {
                    nonHumanAnimalChoiceFromDropdown?.nonHumanAnimalType?.toEmoji() + " " + nonHumanAnimalChoiceFromDropdown?.name
                },
                items = availableNonHumanAnimals.map {
                    Pair(it, it.nonHumanAnimalType.toEmoji() + " " + it.name)
                },
                onClick = { nonHumanAnimalChoiceFromDropdown = it },
            )

            LaunchedEffect(nonHumanAnimalChoiceFromDropdown) {
                if (nonHumanAnimalChoiceFromDropdown != null) {

                    // Add a non-human animal to the list if it doesn't exist
                    val existingItems = selectedNonHumanAnimals.filter {
                        it.id == nonHumanAnimalChoiceFromDropdown!!.id
                    }
                    if (existingItems.isEmpty()) {
                        availableNonHumanAnimals -= nonHumanAnimalChoiceFromDropdown!!
                        selectedNonHumanAnimals += nonHumanAnimalChoiceFromDropdown!!
                        onAddNonHumanAnimal(selectedNonHumanAnimals)
                        nonHumanAnimalChoiceFromDropdown = null
                    }
                }
            }
        }
    }

    if (selectedNonHumanAnimals.isNotEmpty()) {
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
            selectedNonHumanAnimals.forEachIndexed { index, existentNonHumanAnimal ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RmText(
                        text = existentNonHumanAnimal.nonHumanAnimalType.toEmoji()
                                + " " + existentNonHumanAnimal.name
                                + " · " + existentNonHumanAnimal.gender.toEmoji()
                                + " " + stringResource(existentNonHumanAnimal.gender.toStringResource()),
                        fontSize = 16.sp
                    )

                    IconButton(
                        modifier = Modifier.size(32.dp),
                        onClick = {
                            selectedNonHumanAnimals.first {
                                it == existentNonHumanAnimal
                            }.also {
                                availableNonHumanAnimals += it
                                selectedNonHumanAnimals -= it
                                onAddNonHumanAnimal(selectedNonHumanAnimals)
                            }
                        }
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_delete),
                            contentDescription = stringResource(Res.string.non_human_animal_list_creator_delete_content_description),
                            tint = primaryRed,
                            modifier = Modifier.size(24.dp),
                        )
                    }
                }
                if (index < selectedNonHumanAnimals.size - 1) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}
