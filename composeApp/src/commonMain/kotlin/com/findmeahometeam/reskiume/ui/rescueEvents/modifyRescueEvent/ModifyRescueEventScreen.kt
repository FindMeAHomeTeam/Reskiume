package com.findmeahometeam.reskiume.ui.rescueEvents.modifyRescueEvent

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimal
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimalListSaver
import com.findmeahometeam.reskiume.domain.model.fosterHome.City
import com.findmeahometeam.reskiume.domain.model.fosterHome.Country
import com.findmeahometeam.reskiume.domain.model.fosterHome.toStringResource
import com.findmeahometeam.reskiume.domain.model.rescueEvent.NeedToCover
import com.findmeahometeam.reskiume.domain.model.rescueEvent.NeedToCoverListSaver
import com.findmeahometeam.reskiume.domain.model.rescueEvent.NonHumanAnimalToRescue
import com.findmeahometeam.reskiume.ui.core.backgroundColor
import com.findmeahometeam.reskiume.ui.core.components.ManagePermissionState
import com.findmeahometeam.reskiume.ui.core.components.MaxCharacters
import com.findmeahometeam.reskiume.ui.core.components.RmAddPhoto
import com.findmeahometeam.reskiume.ui.core.components.RmButton
import com.findmeahometeam.reskiume.ui.core.components.RmDialog
import com.findmeahometeam.reskiume.ui.core.components.RmManageNotificationPermission
import com.findmeahometeam.reskiume.ui.core.components.RmNeedToCoverListCreator
import com.findmeahometeam.reskiume.ui.core.components.RmNonHumanAnimalListCreator
import com.findmeahometeam.reskiume.ui.core.components.RmResultState
import com.findmeahometeam.reskiume.ui.core.components.RmScaffold
import com.findmeahometeam.reskiume.ui.core.components.RmText
import com.findmeahometeam.reskiume.ui.core.components.RmTextField
import com.findmeahometeam.reskiume.ui.core.components.RmTextLink
import com.findmeahometeam.reskiume.ui.core.components.UiState
import com.findmeahometeam.reskiume.ui.profile.checkAllMyRescueEvents.UiRescueEvent
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import reskiume.composeapp.generated.resources.Res
import reskiume.composeapp.generated.resources.modify_rescue_event_screen_delete_rescue_event_button
import reskiume.composeapp.generated.resources.modify_rescue_event_screen_delete_rescue_event_message
import reskiume.composeapp.generated.resources.modify_rescue_event_screen_delete_rescue_event_text
import reskiume.composeapp.generated.resources.modify_rescue_event_screen_delete_rescue_event_title
import reskiume.composeapp.generated.resources.modify_rescue_event_screen_dismiss_delete_rescue_event_button
import reskiume.composeapp.generated.resources.modify_rescue_event_screen_rescue_event_description
import reskiume.composeapp.generated.resources.modify_rescue_event_screen_rescue_event_title
import reskiume.composeapp.generated.resources.modify_rescue_event_screen_save_rescue_event_changes_button
import reskiume.composeapp.generated.resources.modify_rescue_event_screen_title
import reskiume.composeapp.generated.resources.non_human_animal_list_creator_save_title

@Composable
fun ModifyRescueEventScreen(
    onBackPressed: () -> Unit
) {
    val modifyRescueEventViewmodel: ModifyRescueEventViewmodel =
        koinViewModel<ModifyRescueEventViewmodel>()

    val uiRescueEventState: UiState<UiRescueEvent> by modifyRescueEventViewmodel.rescueEventFlow.collectAsState(
        initial = UiState.Loading()
    )
    val allAvailableNonHumanAnimals: List<NonHumanAnimal> by modifyRescueEventViewmodel.allAvailableNonHumanAnimalsWhoNeedToBeRehomedFlow.collectAsState(
        initial = emptyList()
    )
    val manageChangesUiState: UiState<Unit> by modifyRescueEventViewmodel.manageChangesUiState.collectAsState()

    var notificationPermissionState: ManagePermissionState by rememberSaveable {
        mutableStateOf(ManagePermissionState.CHECK_PERMISSION)
    }
    val scrollState = rememberScrollState()

    RmScaffold(
        title =
            if (uiRescueEventState is UiState.Success) {
                stringResource(
                    Res.string.modify_rescue_event_screen_title,
                    (uiRescueEventState as UiState.Success<UiRescueEvent>).data.rescueEvent.title,
                    stringResource(
                        City
                            .valueOf((uiRescueEventState as UiState.Success<UiRescueEvent>).data.rescueEvent.city)
                            .toStringResource()
                    ).substring(5),
                    stringResource(
                        Country
                            .valueOf((uiRescueEventState as UiState.Success<UiRescueEvent>).data.rescueEvent.country)
                            .toStringResource()
                    ).substring(5)
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
            RmResultState(uiRescueEventState) { uiRescueEvent: UiRescueEvent ->

                var title: String by rememberSaveable { mutableStateOf(uiRescueEvent.rescueEvent.title) }
                var description: String by rememberSaveable { mutableStateOf(uiRescueEvent.rescueEvent.description) }
                var imageUrl: String by rememberSaveable { mutableStateOf(uiRescueEvent.rescueEvent.imageUrl) }
                var allNeedsToCover: List<NeedToCover> by rememberSaveable(
                    stateSaver = NeedToCoverListSaver
                ) {
                    mutableStateOf(
                        uiRescueEvent.rescueEvent.allNeedsToCover
                    )
                }
                var allUiNonHumanAnimalsToRescue: List<NonHumanAnimal> by rememberSaveable(
                    stateSaver = NonHumanAnimalListSaver
                ) {
                    mutableStateOf(
                        uiRescueEvent.allUiNonHumanAnimalsToRescue
                    )
                }
                var displayDeleteDialog: Boolean by rememberSaveable { mutableStateOf(false) }

                val isUpdateRescueEventButtonEnabled by remember(
                    title,
                    description,
                    imageUrl,
                    allNeedsToCover,
                    allUiNonHumanAnimalsToRescue
                ) {
                    derivedStateOf {
                        imageUrl.isNotBlank()
                                && title.isNotBlank()
                                && description.isNotBlank()
                                && allNeedsToCover.isNotEmpty()
                                && allUiNonHumanAnimalsToRescue.isNotEmpty()
                                && (imageUrl != uiRescueEvent.rescueEvent.imageUrl
                                || title != uiRescueEvent.rescueEvent.title
                                || description != uiRescueEvent.rescueEvent.description
                                || allNeedsToCover != uiRescueEvent.rescueEvent.allNeedsToCover
                                || allUiNonHumanAnimalsToRescue != uiRescueEvent.allUiNonHumanAnimalsToRescue)
                    }
                }

                if (notificationPermissionState != ManagePermissionState.PERMISSION_GRANTED) {
                    RmManageNotificationPermission(
                        permissionState = notificationPermissionState,
                        onUpdatePermissionState = {
                            notificationPermissionState = it
                        }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                RmAddPhoto(
                    currentImageUri = imageUrl,
                    onUriRetrieved = {
                        imageUrl = it
                    },
                    onDeleteDiscardedImage = {
                        modifyRescueEventViewmodel.deleteLocalImage(it)
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))
                RmTextField(
                    modifier = Modifier.fillMaxWidth(),
                    text = title,
                    maxCharacters = MaxCharacters.TITLE,
                    label = stringResource(Res.string.modify_rescue_event_screen_rescue_event_title),
                    onValueChange = { title = it },
                    supportingText = {
                        RmText(
                            modifier = Modifier.fillMaxWidth(),
                            text = "${title.length} / ${MaxCharacters.TITLE.max}",
                            textAlign = TextAlign.End,
                        )
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))
                RmTextField(
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    text = description,
                    label = stringResource(Res.string.modify_rescue_event_screen_rescue_event_description),
                    onValueChange = { description = it }
                )

                Spacer(modifier = Modifier.height(16.dp))
                RmNeedToCoverListCreator(
                    rescueEventId = uiRescueEvent.rescueEvent.id,
                    allNeedsToCover = allNeedsToCover
                ) {
                    allNeedsToCover = it
                }

                Spacer(modifier = Modifier.height(16.dp))
                RmNonHumanAnimalListCreator(
                    title = stringResource(Res.string.non_human_animal_list_creator_save_title),
                    allAvailableNonHumanAnimals = allAvailableNonHumanAnimals,
                    allExistentNonHumanAnimals = allUiNonHumanAnimalsToRescue
                ) {
                    allUiNonHumanAnimalsToRescue = it
                }

                Spacer(modifier = Modifier.height(20.dp))
                RmTextLink(
                    text = stringResource(
                        Res.string.modify_rescue_event_screen_delete_rescue_event_text,
                        title
                    ),
                    textToLink = stringResource(Res.string.modify_rescue_event_screen_delete_rescue_event_button),
                    onClick = {
                        displayDeleteDialog = true
                    }
                )
                if (displayDeleteDialog) {
                    RmDialog(
                        emoji = "🗑️",
                        title = stringResource(
                            Res.string.modify_rescue_event_screen_delete_rescue_event_title,
                            title
                        ),
                        message = stringResource(Res.string.modify_rescue_event_screen_delete_rescue_event_message),
                        allowMessage = stringResource(Res.string.modify_rescue_event_screen_delete_rescue_event_button),
                        denyMessage = stringResource(Res.string.modify_rescue_event_screen_dismiss_delete_rescue_event_button),
                        onClickAllow = {
                            modifyRescueEventViewmodel.deleteRescueEvent(
                                uiRescueEvent.rescueEvent.id,
                                uiRescueEvent.rescueEvent.creatorId
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
                        Res.string.modify_rescue_event_screen_save_rescue_event_changes_button,
                        title
                    ),
                    enabled = isUpdateRescueEventButtonEnabled,
                    onClick = {
                        modifyRescueEventViewmodel.saveRescueEventChanges(
                            isDifferentImage = imageUrl != uiRescueEvent.rescueEvent.imageUrl,
                            modifiedRescueEvent = uiRescueEvent.rescueEvent.copy(
                                imageUrl = imageUrl,
                                title = title,
                                description = description,
                                allNeedsToCover = allNeedsToCover,
                                allNonHumanAnimalsToRescue = allUiNonHumanAnimalsToRescue.map {

                                    NonHumanAnimalToRescue(
                                        nonHumanAnimalId = it.id,
                                        caregiverId = it.caregiverId,
                                        rescueEventId = uiRescueEvent.rescueEvent.id
                                    )
                                }
                            )
                        )
                    }
                )
            }
        }
    }
}
