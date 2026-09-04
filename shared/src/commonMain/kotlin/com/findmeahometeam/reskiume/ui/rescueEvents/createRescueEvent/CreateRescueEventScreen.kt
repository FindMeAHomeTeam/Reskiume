package com.findmeahometeam.reskiume.ui.rescueEvents.createRescueEvent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimal
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimalListSaver
import com.findmeahometeam.reskiume.domain.model.fosterHome.City
import com.findmeahometeam.reskiume.domain.model.fosterHome.Country
import com.findmeahometeam.reskiume.domain.model.rescueEvent.NeedToCover
import com.findmeahometeam.reskiume.domain.model.rescueEvent.NeedToCoverListSaver
import com.findmeahometeam.reskiume.domain.model.rescueEvent.NonHumanAnimalToRescue
import com.findmeahometeam.reskiume.domain.model.rescueEvent.RescueEvent
import com.findmeahometeam.reskiume.domain.model.user.User
import com.findmeahometeam.reskiume.ui.core.backgroundColor
import com.findmeahometeam.reskiume.ui.core.components.ManagePermissionState
import com.findmeahometeam.reskiume.ui.core.components.MaxCharacters
import com.findmeahometeam.reskiume.ui.core.components.RmAddPhoto
import com.findmeahometeam.reskiume.ui.core.components.RmButton
import com.findmeahometeam.reskiume.ui.core.components.RmCountryAndCitySelectors
import com.findmeahometeam.reskiume.ui.core.components.RmManageLocationPermission
import com.findmeahometeam.reskiume.ui.core.components.RmManageNotificationPermission
import com.findmeahometeam.reskiume.ui.core.components.RmNeedToCoverListCreator
import com.findmeahometeam.reskiume.ui.core.components.RmNonHumanAnimalListCreator
import com.findmeahometeam.reskiume.ui.core.components.RmResultState
import com.findmeahometeam.reskiume.ui.core.components.RmScaffold
import com.findmeahometeam.reskiume.ui.core.components.RmText
import com.findmeahometeam.reskiume.ui.core.components.RmTextField
import com.findmeahometeam.reskiume.ui.core.components.RmTextLink
import com.findmeahometeam.reskiume.ui.core.components.UiState
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import reskiume.shared.generated.resources.Res
import reskiume.shared.generated.resources.create_rescue_event_screen_create_a_rescue_event_button
import reskiume.shared.generated.resources.create_rescue_event_screen_create_the_rescue_event_button
import reskiume.shared.generated.resources.create_rescue_event_screen_can_register_non_human_animal_button
import reskiume.shared.generated.resources.create_rescue_event_screen_can_register_non_human_animal_label
import reskiume.shared.generated.resources.create_rescue_event_screen_must_register_non_human_animal_label
import reskiume.shared.generated.resources.create_rescue_event_screen_rescue_event_description
import reskiume.shared.generated.resources.create_rescue_event_screen_rescue_event_title
import reskiume.shared.generated.resources.create_rescue_event_screen_title
import reskiume.shared.generated.resources.manage_location_permission_message
import reskiume.shared.generated.resources.manage_location_permission_turn_on_location_message
import reskiume.shared.generated.resources.non_human_animal_list_creator_save_title
import reskiume.shared.generated.resources.rescue_event

@Composable
fun CreateRescueEventScreen(
    onBackPressed: () -> Unit,
    onCreateNonHumanAnimal: () -> Unit
) {
    val createRescueEventViewmodel: CreateRescueEventViewmodel =
        koinViewModel<CreateRescueEventViewmodel>()

    val allAvailableNonHumanAnimals: List<NonHumanAnimal> by createRescueEventViewmodel.allAvailableNonHumanAnimalsWhoNeedToBeRehomedFlow.collectAsStateWithLifecycle(
        initialValue = emptyList()
    )
    val manageChangesUiState: UiState<Unit> by createRescueEventViewmodel.saveChangesUiState.collectAsStateWithLifecycle()

    var title: String by rememberSaveable { mutableStateOf("") }
    var description: String by rememberSaveable { mutableStateOf("") }
    var imageUrl: String by rememberSaveable { mutableStateOf("") }
    var allNeedsToCover: List<NeedToCover> by rememberSaveable(stateSaver = NeedToCoverListSaver) {
        mutableStateOf(emptyList())
    }
    var uiAllNonHumanAnimalsToRescue: List<NonHumanAnimal> by rememberSaveable(stateSaver = NonHumanAnimalListSaver) {
        mutableStateOf(emptyList())
    }
    val user: User? by createRescueEventViewmodel.userState.collectAsStateWithLifecycle(
        initialValue = null
    )
    var selectedCountry: Country by rememberSaveable(user) {
        mutableStateOf(
            if (user == null) {
                Country.UNSELECTED
            } else {
                Country.valueOf(user!!.countryForRescueEventNotifications)
            }
        )
    }
    var selectedCity: City by rememberSaveable(user) {
        mutableStateOf(
            if (user == null) {
                City.UNSELECTED
            } else {
                City.valueOf(user!!.cityForRescueEventNotifications)
            }
        )
    }
    var locationPermissionState: ManagePermissionState by rememberSaveable {
        mutableStateOf(ManagePermissionState.CHECK_PERMISSION)
    }
    var notificationPermissionState: ManagePermissionState by rememberSaveable {
        mutableStateOf(ManagePermissionState.CHECK_PERMISSION)
    }
    val isLocationEnabledState: State<Boolean> =
        createRescueEventViewmodel.observeIfLocationEnabled().collectAsStateWithLifecycle(initialValue = false)

    val isCreateRescueEventButtonEnabled by remember(
        title,
        description,
        imageUrl,
        uiAllNonHumanAnimalsToRescue,
        allNeedsToCover,
        selectedCountry,
        selectedCity,
        locationPermissionState,
        notificationPermissionState,
        isLocationEnabledState.value
    ) {
        derivedStateOf {
            imageUrl.isNotBlank()
                    && title.isNotBlank()
                    && description.isNotBlank()
                    && uiAllNonHumanAnimalsToRescue.isNotEmpty()
                    && allNeedsToCover.isNotEmpty()
                    && selectedCountry != Country.UNSELECTED
                    && selectedCity != City.UNSELECTED
                    && locationPermissionState == ManagePermissionState.PERMISSION_GRANTED
                    && notificationPermissionState == ManagePermissionState.PERMISSION_GRANTED
                    && isLocationEnabledState.value
        }
    }

    val scrollState = rememberScrollState()

    RmScaffold(
        title = stringResource(Res.string.create_rescue_event_screen_title),
        onBackPressed = onBackPressed,
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(padding)
                .consumeWindowInsets(padding)
                .imePadding()
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            RmAddPhoto(
                currentImageUri = imageUrl,
                onUriRetrieved = {
                    imageUrl = it
                },
                onDeleteDiscardedImage = {
                    createRescueEventViewmodel.deleteLocalImage(it)
                }
            )

            Spacer(modifier = Modifier.height(8.dp))
            RmCountryAndCitySelectors(
                selectedCountry = selectedCountry,
                selectedCity = selectedCity,
                onSelectedCountry = {
                    selectedCountry = it
                },
                onSelectedCity = {
                    selectedCity = it
                }
            )

            RmManageLocationPermission(
                explainingLocationPermissionMessage = stringResource(
                    Res.string.manage_location_permission_turn_on_location_message,
                    stringResource(Res.string.rescue_event)
                ),
                explainingLocationActivationMessage = stringResource(
                    Res.string.manage_location_permission_message,
                    stringResource(Res.string.rescue_event)
                ),
                permissionState = locationPermissionState,
                isLocationEnabledState = isLocationEnabledState,
                onRequestEnableLocation = {
                    createRescueEventViewmodel.requestEnableLocation()
                },
                onUpdateLocation = {
                    createRescueEventViewmodel.updateLocation()
                },
                onBackPressed = onBackPressed,
                onUpdatePermissionState = {
                    if (it == ManagePermissionState.IDLE) {
                        onBackPressed()
                    } else {
                        locationPermissionState = it
                    }
                }
            )
            if (locationPermissionState == ManagePermissionState.PERMISSION_GRANTED && notificationPermissionState != ManagePermissionState.PERMISSION_GRANTED) {
                RmManageNotificationPermission(
                    permissionState = notificationPermissionState,
                    onUpdatePermissionState = {
                        if (it == ManagePermissionState.IDLE) {
                            onBackPressed()
                        } else {
                            notificationPermissionState = it
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            RmTextField(
                modifier = Modifier.fillMaxWidth(),
                text = title,
                maxCharacters = MaxCharacters.TITLE,
                label = stringResource(Res.string.create_rescue_event_screen_rescue_event_title),
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
                label = stringResource(Res.string.create_rescue_event_screen_rescue_event_description),
                onValueChange = { description = it }
            )

            Spacer(modifier = Modifier.height(16.dp))
            RmNeedToCoverListCreator(
                "",
                emptyList()
            ) {
                allNeedsToCover = it
            }

            if (allAvailableNonHumanAnimals.isNotEmpty()) {

                Spacer(modifier = Modifier.height(16.dp))
                RmNonHumanAnimalListCreator(
                    title = stringResource(Res.string.non_human_animal_list_creator_save_title),
                    allAvailableNonHumanAnimals = allAvailableNonHumanAnimals,
                    allSelectedNonHumanAnimals = emptyList()
                ) {
                    uiAllNonHumanAnimalsToRescue = it
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            RmTextLink(
                modifier = Modifier.fillMaxWidth().padding(10.dp),
                text = if (allAvailableNonHumanAnimals.isEmpty()) {
                    stringResource(Res.string.create_rescue_event_screen_must_register_non_human_animal_label)
                } else {
                    stringResource(Res.string.create_rescue_event_screen_can_register_non_human_animal_label)
                },
                textToLink = stringResource(Res.string.create_rescue_event_screen_can_register_non_human_animal_button),
                textAlign = TextAlign.Start,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                onClick = onCreateNonHumanAnimal
            )

            Spacer(modifier = Modifier.height(10.dp))
            RmResultState(manageChangesUiState, onSuccess = { onBackPressed() })
            Spacer(modifier = Modifier.height(10.dp))

            Spacer(modifier = Modifier.weight(1f))
            RmButton(
                text = stringResource(
                    if (title.isBlank()) {
                        Res.string.create_rescue_event_screen_create_a_rescue_event_button
                    } else {
                        Res.string.create_rescue_event_screen_create_the_rescue_event_button
                    },
                    title
                ),
                enabled = isCreateRescueEventButtonEnabled,
                onClick = {
                    createRescueEventViewmodel.createRescueEvent(
                        RescueEvent(
                            id = "",
                            creatorId = "",
                            title = title,
                            description = description,
                            imageUrl = imageUrl,
                            allNeedsToCover = allNeedsToCover,
                            allNonHumanAnimalsToRescue = uiAllNonHumanAnimalsToRescue.map {
                                NonHumanAnimalToRescue(
                                    nonHumanAnimalId = it.id,
                                    caregiverId = it.caregiverId,
                                    rescueEventId = ""
                                )
                            },
                            longitude = 0.0,
                            latitude = 0.0,
                            country = selectedCountry.name,
                            city = selectedCity.name
                        )
                    )
                }
            )
        }
    }
}
