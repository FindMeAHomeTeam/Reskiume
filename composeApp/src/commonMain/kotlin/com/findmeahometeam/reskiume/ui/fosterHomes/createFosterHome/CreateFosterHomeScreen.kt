package com.findmeahometeam.reskiume.ui.fosterHomes.createFosterHome

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
import androidx.compose.runtime.State
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimal
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimalListSaver
import com.findmeahometeam.reskiume.domain.model.fosterHome.AcceptedNonHumanAnimalForFosterHome
import com.findmeahometeam.reskiume.domain.model.fosterHome.AcceptedNonHumanAnimalForFosterHomeListSaver
import com.findmeahometeam.reskiume.domain.model.fosterHome.City
import com.findmeahometeam.reskiume.domain.model.fosterHome.Country
import com.findmeahometeam.reskiume.domain.model.fosterHome.FosterHome
import com.findmeahometeam.reskiume.domain.model.fosterHome.ResidentNonHumanAnimalForFosterHome
import com.findmeahometeam.reskiume.ui.core.backgroundColor
import com.findmeahometeam.reskiume.ui.core.components.ManagePermissionState
import com.findmeahometeam.reskiume.ui.core.components.RmAcceptedNonHumanAnimalListCreator
import com.findmeahometeam.reskiume.ui.core.components.RmAddPhoto
import com.findmeahometeam.reskiume.ui.core.components.RmButton
import com.findmeahometeam.reskiume.ui.core.components.RmCountryAndCitySelectors
import com.findmeahometeam.reskiume.ui.core.components.RmManageLocationPermission
import com.findmeahometeam.reskiume.ui.core.components.RmNonHumanAnimalListCreator
import com.findmeahometeam.reskiume.ui.core.components.RmResultState
import com.findmeahometeam.reskiume.ui.core.components.RmScaffold
import com.findmeahometeam.reskiume.ui.core.components.RmTextField
import com.findmeahometeam.reskiume.ui.core.components.UiState
import com.findmeahometeam.reskiume.ui.fosterHomes.checkAllFosterHomes.PlaceUtil
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import reskiume.composeapp.generated.resources.Res
import reskiume.composeapp.generated.resources.create_foster_home_screen_create_a_foster_home_button
import reskiume.composeapp.generated.resources.create_foster_home_screen_create_the_foster_home_button
import reskiume.composeapp.generated.resources.create_foster_home_screen_foster_home_conditions
import reskiume.composeapp.generated.resources.create_foster_home_screen_foster_home_description
import reskiume.composeapp.generated.resources.create_foster_home_screen_foster_home_title
import reskiume.composeapp.generated.resources.create_foster_home_screen_title
import reskiume.composeapp.generated.resources.foster_home
import reskiume.composeapp.generated.resources.manage_location_permission_message
import reskiume.composeapp.generated.resources.manage_location_permission_turn_on_location_message
import reskiume.composeapp.generated.resources.non_human_animal_list_creator_resident_title

@Composable
fun CreateFosterHomeScreen(
    onBackPressed: () -> Unit
) {
    val createFosterHomeViewmodel: CreateFosterHomeViewmodel =
        koinViewModel<CreateFosterHomeViewmodel>()

    val placeUtil: PlaceUtil = koinInject<PlaceUtil>()

    val allAvailableUiNonHumanAnimals: List<NonHumanAnimal> by createFosterHomeViewmodel.allAvailableNonHumanAnimalsWhoNeedToBeRehomedFlow.collectAsStateWithLifecycle(
        initialValue = emptyList()
    )
    val manageChangesUiState: UiState<Unit> by createFosterHomeViewmodel.saveChangesUiState.collectAsState()

    var title: String by rememberSaveable { mutableStateOf("") }
    var description: String by rememberSaveable { mutableStateOf("") }
    var conditions: String by rememberSaveable { mutableStateOf("") }
    var imageUrl: String by rememberSaveable { mutableStateOf("") }
    var allAcceptedNonHumanAnimals: List<AcceptedNonHumanAnimalForFosterHome> by rememberSaveable(
        stateSaver = AcceptedNonHumanAnimalForFosterHomeListSaver
    ) {
        mutableStateOf(emptyList())
    }
    var allResidentUiNonHumanAnimals: List<NonHumanAnimal> by rememberSaveable(stateSaver = NonHumanAnimalListSaver) {
        mutableStateOf(emptyList())
    }
    var selectedCountry: Country by rememberSaveable { mutableStateOf(Country.UNSELECTED) }
    var selectedCity: City by rememberSaveable { mutableStateOf(City.UNSELECTED) }

    var permissionState: ManagePermissionState by rememberSaveable {
        mutableStateOf(ManagePermissionState.CHECK_PERMISSION)
    }
    val isLocationEnabledState: State<Boolean> =
        createFosterHomeViewmodel.observeIfLocationEnabled().collectAsState(initial = false)

    val isCreateFosterHomeButtonEnabled by remember(
        title,
        description,
        conditions,
        imageUrl,
        allAcceptedNonHumanAnimals,
        selectedCountry,
        selectedCity,
        permissionState,
        isLocationEnabledState
    ) {
        derivedStateOf {
            imageUrl.isNotBlank()
                    && title.isNotBlank()
                    && description.isNotBlank()
                    && conditions.isNotBlank()
                    && allAcceptedNonHumanAnimals.isNotEmpty()
                    && selectedCountry != Country.UNSELECTED
                    && selectedCity != City.UNSELECTED
                    && permissionState == ManagePermissionState.PERMISSION_GRANTED
                    && isLocationEnabledState.value
        }
    }

    val scrollState = rememberScrollState()

    RmScaffold(
        title = stringResource(Res.string.create_foster_home_screen_title),
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
            RmAddPhoto(currentImageUri = imageUrl) {
                imageUrl = it
            }

            Spacer(modifier = Modifier.height(8.dp))
            RmCountryAndCitySelectors(
                placeUtil,
                selectedCountry,
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
                    stringResource(Res.string.foster_home)
                ),
                explainingLocationActivationMessage = stringResource(
                    Res.string.manage_location_permission_message,
                    stringResource(Res.string.foster_home)
                ),
                permissionState = permissionState,
                isLocationEnabledState = isLocationEnabledState,
                onRequestEnableLocation = {
                    createFosterHomeViewmodel.requestEnableLocation()
                },
                onUpdateLocation = {
                    createFosterHomeViewmodel.updateLocation()
                },
                onBackPressed = onBackPressed,
                onUpdatePermissionState = {
                    if (it == ManagePermissionState.IDLE) {
                        onBackPressed()
                    } else {
                        permissionState = it
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))
            RmTextField(
                modifier = Modifier.fillMaxWidth(),
                text = title,
                label = stringResource(Res.string.create_foster_home_screen_foster_home_title),
                onValueChange = { title = it }
            )

            Spacer(modifier = Modifier.height(8.dp))
            RmTextField(
                modifier = Modifier.fillMaxWidth().height(100.dp),
                text = description,
                label = stringResource(Res.string.create_foster_home_screen_foster_home_description),
                onValueChange = { description = it }
            )

            Spacer(modifier = Modifier.height(8.dp))
            RmTextField(
                modifier = Modifier.fillMaxWidth().height(100.dp),
                text = conditions,
                label = stringResource(Res.string.create_foster_home_screen_foster_home_conditions),
                onValueChange = { conditions = it }
            )

            Spacer(modifier = Modifier.height(16.dp))
            RmAcceptedNonHumanAnimalListCreator(
                fosterHomeId = "",
                allAcceptedNonHumanAnimals = allAcceptedNonHumanAnimals
            ) {
                allAcceptedNonHumanAnimals = it
            }

            Spacer(modifier = Modifier.height(16.dp))
            RmNonHumanAnimalListCreator(
                title = stringResource(Res.string.non_human_animal_list_creator_resident_title),
                allAvailableNonHumanAnimals = allAvailableUiNonHumanAnimals.minus(
                    allResidentUiNonHumanAnimals.toSet()
                ),
                allExistentNonHumanAnimals = allResidentUiNonHumanAnimals
            ) {
                allResidentUiNonHumanAnimals = it
            }

            Spacer(modifier = Modifier.height(10.dp))
            RmResultState(manageChangesUiState, onSuccess = { onBackPressed() })
            Spacer(modifier = Modifier.height(10.dp))

            Spacer(modifier = Modifier.weight(1f))
            RmButton(
                text = stringResource(
                    if (title.isBlank()) {
                        Res.string.create_foster_home_screen_create_a_foster_home_button
                    } else {
                        Res.string.create_foster_home_screen_create_the_foster_home_button
                    },
                    title
                ),
                enabled = isCreateFosterHomeButtonEnabled,
                onClick = {
                    createFosterHomeViewmodel.createFosterHome(
                        FosterHome(
                            id = "",
                            ownerId = "",
                            title = title,
                            description = description,
                            conditions = conditions,
                            imageUrl = imageUrl,
                            allAcceptedNonHumanAnimals = allAcceptedNonHumanAnimals,
                            allResidentNonHumanAnimals = allResidentUiNonHumanAnimals.map {
                                ResidentNonHumanAnimalForFosterHome(
                                    nonHumanAnimalId = it.id,
                                    caregiverId = it.caregiverId,
                                    fosterHomeId = ""
                                )
                            },
                            available = true,
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
