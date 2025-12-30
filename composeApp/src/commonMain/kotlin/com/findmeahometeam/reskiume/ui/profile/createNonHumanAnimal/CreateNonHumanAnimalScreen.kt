package com.findmeahometeam.reskiume.ui.profile.createNonHumanAnimal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.findmeahometeam.reskiume.domain.model.AgeCategory
import com.findmeahometeam.reskiume.domain.model.Gender
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimal
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimalType
import com.findmeahometeam.reskiume.domain.model.toStringResource
import com.findmeahometeam.reskiume.ui.core.backgroundColor
import com.findmeahometeam.reskiume.ui.core.components.RmAddPhoto
import com.findmeahometeam.reskiume.ui.core.components.RmButton
import com.findmeahometeam.reskiume.ui.core.components.RmDropDownMenu
import com.findmeahometeam.reskiume.ui.core.components.RmResultState
import com.findmeahometeam.reskiume.ui.core.components.RmScaffold
import com.findmeahometeam.reskiume.ui.core.components.RmTextField
import com.findmeahometeam.reskiume.ui.core.components.UiState
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import reskiume.composeapp.generated.resources.Res
import reskiume.composeapp.generated.resources.create_non_human_animal_screen_create_non_human_animal_profile_button
import reskiume.composeapp.generated.resources.create_non_human_animal_screen_create_non_human_animal_profile_named_button
import reskiume.composeapp.generated.resources.create_non_human_animal_screen_non_human_animal_age_category
import reskiume.composeapp.generated.resources.create_non_human_animal_screen_non_human_animal_description
import reskiume.composeapp.generated.resources.create_non_human_animal_screen_non_human_animal_gender
import reskiume.composeapp.generated.resources.create_non_human_animal_screen_non_human_animal_name
import reskiume.composeapp.generated.resources.create_non_human_animal_screen_non_human_animal_type
import reskiume.composeapp.generated.resources.create_non_human_animal_screen_title

@Composable
fun CreateNonHumanAnimalScreen(
    onBackPressed: () -> Unit
) {
    val createNonHumanAnimalViewmodel: CreateNonHumanAnimalViewmodel =
        koinViewModel<CreateNonHumanAnimalViewmodel>()

    val saveChangesUiState: UiState<Unit> by createNonHumanAnimalViewmodel.saveChangesUiState.collectAsState()

    var imageUrl: String by rememberSaveable { mutableStateOf("") }
    var name: String by rememberSaveable { mutableStateOf("") }
    var nonHumanAnimalType: NonHumanAnimalType by rememberSaveable {
        mutableStateOf(NonHumanAnimalType.UNSELECTED)
    }
    var gender: Gender by rememberSaveable { mutableStateOf(Gender.UNSELECTED) }
    var ageCategory: AgeCategory by rememberSaveable { mutableStateOf(AgeCategory.UNSELECTED) }
    var description: String by rememberSaveable { mutableStateOf("") }

    val isUpdateUserButtonEnabled by remember(
        imageUrl,
        name,
        nonHumanAnimalType,
        gender,
        ageCategory,
        description
    ) {
        derivedStateOf {
            imageUrl.isNotBlank()
                    && name.isNotBlank()
                    && nonHumanAnimalType != NonHumanAnimalType.UNSELECTED
                    && gender != Gender.UNSELECTED
                    && ageCategory != AgeCategory.UNSELECTED
                    && description.isNotBlank()
        }
    }

    val scrollState = rememberScrollState()

    RmScaffold(
        title = stringResource(Res.string.create_non_human_animal_screen_title),
        onBackPressed = onBackPressed,
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(8.dp))
            RmAddPhoto {
                imageUrl = it
            }

            Spacer(modifier = Modifier.height(8.dp))
            RmTextField(
                modifier = Modifier.fillMaxWidth(),
                text = name,
                label = stringResource(Res.string.create_non_human_animal_screen_non_human_animal_name),
                onValueChange = { name = it }
            )

            Spacer(modifier = Modifier.height(8.dp))
            RmDropDownMenu(
                dropDownLabel = stringResource(Res.string.create_non_human_animal_screen_non_human_animal_type),
                defaultElementText = stringResource(nonHumanAnimalType.toStringResource()),
                items = NonHumanAnimalType.entries.mapNotNull {
                    if (it != NonHumanAnimalType.UNSELECTED) {
                        Pair(it, stringResource(it.toStringResource()))
                    } else {
                        null
                    }
                },
                onClick = { nonHumanAnimalType = it },
            )

            Spacer(modifier = Modifier.height(8.dp))
            RmDropDownMenu(
                dropDownLabel = stringResource(Res.string.create_non_human_animal_screen_non_human_animal_gender),
                defaultElementText = stringResource(gender.toStringResource()),
                items = Gender.entries.mapNotNull {
                    if (it != Gender.UNSELECTED) {
                        Pair(it, stringResource(it.toStringResource()))
                    } else {
                        null
                    }
                },
                onClick = { gender = it },
            )

            Spacer(modifier = Modifier.height(8.dp))
            RmDropDownMenu(
                dropDownLabel = stringResource(Res.string.create_non_human_animal_screen_non_human_animal_age_category),
                defaultElementText = stringResource(ageCategory.toStringResource()),
                items = AgeCategory.entries.mapNotNull {
                    if (it != AgeCategory.UNSELECTED) {
                        Pair(it, stringResource(it.toStringResource()))
                    } else {
                        null
                    }
                },
                onClick = { ageCategory = it },
            )

            Spacer(modifier = Modifier.height(8.dp))
            RmTextField(
                modifier = Modifier.fillMaxWidth(),
                text = description,
                label = stringResource(Res.string.create_non_human_animal_screen_non_human_animal_description),
                onValueChange = { description = it }
            )

            Spacer(modifier = Modifier.height(10.dp))
            RmResultState(saveChangesUiState, onSuccess = { onBackPressed() })
            Spacer(modifier = Modifier.height(10.dp))

            Spacer(modifier = Modifier.weight(1f))
            RmButton(
                text = if (name.isEmpty()) {
                    stringResource(Res.string.create_non_human_animal_screen_create_non_human_animal_profile_button)
                } else {
                    stringResource(
                        Res.string.create_non_human_animal_screen_create_non_human_animal_profile_named_button,
                        name
                    )
                },
                enabled = isUpdateUserButtonEnabled,
                onClick = {
                    createNonHumanAnimalViewmodel.saveNonHumanAnimalChanges(
                        NonHumanAnimal(
                            caregiverId = "",
                            imageUrl = imageUrl,
                            name = name,
                            nonHumanAnimalType = nonHumanAnimalType,
                            gender = gender,
                            ageCategory = ageCategory,
                            description = description
                        )
                    )
                }
            )
        }
    }
}
