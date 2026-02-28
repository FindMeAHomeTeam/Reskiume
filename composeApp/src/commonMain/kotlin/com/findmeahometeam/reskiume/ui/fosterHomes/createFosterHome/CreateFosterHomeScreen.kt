package com.findmeahometeam.reskiume.ui.fosterHomes.createFosterHome

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimal
import com.findmeahometeam.reskiume.domain.model.fosterHome.AcceptedNonHumanAnimalForFosterHome
import com.findmeahometeam.reskiume.domain.model.fosterHome.City
import com.findmeahometeam.reskiume.domain.model.fosterHome.Country
import com.findmeahometeam.reskiume.domain.model.fosterHome.FosterHome
import com.findmeahometeam.reskiume.domain.model.fosterHome.ResidentNonHumanAnimalForFosterHome
import com.findmeahometeam.reskiume.ui.core.backgroundColor
import com.findmeahometeam.reskiume.ui.core.components.ManagePermissionState
import com.findmeahometeam.reskiume.ui.core.components.RmAcceptedNonHumanAnimalListCreator
import com.findmeahometeam.reskiume.ui.core.components.RmAddPhoto
import com.findmeahometeam.reskiume.ui.core.components.RmButton
import com.findmeahometeam.reskiume.ui.core.components.RmDialog
import com.findmeahometeam.reskiume.ui.core.components.RmManagePermission
import com.findmeahometeam.reskiume.ui.core.components.RmResidentNonHumanAnimalListCreator
import com.findmeahometeam.reskiume.ui.core.components.RmResultState
import com.findmeahometeam.reskiume.ui.core.components.RmScaffold
import com.findmeahometeam.reskiume.ui.core.components.RmSearchBarWithSuggestions
import com.findmeahometeam.reskiume.ui.core.components.RmTextField
import com.findmeahometeam.reskiume.ui.core.components.StringsForDialog
import com.findmeahometeam.reskiume.ui.core.components.UiState
import com.findmeahometeam.reskiume.ui.fosterHomes.checkAllFosterHomes.PlaceUtil
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.location.COARSE_LOCATION
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import reskiume.composeapp.generated.resources.Res
import reskiume.composeapp.generated.resources.create_foster_home_location_permission_title
import reskiume.composeapp.generated.resources.create_foster_home_screen_create_a_foster_home_button
import reskiume.composeapp.generated.resources.create_foster_home_screen_create_the_foster_home_button
import reskiume.composeapp.generated.resources.create_foster_home_screen_foster_home_city
import reskiume.composeapp.generated.resources.create_foster_home_screen_foster_home_conditions
import reskiume.composeapp.generated.resources.create_foster_home_screen_foster_home_country
import reskiume.composeapp.generated.resources.create_foster_home_screen_foster_home_description
import reskiume.composeapp.generated.resources.create_foster_home_screen_foster_home_title
import reskiume.composeapp.generated.resources.create_foster_home_screen_title
import reskiume.composeapp.generated.resources.create_foster_home_location_go_to_settings_title
import reskiume.composeapp.generated.resources.create_foster_home_location_grant_in_settings_message
import reskiume.composeapp.generated.resources.create_foster_home_location_open_settings_button
import reskiume.composeapp.generated.resources.create_foster_home_location_permission_do_not_grant_permission_button
import reskiume.composeapp.generated.resources.create_foster_home_location_permission_grant_permission_button
import reskiume.composeapp.generated.resources.create_foster_home_location_permission_message
import reskiume.composeapp.generated.resources.create_foster_home_location_turn_on_later_location_button
import reskiume.composeapp.generated.resources.create_foster_home_location_turn_on_location_message
import reskiume.composeapp.generated.resources.create_foster_home_location_turn_on_location_open_settings_button
import reskiume.composeapp.generated.resources.create_foster_home_location_turn_on_location_title

@Composable
fun CreateFosterHomeScreen(
    onBackPressed: () -> Unit
) {
    val createFosterHomeViewmodel: CreateFosterHomeViewmodel =
        koinViewModel<CreateFosterHomeViewmodel>()

    val placeUtil: PlaceUtil = koinInject<PlaceUtil>()

    val allAvailableNonHumanAnimals: List<NonHumanAnimal> by createFosterHomeViewmodel.allAvailableNonHumanAnimalsLookingForAdoptionFlow.collectAsState(
        initial = emptyList()
    )
    val manageChangesUiState: UiState<Unit> by createFosterHomeViewmodel.saveChangesUiState.collectAsState()

    var title: String by rememberSaveable { mutableStateOf("") }
    var description: String by rememberSaveable { mutableStateOf("") }
    var conditions: String by rememberSaveable { mutableStateOf("") }
    var imageUrl: String by rememberSaveable { mutableStateOf("") }
    var allAcceptedNonHumanAnimals: List<AcceptedNonHumanAnimalForFosterHome> by rememberSaveable {
        mutableStateOf(emptyList())
    }
    var uiAllResidentNonHumanAnimals: List<NonHumanAnimal> by rememberSaveable {
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
            RmAddPhoto {
                imageUrl = it
            }

            DisplayCountryAndCitySelectors(
                placeUtil,
                selectedCountry,
                onSelectedCountry = {
                    selectedCountry = it
                },
                onSelectedCity = {
                    selectedCity = it
                }
            )

            ManageLocationPermission(
                permissionState,
                isLocationEnabledState,
                createFosterHomeViewmodel,
                onBackPressed,
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
                modifier = Modifier.fillMaxWidth(),
                text = description,
                label = stringResource(Res.string.create_foster_home_screen_foster_home_description),
                onValueChange = { description = it }
            )

            Spacer(modifier = Modifier.height(8.dp))
            RmTextField(
                modifier = Modifier.fillMaxWidth(),
                text = conditions,
                label = stringResource(Res.string.create_foster_home_screen_foster_home_conditions),
                onValueChange = { conditions = it }
            )

            Spacer(modifier = Modifier.height(8.dp))
            RmAcceptedNonHumanAnimalListCreator(
                "",
                emptyList()
            ) {
                allAcceptedNonHumanAnimals = it
            }

            Spacer(modifier = Modifier.height(8.dp))
            RmResidentNonHumanAnimalListCreator(
                allAvailableNonHumanAnimals,
                emptyList()
            ) {
                uiAllResidentNonHumanAnimals = it
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
                            allResidentNonHumanAnimals = uiAllResidentNonHumanAnimals.map {
                                ResidentNonHumanAnimalForFosterHome(
                                    nonHumanAnimalId = it.id,
                                    caregiverId = it.caregiverId,
                                    fosterHomeId = ""
                                )
                            },
                            available = true,
                            longitude = 0.0,
                            latitude = 0.0,
                            country = selectedCountry.countryName,
                            city = selectedCity.cityName
                        )
                    )
                }
            )
        }
    }
}

@Composable
fun DisplayCountryAndCitySelectors(
    placeUtil: PlaceUtil,
    selectedCountry: Country,
    onSelectedCountry: (country: Country) -> Unit = {},
    onSelectedCity: (city: City) -> Unit = {}
) {
    var isCountryVisible: Boolean by rememberSaveable { mutableStateOf(true) }
    val countryFieldState = rememberTextFieldState()
    val countryItems: List<Pair<Country, String>> by placeUtil.allCountryItems()
        .collectAsState(initial = emptyList())
    var isCityVisible: Boolean by rememberSaveable { mutableStateOf(false) }
    val cityFieldState = rememberTextFieldState()
    val cityItems: List<Pair<City, String>> by placeUtil.allCityItems(
        selectedCountry
    ).collectAsState(initial = emptyList())

    Column(
        modifier = Modifier.heightIn(min = 100.dp, max = 300.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isCountryVisible) {
                RmSearchBarWithSuggestions(
                    modifier = Modifier.weight(1f),
                    onFocusChanged = {
                        if (it.isFocused) {
                            isCityVisible = false
                        }
                    },
                    textFieldState = countryFieldState,
                    placeholder = stringResource(Res.string.create_foster_home_screen_foster_home_country),
                    items = countryItems,
                    onSearch = { country: Country? ->
                        country?.let { onSelectedCountry(it) }
                        if (country == null) {
                            onSelectedCountry(Country.UNSELECTED)
                            onSelectedCity(City.UNSELECTED)
                            cityFieldState.setTextAndPlaceCursorAtEnd("")
                        }
                        isCityVisible = country != null
                    }
                )
            }

            if (isCountryVisible && isCityVisible) {
                Spacer(modifier = Modifier.width(8.dp))
            }

            if (isCityVisible && cityItems.isNotEmpty()) {
                RmSearchBarWithSuggestions(
                    modifier = Modifier.weight(1f),
                    onFocusChanged = {
                        if (it.isFocused) {
                            isCountryVisible = false
                        }
                    },
                    textFieldState = cityFieldState,
                    placeholder = stringResource(Res.string.create_foster_home_screen_foster_home_city),
                    items = cityItems,
                    onSearch = { city: City? ->
                        city?.let { onSelectedCity(it) }
                        if (city == null) {
                            onSelectedCity(City.UNSELECTED)
                            cityFieldState.setTextAndPlaceCursorAtEnd("")
                        }
                        isCountryVisible = true
                    }
                )
            }
        }
    }
}

@Composable
fun ManageLocationPermission(
    permissionState: ManagePermissionState,
    isLocationEnabledState: State<Boolean>,
    createFosterHomeViewmodel: CreateFosterHomeViewmodel,
    onBackPressed: () -> Unit,
    onUpdatePermissionState: (ManagePermissionState) -> Unit
) {
    var displayDialogToRequestLocationActivation: Boolean by rememberSaveable(!isLocationEnabledState.value) {
        mutableStateOf(
            !isLocationEnabledState.value
        )
    }

    RmManagePermission(
        permission = Permission.COARSE_LOCATION,
        stringsForDialogExplainingPermission = StringsForDialog(
            emoji = "📍",
            title = stringResource(Res.string.create_foster_home_location_permission_title),
            message = stringResource(Res.string.create_foster_home_location_permission_message),
            allowMessage = stringResource(Res.string.create_foster_home_location_permission_grant_permission_button),
            denyMessage = stringResource(Res.string.create_foster_home_location_permission_do_not_grant_permission_button)
        ),
        stringsForDialogToOpenSettings = StringsForDialog(
            emoji = "⚙️",
            title = stringResource(Res.string.create_foster_home_location_go_to_settings_title),
            message = stringResource(Res.string.create_foster_home_location_grant_in_settings_message),
            allowMessage = stringResource(Res.string.create_foster_home_location_open_settings_button),
            denyMessage = stringResource(Res.string.create_foster_home_location_permission_do_not_grant_permission_button),
        ),
        managePermissionState = permissionState,
        updateManagePermissionState = onUpdatePermissionState,
        onPermissionGranted = {}
    )

    if (permissionState == ManagePermissionState.PERMISSION_GRANTED) {

        if (displayDialogToRequestLocationActivation) {

            RmDialog(
                emoji = "⚙️",
                title = stringResource(Res.string.create_foster_home_location_turn_on_location_title),
                message = stringResource(Res.string.create_foster_home_location_turn_on_location_message),
                allowMessage = stringResource(Res.string.create_foster_home_location_turn_on_location_open_settings_button),
                denyMessage = stringResource(Res.string.create_foster_home_location_turn_on_later_location_button),
                onClickAllow = {
                    createFosterHomeViewmodel.requestEnableLocation { isEnabled ->
                        if (isEnabled) {
                            displayDialogToRequestLocationActivation = false
                        }
                    }
                },
                onClickDeny = {
                    displayDialogToRequestLocationActivation = false
                    onBackPressed()
                }
            )
        } else {
            LaunchedEffect(isLocationEnabledState.value) {

                if (isLocationEnabledState.value) {
                    createFosterHomeViewmodel.updateLocation()
                }
            }
        }
    }
}
