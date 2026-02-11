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
import com.findmeahometeam.reskiume.domain.model.fosterHome.FosterHome
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

@Composable
fun ModifyFosterHomeScreen(
    onBackPressed: () -> Unit
) {
    val modifyFosterHomeViewmodel: ModifyFosterHomeViewmodel =
        koinViewModel<ModifyFosterHomeViewmodel>()

    val fosterHomeState: UiState<FosterHome> by modifyFosterHomeViewmodel.fosterHomeFlow.collectAsState(
        initial = UiState.Loading()
    )
    val allAvailableNonHumanAnimals: List<NonHumanAnimal> by modifyFosterHomeViewmodel.allAvailableNonHumanAnimalsFlow.collectAsState(
        initial = emptyList()
    )
    val manageChangesUiState: UiState<Unit> by modifyFosterHomeViewmodel.manageChangesUiState.collectAsState()

    val scrollState = rememberScrollState()

    RmScaffold(
        title =
            if (fosterHomeState is UiState.Success) {
                stringResource(
                    Res.string.modify_foster_home_screen_title,
                    (fosterHomeState as UiState.Success<FosterHome>).data.title,
                    (fosterHomeState as UiState.Success<FosterHome>).data.city,
                    (fosterHomeState as UiState.Success<FosterHome>).data.country
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

            RmResultState(fosterHomeState) { fosterHome: FosterHome ->

                var title: String by rememberSaveable { mutableStateOf(fosterHome.title) }
                var description: String by rememberSaveable { mutableStateOf(fosterHome.description) }
                var conditions: String by rememberSaveable { mutableStateOf(fosterHome.conditions) }
                var imageUrl: String by rememberSaveable { mutableStateOf(fosterHome.imageUrl) }
                var allAcceptedNonHumanAnimals: List<AcceptedNonHumanAnimalForFosterHome> by rememberSaveable {
                    mutableStateOf(
                        fosterHome.allAcceptedNonHumanAnimals
                    )
                }
                var allResidentNonHumanAnimals: List<ResidentNonHumanAnimalForFosterHome> by rememberSaveable {
                    mutableStateOf(
                        fosterHome.allResidentNonHumanAnimals
                    )
                }
                var isAvailable: Boolean by rememberSaveable { mutableStateOf(fosterHome.available) }
                var displayDeleteDialog: Boolean by rememberSaveable { mutableStateOf(false) }

                val isUpdateUserButtonEnabled by remember(
                    title,
                    description,
                    conditions,
                    imageUrl,
                    allAcceptedNonHumanAnimals,
                    allResidentNonHumanAnimals,
                    isAvailable,
                ) {
                    derivedStateOf {
                        imageUrl.isNotBlank()
                                && title.isNotBlank()
                                && description.isNotBlank()
                                && conditions.isNotBlank()
                                && allAcceptedNonHumanAnimals.isNotEmpty()
                                && (imageUrl != fosterHome.imageUrl
                                || title != fosterHome.title
                                || description != fosterHome.description
                                || conditions != fosterHome.conditions
                                || allAcceptedNonHumanAnimals != fosterHome.allAcceptedNonHumanAnimals
                                || allResidentNonHumanAnimals != fosterHome.allResidentNonHumanAnimals
                                || isAvailable != fosterHome.available)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                RmAddPhoto(currentImageUri = fosterHome.imageUrl) {
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
                    fosterHome.id,
                    fosterHome.allAcceptedNonHumanAnimals
                ) {
                    allAcceptedNonHumanAnimals = it
                }

                Spacer(modifier = Modifier.height(8.dp))
                RmResidentNonHumanAnimalListCreator(
                    fosterHome.id,
                    allAvailableNonHumanAnimals,
                    fosterHome.allResidentNonHumanAnimals
                ) {
                    allResidentNonHumanAnimals = it
                }

                Spacer(modifier = Modifier.height(20.dp))
                RmTextLink(
                    text = stringResource(
                        Res.string.modify_foster_home_screen_delete_foster_home_text,
                        fosterHome.title
                    ),
                    textToLink = stringResource(Res.string.modify_foster_home_screen_delete_foster_home_button),
                    onClick = { displayDeleteDialog = true }
                )
                if (displayDeleteDialog) {
                    RmDialog(
                        emoji = "🗑️",
                        title = stringResource(
                            Res.string.modify_foster_home_screen_delete_foster_home_title,
                            fosterHome.title
                        ),
                        message = stringResource(Res.string.modify_foster_home_screen_delete_foster_home_message),
                        allowMessage = stringResource(Res.string.modify_foster_home_screen_delete_foster_home_button),
                        denyMessage = stringResource(Res.string.modify_foster_home_screen_dismiss_delete_foster_home_button),
                        onClickAllow = {
                            modifyFosterHomeViewmodel.deleteFosterHome(
                                fosterHome.id,
                                fosterHome.ownerId
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
                        Res.string.modify_foster_home_screen_save_foster_home_changes_button,
                        title
                    ),
                    enabled = isUpdateUserButtonEnabled,
                    onClick = {
                        modifyFosterHomeViewmodel.saveFosterHomeChanges(
                            isDifferentImage = imageUrl != fosterHome.imageUrl,
                            modifiedFosterHome = fosterHome.copy(
                                imageUrl = imageUrl,
                                title = title,
                                description = description,
                                conditions = conditions,
                                allAcceptedNonHumanAnimals = allAcceptedNonHumanAnimals,
                                allResidentNonHumanAnimals = allResidentNonHumanAnimals,
                                available = isAvailable
                            )
                        )
                    }
                )
            }
        }
    }
}
