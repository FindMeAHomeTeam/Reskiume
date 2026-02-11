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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimal
import com.findmeahometeam.reskiume.domain.model.fosterHome.ResidentNonHumanAnimalForFosterHome
import com.findmeahometeam.reskiume.domain.model.toEmoji
import com.findmeahometeam.reskiume.ui.core.backgroundColorForItems
import com.findmeahometeam.reskiume.ui.core.primaryGreen
import com.findmeahometeam.reskiume.ui.core.primaryRed
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import reskiume.composeapp.generated.resources.Res
import reskiume.composeapp.generated.resources.ic_add
import reskiume.composeapp.generated.resources.ic_delete
import reskiume.composeapp.generated.resources.resident_non_human_animal_list_creator_add_content_description
import reskiume.composeapp.generated.resources.resident_non_human_animal_list_creator_delete_content_description
import reskiume.composeapp.generated.resources.resident_non_human_animal_list_creator_non_human_animal_label
import reskiume.composeapp.generated.resources.resident_non_human_animal_list_creator_title
import reskiume.composeapp.generated.resources.resident_non_human_animal_list_creator_unselected_non_human_animal_label

@Composable
fun RmResidentNonHumanAnimalListCreator(
    fosterHomeId: String,
    allAvailableNonHumanAnimals: List<NonHumanAnimal>,
    allResidentNonHumanAnimals: List<ResidentNonHumanAnimalForFosterHome>,
    onAddResidentNonHumanAnimal: (List<ResidentNonHumanAnimalForFosterHome>) -> Unit
) {
    var availableNonHumanAnimals: List<NonHumanAnimal> by remember(allAvailableNonHumanAnimals) {
        mutableStateOf(
            allAvailableNonHumanAnimals
        )
    }
    var residentNonHumanAnimals: List<ResidentNonHumanAnimalForFosterHome> by remember {
        mutableStateOf(
            allResidentNonHumanAnimals
        )
    }
    var residentNonHumanAnimal: NonHumanAnimal? by remember { mutableStateOf(null) }

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
            text = stringResource(Res.string.resident_non_human_animal_list_creator_title),
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
                dropDownLabel = stringResource(Res.string.resident_non_human_animal_list_creator_non_human_animal_label),
                defaultElementText = if (residentNonHumanAnimal == null) {
                    stringResource(Res.string.resident_non_human_animal_list_creator_unselected_non_human_animal_label)
                } else {
                    residentNonHumanAnimal?.nonHumanAnimalType?.toEmoji() + " " + residentNonHumanAnimal?.name
                },
                items = availableNonHumanAnimals.map {
                    Pair(it, it.nonHumanAnimalType.toEmoji() + " " + it.name)
                },
                onClick = { residentNonHumanAnimal = it },
            )

            IconButton(
                modifier = Modifier
                    .padding(start = 16.dp, top = 8.dp)
                    .size(32.dp),
                onClick = {
                    if (residentNonHumanAnimal != null) {

                        val existingItems = residentNonHumanAnimals.filter {
                            it.residentNonHumanAnimal == residentNonHumanAnimal
                        }
                        if (existingItems.isEmpty()) {
                            val residentNonHumanAnimalForFosterHome =
                                ResidentNonHumanAnimalForFosterHome(
                                    fosterHomeId = fosterHomeId,
                                    residentNonHumanAnimal = residentNonHumanAnimal
                                )
                            availableNonHumanAnimals -= residentNonHumanAnimal!!
                            residentNonHumanAnimals += residentNonHumanAnimalForFosterHome
                            onAddResidentNonHumanAnimal(residentNonHumanAnimals)
                            residentNonHumanAnimal = null
                        }
                    }
                }
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_add),
                    contentDescription = stringResource(Res.string.resident_non_human_animal_list_creator_add_content_description),
                    tint = primaryGreen,
                    modifier = Modifier.size(24.dp),
                )
            }
        }
    }

    if (residentNonHumanAnimals.isNotEmpty()) {
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
            residentNonHumanAnimals.forEachIndexed { index, residentNonHumanAnimalForFosterHome ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RmText(
                        text = residentNonHumanAnimalForFosterHome.residentNonHumanAnimal?.nonHumanAnimalType?.toEmoji()
                                + " " + residentNonHumanAnimalForFosterHome.residentNonHumanAnimal?.name,
                        fontSize = 16.sp
                    )

                    IconButton(
                        modifier = Modifier.size(32.dp),
                        onClick = {
                            residentNonHumanAnimals.first {
                                it.residentNonHumanAnimal == residentNonHumanAnimalForFosterHome.residentNonHumanAnimal
                            }.also {
                                availableNonHumanAnimals += it.residentNonHumanAnimal!!
                                residentNonHumanAnimals -= it
                                onAddResidentNonHumanAnimal(residentNonHumanAnimals)
                            }
                        }
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_delete),
                            contentDescription = stringResource(Res.string.resident_non_human_animal_list_creator_delete_content_description),
                            tint = primaryRed,
                            modifier = Modifier.size(24.dp),
                        )
                    }
                }
                if (index < residentNonHumanAnimals.size - 1) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}
