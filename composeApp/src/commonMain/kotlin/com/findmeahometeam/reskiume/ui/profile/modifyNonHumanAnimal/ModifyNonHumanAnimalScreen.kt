package com.findmeahometeam.reskiume.ui.profile.modifyNonHumanAnimal

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
import com.findmeahometeam.reskiume.ui.core.components.RmDialog
import com.findmeahometeam.reskiume.ui.core.components.RmDropDownMenu
import com.findmeahometeam.reskiume.ui.core.components.RmResultState
import com.findmeahometeam.reskiume.ui.core.components.RmScaffold
import com.findmeahometeam.reskiume.ui.core.components.RmTextField
import com.findmeahometeam.reskiume.ui.core.components.RmTextLink
import com.findmeahometeam.reskiume.ui.core.components.UiState
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import reskiume.composeapp.generated.resources.Res
import reskiume.composeapp.generated.resources.modify_non_human_animal_screen_delete_non_human_animal_button
import reskiume.composeapp.generated.resources.modify_non_human_animal_screen_delete_non_human_animal_message
import reskiume.composeapp.generated.resources.modify_non_human_animal_screen_delete_non_human_animal_text
import reskiume.composeapp.generated.resources.modify_non_human_animal_screen_delete_non_human_animal_title
import reskiume.composeapp.generated.resources.modify_non_human_animal_screen_dismiss_delete_non_human_animal_button
import reskiume.composeapp.generated.resources.modify_non_human_animal_screen_non_human_animal_age_category
import reskiume.composeapp.generated.resources.modify_non_human_animal_screen_non_human_animal_description
import reskiume.composeapp.generated.resources.modify_non_human_animal_screen_non_human_animal_gender
import reskiume.composeapp.generated.resources.modify_non_human_animal_screen_non_human_animal_name
import reskiume.composeapp.generated.resources.modify_non_human_animal_screen_non_human_animal_profile_title
import reskiume.composeapp.generated.resources.modify_non_human_animal_screen_non_human_animal_type
import reskiume.composeapp.generated.resources.modify_non_human_animal_screen_save_non_human_animal_changes_button

@Composable
fun ModifyNonHumanAnimalScreen(
    onBackPressed: () -> Unit
) {
    val modifyNonHumanAnimalViewmodel: ModifyNonHumanAnimalViewmodel =
        koinViewModel<ModifyNonHumanAnimalViewmodel>()

    val nonHumanAnimalState: UiState<NonHumanAnimal> by modifyNonHumanAnimalViewmodel.nonHumanAnimalFlow.collectAsState(
        initial = UiState.Loading()
    )
    val manageChangesUiState: UiState<Unit> by modifyNonHumanAnimalViewmodel.manageChangesUiState.collectAsState()

    val scrollState = rememberScrollState()

    RmScaffold(
        title = stringResource(
            Res.string.modify_non_human_animal_screen_non_human_animal_profile_title,
            if (nonHumanAnimalState is UiState.Success) (nonHumanAnimalState as UiState.Success<NonHumanAnimal>).data.name else ""
        ),
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

            RmResultState(nonHumanAnimalState) { nonHumanAnimal: NonHumanAnimal ->

                var imageUrl: String by rememberSaveable { mutableStateOf(nonHumanAnimal.imageUrl) }
                var name: String by rememberSaveable { mutableStateOf(nonHumanAnimal.name) }
                var nonHumanAnimalType: NonHumanAnimalType by rememberSaveable {
                    mutableStateOf(
                        nonHumanAnimal.nonHumanAnimalType
                    )
                }
                var gender: Gender by rememberSaveable { mutableStateOf(nonHumanAnimal.gender) }
                var ageCategory: AgeCategory by rememberSaveable { mutableStateOf(nonHumanAnimal.ageCategory) }
                var description: String by rememberSaveable { mutableStateOf(nonHumanAnimal.description) }
                var displayDeleteDialog: Boolean by rememberSaveable { mutableStateOf(false) }

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
                                && (imageUrl != nonHumanAnimal.imageUrl
                                || name != nonHumanAnimal.name
                                || nonHumanAnimalType != nonHumanAnimal.nonHumanAnimalType
                                || gender != nonHumanAnimal.gender
                                || ageCategory != nonHumanAnimal.ageCategory
                                || description != nonHumanAnimal.description)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                RmAddPhoto(currentImageUri = nonHumanAnimal.imageUrl) {
                    imageUrl = it
                }

                Spacer(modifier = Modifier.height(8.dp))
                RmTextField(
                    modifier = Modifier.fillMaxWidth(),
                    text = name,
                    label = stringResource(Res.string.modify_non_human_animal_screen_non_human_animal_name),
                    onValueChange = { name = it }
                )

                Spacer(modifier = Modifier.height(8.dp))
                RmDropDownMenu(
                    dropDownLabel = stringResource(Res.string.modify_non_human_animal_screen_non_human_animal_type),
                    defaultElementText = stringResource(nonHumanAnimal.nonHumanAnimalType.toStringResource()),
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
                    dropDownLabel = stringResource(Res.string.modify_non_human_animal_screen_non_human_animal_gender),
                    defaultElementText = stringResource(nonHumanAnimal.gender.toStringResource()),
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
                    dropDownLabel = stringResource(Res.string.modify_non_human_animal_screen_non_human_animal_age_category),
                    defaultElementText = stringResource(nonHumanAnimal.ageCategory.toStringResource()),
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
                    label = stringResource(Res.string.modify_non_human_animal_screen_non_human_animal_description),
                    onValueChange = { description = it }
                )

                Spacer(modifier = Modifier.height(20.dp))
                RmTextLink(
                    text = stringResource(
                        Res.string.modify_non_human_animal_screen_delete_non_human_animal_text,
                        nonHumanAnimal.name
                    ),
                    textToLink = stringResource(Res.string.modify_non_human_animal_screen_delete_non_human_animal_button),
                    onClick = { displayDeleteDialog = true }
                )
                if (displayDeleteDialog) {
                    RmDialog(
                        emoji = "🗑️",
                        title = stringResource(
                            Res.string.modify_non_human_animal_screen_delete_non_human_animal_title,
                            nonHumanAnimal.name
                        ),
                        message = stringResource(Res.string.modify_non_human_animal_screen_delete_non_human_animal_message),
                        allowMessage = stringResource(Res.string.modify_non_human_animal_screen_delete_non_human_animal_button),
                        denyMessage = stringResource(Res.string.modify_non_human_animal_screen_dismiss_delete_non_human_animal_button),
                        onClickAllow = {
                            modifyNonHumanAnimalViewmodel.deleteNonHumanAnimal(
                                nonHumanAnimal.id,
                                nonHumanAnimal.caregiverId
                            )
                            displayDeleteDialog = false
                        },
                        onClickDeny = { displayDeleteDialog = false }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
                RmResultState(manageChangesUiState, onSuccess = { onBackPressed() })
                Spacer(modifier = Modifier.height(10.dp))

                Spacer(modifier = Modifier.weight(1f))
                RmButton(
                    text = stringResource(
                        Res.string.modify_non_human_animal_screen_save_non_human_animal_changes_button,
                        nonHumanAnimal.name
                    ),
                    enabled = isUpdateUserButtonEnabled,
                    onClick = {
                        modifyNonHumanAnimalViewmodel.saveNonHumanAnimalChanges(
                            isDifferentImage = imageUrl != nonHumanAnimal.imageUrl,
                            modifiedNonHumanAnimal = nonHumanAnimal.copy(
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
}
