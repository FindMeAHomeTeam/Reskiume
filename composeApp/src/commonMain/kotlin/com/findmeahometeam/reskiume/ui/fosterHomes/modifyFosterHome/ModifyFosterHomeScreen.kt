package com.findmeahometeam.reskiume.ui.fosterHomes.modifyFosterHome

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
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimal
import com.findmeahometeam.reskiume.domain.model.fosterHome.AcceptedNonHumanAnimalForFosterHome
import com.findmeahometeam.reskiume.domain.model.fosterHome.ResidentNonHumanAnimalForFosterHome
import com.findmeahometeam.reskiume.ui.core.backgroundColor
import com.findmeahometeam.reskiume.ui.core.components.RmAcceptedNonHumanAnimalListCreator
import com.findmeahometeam.reskiume.ui.core.components.RmAddPhoto
import com.findmeahometeam.reskiume.ui.core.components.RmButton
import com.findmeahometeam.reskiume.ui.core.components.RmDialog
import com.findmeahometeam.reskiume.ui.core.components.RmListAvatarType
import com.findmeahometeam.reskiume.ui.core.components.RmListSwitchItem
import com.findmeahometeam.reskiume.ui.core.components.RmResidentNonHumanAnimalListCreator
import com.findmeahometeam.reskiume.ui.core.components.RmResultState
import com.findmeahometeam.reskiume.ui.core.components.RmScaffold
import com.findmeahometeam.reskiume.ui.core.components.RmTextField
import com.findmeahometeam.reskiume.ui.core.components.RmTextLink
import com.findmeahometeam.reskiume.ui.core.components.UiState
import com.findmeahometeam.reskiume.ui.core.primaryGreen
import com.findmeahometeam.reskiume.ui.core.tertiaryGreen
import com.findmeahometeam.reskiume.ui.fosterHomes.checkAllFosterHomes.UiFosterHome
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import reskiume.composeapp.generated.resources.Res
import reskiume.composeapp.generated.resources.ic_notifications
import reskiume.composeapp.generated.resources.modify_foster_home_availability_description
import reskiume.composeapp.generated.resources.modify_foster_home_screen_available_label
import reskiume.composeapp.generated.resources.modify_foster_home_screen_delete_foster_home_button
import reskiume.composeapp.generated.resources.modify_foster_home_screen_delete_foster_home_message
import reskiume.composeapp.generated.resources.modify_foster_home_screen_delete_foster_home_text
import reskiume.composeapp.generated.resources.modify_foster_home_screen_delete_foster_home_title
import reskiume.composeapp.generated.resources.modify_foster_home_screen_dismiss_delete_foster_home_button
import reskiume.composeapp.generated.resources.modify_foster_home_screen_foster_home_conditions
import reskiume.composeapp.generated.resources.modify_foster_home_screen_foster_home_description
import reskiume.composeapp.generated.resources.modify_foster_home_screen_foster_home_title
import reskiume.composeapp.generated.resources.modify_foster_home_screen_save_foster_home_changes_button
import reskiume.composeapp.generated.resources.modify_foster_home_screen_title
import reskiume.composeapp.generated.resources.modify_foster_home_screen_unavailable_label
import reskiume.composeapp.generated.resources.modify_foster_home_screen_warning_foster_home_with_residents_message
import reskiume.composeapp.generated.resources.modify_foster_home_screen_warning_foster_home_with_residents_ok_button
import reskiume.composeapp.generated.resources.modify_foster_home_screen_warning_foster_home_with_residents_title

@Composable
fun ModifyFosterHomeScreen(
    onBackPressed: () -> Unit
) {
    val modifyFosterHomeViewmodel: ModifyFosterHomeViewmodel =
        koinViewModel<ModifyFosterHomeViewmodel>()

    val uiFosterHomeState: UiState<UiFosterHome> by modifyFosterHomeViewmodel.fosterHomeFlow.collectAsState(
        initial = UiState.Loading()
    )
    val allAvailableNonHumanAnimals: List<NonHumanAnimal> by modifyFosterHomeViewmodel.allAvailableNonHumanAnimalsLookingForAdoptionFlow.collectAsState(
        initial = emptyList()
    )
    val manageChangesUiState: UiState<Unit> by modifyFosterHomeViewmodel.manageChangesUiState.collectAsState()

    val scrollState = rememberScrollState()

    RmScaffold(
        title =
            if (uiFosterHomeState is UiState.Success) {
                stringResource(
                    Res.string.modify_foster_home_screen_title,
                    (uiFosterHomeState as UiState.Success<UiFosterHome>).data.fosterHome.title,
                    (uiFosterHomeState as UiState.Success<UiFosterHome>).data.fosterHome.city,
                    (uiFosterHomeState as UiState.Success<UiFosterHome>).data.fosterHome.country
                )
            } else {
                ""
            },
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

            RmResultState(uiFosterHomeState) { uiFosterHome: UiFosterHome ->

                var title: String by rememberSaveable { mutableStateOf(uiFosterHome.fosterHome.title) }
                var description: String by rememberSaveable { mutableStateOf(uiFosterHome.fosterHome.description) }
                var conditions: String by rememberSaveable { mutableStateOf(uiFosterHome.fosterHome.conditions) }
                var imageUrl: String by rememberSaveable { mutableStateOf(uiFosterHome.fosterHome.imageUrl) }
                var allAcceptedNonHumanAnimals: List<AcceptedNonHumanAnimalForFosterHome> by rememberSaveable {
                    mutableStateOf(
                        uiFosterHome.fosterHome.allAcceptedNonHumanAnimals
                    )
                }
                var uiAllResidentNonHumanAnimals: List<NonHumanAnimal> by rememberSaveable {
                    mutableStateOf(
                        uiFosterHome.uiAllResidentNonHumanAnimals
                    )
                }
                var isAvailable: Boolean by rememberSaveable { mutableStateOf(uiFosterHome.fosterHome.available) }
                var displayDeleteDialog: Boolean by rememberSaveable { mutableStateOf(false) }
                var displayFosterHomeWithResidentsDialog: Boolean by rememberSaveable { mutableStateOf(false) }

                val isUpdateFosterHomeButtonEnabled by remember(
                    title,
                    description,
                    conditions,
                    imageUrl,
                    allAcceptedNonHumanAnimals,
                    uiAllResidentNonHumanAnimals,
                    isAvailable,
                ) {
                    derivedStateOf {
                        imageUrl.isNotBlank()
                                && title.isNotBlank()
                                && description.isNotBlank()
                                && conditions.isNotBlank()
                                && allAcceptedNonHumanAnimals.isNotEmpty()
                                && (imageUrl != uiFosterHome.fosterHome.imageUrl
                                || title != uiFosterHome.fosterHome.title
                                || description != uiFosterHome.fosterHome.description
                                || conditions != uiFosterHome.fosterHome.conditions
                                || allAcceptedNonHumanAnimals != uiFosterHome.fosterHome.allAcceptedNonHumanAnimals
                                || uiAllResidentNonHumanAnimals != uiFosterHome.uiAllResidentNonHumanAnimals
                                || isAvailable != uiFosterHome.fosterHome.available)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                RmAddPhoto(currentImageUri = uiFosterHome.fosterHome.imageUrl) {
                    imageUrl = it
                }

                Spacer(modifier = Modifier.height(8.dp))
                RmTextField(
                    modifier = Modifier.fillMaxWidth(),
                    text = title,
                    label = stringResource(Res.string.modify_foster_home_screen_foster_home_title),
                    onValueChange = { title = it }
                )

                Spacer(modifier = Modifier.height(8.dp))
                RmTextField(
                    modifier = Modifier.fillMaxWidth(),
                    text = description,
                    label = stringResource(Res.string.modify_foster_home_screen_foster_home_description),
                    onValueChange = { description = it }
                )

                Spacer(modifier = Modifier.height(8.dp))
                RmTextField(
                    modifier = Modifier.fillMaxWidth(),
                    text = conditions,
                    label = stringResource(Res.string.modify_foster_home_screen_foster_home_conditions),
                    onValueChange = { conditions = it }
                )

                Spacer(modifier = Modifier.height(8.dp))
                RmListSwitchItem(
                    title = if (isAvailable) {
                        stringResource(Res.string.modify_foster_home_screen_available_label)
                    } else {
                        stringResource(Res.string.modify_foster_home_screen_unavailable_label)
                    },
                    description = stringResource(Res.string.modify_foster_home_availability_description),
                    containerColor = backgroundColor,
                    listAvatarType = RmListAvatarType.Icon(
                        backgroundColor = tertiaryGreen,
                        icon = Res.drawable.ic_notifications,
                        iconColor = primaryGreen
                    ),
                    isChecked = isAvailable,
                    onCheckedChange = { isChecked ->
                        isAvailable = isChecked
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))
                RmAcceptedNonHumanAnimalListCreator(
                    uiFosterHome.fosterHome.id,
                    uiFosterHome.fosterHome.allAcceptedNonHumanAnimals
                ) {
                    allAcceptedNonHumanAnimals = it
                }

                Spacer(modifier = Modifier.height(8.dp))
                RmResidentNonHumanAnimalListCreator(
                    allAvailableNonHumanAnimals,
                    uiFosterHome.uiAllResidentNonHumanAnimals
                ) {
                    uiAllResidentNonHumanAnimals = it
                }

                Spacer(modifier = Modifier.height(20.dp))
                RmTextLink(
                    text = stringResource(
                        Res.string.modify_foster_home_screen_delete_foster_home_text,
                        uiFosterHome.fosterHome.title
                    ),
                    textToLink = stringResource(Res.string.modify_foster_home_screen_delete_foster_home_button),
                    onClick = {
                        if (uiFosterHome.uiAllResidentNonHumanAnimals.isEmpty()) {
                            displayDeleteDialog = true
                        } else {
                            displayFosterHomeWithResidentsDialog = true
                        }

                    }
                )
                if (displayDeleteDialog) {
                    RmDialog(
                        emoji = "🗑️",
                        title = stringResource(
                            Res.string.modify_foster_home_screen_delete_foster_home_title,
                            uiFosterHome.fosterHome.title
                        ),
                        message = stringResource(Res.string.modify_foster_home_screen_delete_foster_home_message),
                        allowMessage = stringResource(Res.string.modify_foster_home_screen_delete_foster_home_button),
                        denyMessage = stringResource(Res.string.modify_foster_home_screen_dismiss_delete_foster_home_button),
                        onClickAllow = {
                            modifyFosterHomeViewmodel.deleteFosterHome(
                                uiFosterHome.fosterHome.id,
                                uiFosterHome.fosterHome.ownerId
                            )
                            displayDeleteDialog = false
                        },
                        onClickDeny = { displayDeleteDialog = false }
                    )
                }
                if (displayFosterHomeWithResidentsDialog) {
                    RmDialog(
                        emoji = "🐷🐱",
                        title = stringResource(
                            Res.string.modify_foster_home_screen_warning_foster_home_with_residents_title,
                            uiFosterHome.uiAllResidentNonHumanAnimals.size
                        ),
                        message = stringResource(Res.string.modify_foster_home_screen_warning_foster_home_with_residents_message),
                        allowMessage = stringResource(Res.string.modify_foster_home_screen_warning_foster_home_with_residents_ok_button),
                        onClickAllow = {
                            displayFosterHomeWithResidentsDialog = false
                        },
                        onClickDeny = { displayFosterHomeWithResidentsDialog = false }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
                RmResultState(manageChangesUiState, onSuccess = { onBackPressed() })
                Spacer(modifier = Modifier.height(10.dp))

                Spacer(modifier = Modifier.weight(1f))
                RmButton(
                    text = stringResource(
                        Res.string.modify_foster_home_screen_save_foster_home_changes_button,
                        title
                    ),
                    enabled = isUpdateFosterHomeButtonEnabled,
                    onClick = {
                        modifyFosterHomeViewmodel.saveFosterHomeChanges(
                            isDifferentImage = imageUrl != uiFosterHome.fosterHome.imageUrl,
                            modifiedFosterHome = uiFosterHome.fosterHome.copy(
                                imageUrl = imageUrl,
                                title = title,
                                description = description,
                                conditions = conditions,
                                allAcceptedNonHumanAnimals = allAcceptedNonHumanAnimals,
                                allResidentNonHumanAnimals = uiAllResidentNonHumanAnimals.map {
                                    ResidentNonHumanAnimalForFosterHome(
                                        nonHumanAnimalId = it.id,
                                        caregiverId = it.caregiverId,
                                        fosterHomeId = uiFosterHome.fosterHome.id
                                    )
                                },
                                available = isAvailable
                            )
                        )
                    }
                )
            }
        }
    }
}
