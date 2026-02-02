package com.findmeahometeam.reskiume.ui.fosterHomes.checkAllFosterHomes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimalType
import com.findmeahometeam.reskiume.domain.model.fosterHome.City
import com.findmeahometeam.reskiume.domain.model.fosterHome.Country
import com.findmeahometeam.reskiume.domain.model.toStringResource
import com.findmeahometeam.reskiume.ui.core.backgroundColor
import com.findmeahometeam.reskiume.ui.core.components.ManagePermissionState
import com.findmeahometeam.reskiume.ui.core.components.RmButton
import com.findmeahometeam.reskiume.ui.core.components.RmDialog
import com.findmeahometeam.reskiume.ui.core.components.RmDisplaySingleChoiceSegmentedButtonRow
import com.findmeahometeam.reskiume.ui.core.components.RmDropDownMenu
import com.findmeahometeam.reskiume.ui.core.components.RmFosterHomeListItem
import com.findmeahometeam.reskiume.ui.core.components.RmManagePermission
import com.findmeahometeam.reskiume.ui.core.components.RmResultState
import com.findmeahometeam.reskiume.ui.core.components.RmScaffold
import com.findmeahometeam.reskiume.ui.core.components.RmSearchBarWithSuggestions
import com.findmeahometeam.reskiume.ui.core.components.RmSecondaryText
import com.findmeahometeam.reskiume.ui.core.components.RmText
import com.findmeahometeam.reskiume.ui.core.components.StringsForDialog
import com.findmeahometeam.reskiume.ui.core.components.UiState
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.location.COARSE_LOCATION
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import reskiume.composeapp.generated.resources.Res
import reskiume.composeapp.generated.resources.check_all_foster_homes_screen_city_label
import reskiume.composeapp.generated.resources.check_all_foster_homes_screen_country_label
import reskiume.composeapp.generated.resources.check_all_foster_homes_screen_no_foster_home_found
import reskiume.composeapp.generated.resources.check_all_foster_homes_screen_non_human_animal_type_label
import reskiume.composeapp.generated.resources.check_all_foster_homes_screen_search_available_foster_homes
import reskiume.composeapp.generated.resources.check_all_foster_homes_screen_search_foster_homes_button
import reskiume.composeapp.generated.resources.check_all_foster_homes_screen_title
import reskiume.composeapp.generated.resources.location_go_to_settings_title
import reskiume.composeapp.generated.resources.location_grant_in_settings_message
import reskiume.composeapp.generated.resources.location_open_settings_button
import reskiume.composeapp.generated.resources.location_permission_do_not_grant_permission_button
import reskiume.composeapp.generated.resources.location_permission_grant_permission_button
import reskiume.composeapp.generated.resources.location_permission_message
import reskiume.composeapp.generated.resources.location_permission_title
import reskiume.composeapp.generated.resources.location_turn_on_later_location_button
import reskiume.composeapp.generated.resources.location_turn_on_location_message
import reskiume.composeapp.generated.resources.location_turn_on_location_open_settings_button
import reskiume.composeapp.generated.resources.location_turn_on_location_title

@Composable
fun CheckAllFosterHomesScreen(
    onFosterHomeClicked: (fosterHomeId: String) -> Unit
) {
    val checkAllFosterHomesViewmodel: CheckAllFosterHomesViewmodel =
        koinViewModel<CheckAllFosterHomesViewmodel>()


    var isCountryVisible: Boolean by rememberSaveable { mutableStateOf(true) }
    val countryFieldState = rememberTextFieldState()
    var selectedCountry: Country by rememberSaveable { mutableStateOf(Country.UNSELECTED) }
    val countryItems: List<Pair<Country, String>> by checkAllFosterHomesViewmodel.allCountryItems()
        .collectAsState(initial = emptyList())
    var isCityVisible: Boolean by rememberSaveable { mutableStateOf(false) }
    val cityFieldState = rememberTextFieldState()
    var selectedCity: City by rememberSaveable { mutableStateOf(City.UNSELECTED) }
    val cityItems: List<Pair<City, String>> by checkAllFosterHomesViewmodel.allCityItems(
        selectedCountry
    ).collectAsState(initial = emptyList())
    var searchOption: SearchOption by rememberSaveable { mutableStateOf(SearchOption.COUNTRY_CITY) }
    var activistLongitude: Double by rememberSaveable { mutableStateOf(0.0) }
    var activistLatitude: Double by rememberSaveable { mutableStateOf(0.0) }
    var nonHumanAnimalType: NonHumanAnimalType by rememberSaveable {
        mutableStateOf(NonHumanAnimalType.UNSELECTED)
    }
    var managePermissionState: ManagePermissionState by rememberSaveable {
        mutableStateOf(
            ManagePermissionState.IDLE
        )
    }
    var displayDialogToRequestLocationActivation: Boolean by rememberSaveable { mutableStateOf(false) }

    val uiFosterHomeListState: UiState<List<UiFosterHome>> by checkAllFosterHomesViewmodel.allFosterHomesState.collectAsState()
    val isSearchButtonEnabled: Boolean by remember(
        selectedCountry,
        selectedCity,
        managePermissionState,
        nonHumanAnimalType,
        uiFosterHomeListState
    ) {
        derivedStateOf {
            nonHumanAnimalType != NonHumanAnimalType.UNSELECTED &&
                    uiFosterHomeListState !is UiState.Loading &&

                    if (searchOption == SearchOption.COUNTRY_CITY) {
                        selectedCountry != Country.UNSELECTED && selectedCity != City.UNSELECTED
                    } else {
                        managePermissionState == ManagePermissionState.PERMISSION_GRANTED
                    }
        }
    }

    RmScaffold(
        title = stringResource(Res.string.check_all_foster_homes_screen_title)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(padding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RmDisplaySingleChoiceSegmentedButtonRow(
                items = SearchOption.entries.map {
                    Pair(
                        it,
                        stringResource(it.stringResource)
                    )
                }
            ) { searchOp ->
                searchOption = searchOp

                if (searchOption == SearchOption.LOCATION) {

                    managePermissionState = ManagePermissionState.CHECK_PERMISSION
                    displayDialogToRequestLocationActivation = !checkAllFosterHomesViewmodel.getIfLocationEnabled()
                }
            }

            if (searchOption == SearchOption.LOCATION) {

                RmManagePermission(
                    permission = Permission.COARSE_LOCATION,
                    stringsForDialogExplainingPermission = StringsForDialog(
                        emoji = "📍",
                        title = stringResource(Res.string.location_permission_title),
                        message = stringResource(Res.string.location_permission_message),
                        allowMessage = stringResource(Res.string.location_permission_grant_permission_button),
                        denyMessage = stringResource(Res.string.location_permission_do_not_grant_permission_button)
                    ),
                    stringsForDialogToOpenSettings = StringsForDialog(
                        emoji = "⚙️",
                        title = stringResource(Res.string.location_go_to_settings_title),
                        message = stringResource(Res.string.location_grant_in_settings_message),
                        allowMessage = stringResource(Res.string.location_open_settings_button),
                        denyMessage = stringResource(Res.string.location_permission_do_not_grant_permission_button),
                    ),
                    managePermissionState = managePermissionState,
                    updateManagePermissionState = {
                        managePermissionState = it
                    },
                    onPermissionGranted = {
                        if (checkAllFosterHomesViewmodel.getIfLocationEnabled()) {

                            checkAllFosterHomesViewmodel.updateLocation { longitude, latitude ->
                                activistLongitude = longitude
                                activistLatitude = latitude
                            }
                        }
                    }
                )
            }

            if (managePermissionState == ManagePermissionState.PERMISSION_GRANTED && displayDialogToRequestLocationActivation) {

                RmDialog(
                    emoji = "⚙️",
                    title = stringResource(Res.string.location_turn_on_location_title),
                    message = stringResource(Res.string.location_turn_on_location_message),
                    allowMessage = stringResource(Res.string.location_turn_on_location_open_settings_button),
                    denyMessage = stringResource(Res.string.location_turn_on_later_location_button),
                    onClickAllow = {
                        checkAllFosterHomesViewmodel.requestEnableLocation { isEnabled ->
                            if (isEnabled) {

                                checkAllFosterHomesViewmodel.updateLocation { longitude, latitude ->
                                    activistLongitude = longitude
                                    activistLatitude = latitude
                                }
                                displayDialogToRequestLocationActivation = false
                            }
                        }
                    },
                    onClickDeny = {
                        displayDialogToRequestLocationActivation = false
                    }
                )
            }

            AnimatedVisibility(
                visible = searchOption == SearchOption.COUNTRY_CITY
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
                            placeholder = stringResource(Res.string.check_all_foster_homes_screen_country_label),
                            items = countryItems,
                            onSearch = { country: Country? ->
                                country?.let { selectedCountry = it }
                                if (country == null) {
                                    selectedCountry = Country.UNSELECTED
                                    selectedCity = City.UNSELECTED
                                    cityFieldState.setTextAndPlaceCursorAtEnd("")
                                }
                                isCityVisible = country != null
                            }
                        )
                    }

                    if (isCountryVisible && isCityVisible) {
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    if (isCityVisible) {
                        RmSearchBarWithSuggestions(
                            modifier = Modifier.weight(1f),
                            onFocusChanged = {
                                if (it.isFocused) {
                                    isCountryVisible = false
                                }
                            },
                            textFieldState = cityFieldState,
                            placeholder = stringResource(Res.string.check_all_foster_homes_screen_city_label),
                            items = cityItems,
                            onSearch = { city: City? ->
                                city?.let { selectedCity = it }
                                if (city == null) {
                                    selectedCity = City.UNSELECTED
                                    cityFieldState.setTextAndPlaceCursorAtEnd("")
                                }
                                isCountryVisible = true
                            },
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            RmDropDownMenu(
                dropDownLabel = stringResource(Res.string.check_all_foster_homes_screen_non_human_animal_type_label),
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

            RmButton(
                text = stringResource(Res.string.check_all_foster_homes_screen_search_foster_homes_button),
                enabled = isSearchButtonEnabled
            ) {
                if (searchOption == SearchOption.COUNTRY_CITY) {
                    checkAllFosterHomesViewmodel.fetchAllFosterHomesStateByPlace(
                        country = selectedCountry.englishName,
                        city = selectedCity.englishName,
                        nonHumanAnimalType = nonHumanAnimalType
                    )
                } else {
                    checkAllFosterHomesViewmodel.fetchAllFosterHomesStateByLocation(
                        activistLongitude = activistLongitude,
                        activistLatitude = activistLatitude,
                        nonHumanAnimalType = nonHumanAnimalType
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            RmResultState(uiFosterHomeListState) { fosterHomeList: List<UiFosterHome> ->

                AnimatedVisibility(visible = fosterHomeList.isEmpty()) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Spacer(modifier = Modifier.weight(1f))
                        RmText(
                            modifier = Modifier.fillMaxWidth(),
                            text = stringResource(Res.string.check_all_foster_homes_screen_no_foster_home_found),
                            textAlign = TextAlign.Center,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                if (fosterHomeList.isNotEmpty()) {

                    Spacer(modifier = Modifier.height(8.dp))
                    RmSecondaryText(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(Res.string.check_all_foster_homes_screen_search_available_foster_homes),
                        fontSize = 16.sp,
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    LazyColumn {
                        items(
                            items = fosterHomeList,
                            key = { uiFosterHome -> uiFosterHome.hashCode() }
                        ) { uiFosterHome ->
                            RmFosterHomeListItem(
                                modifier = Modifier.animateItem(),
                                title = uiFosterHome.fosterHome.title,
                                imageUrl = uiFosterHome.fosterHome.imageUrl,
                                allAcceptedNonHumanAnimals = uiFosterHome.fosterHome.allAcceptedNonHumanAnimals,
                                allResidentNonHumanAnimalForFosterHome = uiFosterHome.fosterHome.allResidentNonHumanAnimals,
                                distance = uiFosterHome.distance,
                                city = uiFosterHome.fosterHome.city,
                                onClick = {
                                    onFosterHomeClicked(uiFosterHome.fosterHome.id)
                                }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }

                }
            }
        }
    }
}
