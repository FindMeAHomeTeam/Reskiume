package com.findmeahometeam.reskiume.ui.fosterHomes.checkAllFosterHomes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.findmeahometeam.reskiume.data.remote.response.AuthUser
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimalType
import com.findmeahometeam.reskiume.domain.model.fosterHome.City
import com.findmeahometeam.reskiume.domain.model.fosterHome.Country
import com.findmeahometeam.reskiume.domain.model.fosterHome.toStringResource
import com.findmeahometeam.reskiume.domain.model.toEmoji
import com.findmeahometeam.reskiume.domain.model.toStringResource
import com.findmeahometeam.reskiume.ui.core.backgroundColor
import com.findmeahometeam.reskiume.ui.core.components.ManagePermissionState
import com.findmeahometeam.reskiume.ui.core.components.RmButton
import com.findmeahometeam.reskiume.ui.core.components.RmCountryAndCitySelectors
import com.findmeahometeam.reskiume.ui.core.components.RmDisplaySingleChoiceSegmentedButtonRow
import com.findmeahometeam.reskiume.ui.core.components.RmDropDownMenu
import com.findmeahometeam.reskiume.ui.core.components.RmFosterHomeListItem
import com.findmeahometeam.reskiume.ui.core.components.RmManageLocationPermission
import com.findmeahometeam.reskiume.ui.core.components.RmResultState
import com.findmeahometeam.reskiume.ui.core.components.RmScaffold
import com.findmeahometeam.reskiume.ui.core.components.RmSecondaryText
import com.findmeahometeam.reskiume.ui.core.components.RmText
import com.findmeahometeam.reskiume.ui.core.components.UiState
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import reskiume.composeapp.generated.resources.Res
import reskiume.composeapp.generated.resources.check_all_foster_homes_screen_no_foster_home_found
import reskiume.composeapp.generated.resources.check_all_foster_homes_screen_non_human_animal_type_label
import reskiume.composeapp.generated.resources.check_all_foster_homes_screen_search_available_foster_homes
import reskiume.composeapp.generated.resources.check_all_foster_homes_screen_search_by_location
import reskiume.composeapp.generated.resources.check_all_foster_homes_screen_search_by_place
import reskiume.composeapp.generated.resources.check_all_foster_homes_screen_search_foster_homes_button
import reskiume.composeapp.generated.resources.check_all_foster_homes_screen_title
import reskiume.composeapp.generated.resources.manage_location_permission_message_check_all_foster_homes
import reskiume.composeapp.generated.resources.manage_location_permission_turn_on_location_message_check_all_foster_homes

@Composable
fun CheckAllFosterHomesScreen(
    onModifyFosterHome: (fosterHomeId: String) -> Unit,
    onCheckFosterHome: (fosterHomeId: String, ownerId: String) -> Unit
) {
    val checkAllFosterHomesViewmodel: CheckAllFosterHomesViewmodel =
        koinViewModel<CheckAllFosterHomesViewmodel>()

    val placeUtil: PlaceUtil = koinInject<PlaceUtil>()

    var selectedCountry: Country by rememberSaveable { mutableStateOf(Country.UNSELECTED) }
    var selectedCity: City by rememberSaveable { mutableStateOf(City.UNSELECTED) }

    var searchOption: SearchOption by rememberSaveable { mutableStateOf(SearchOption.COUNTRY_CITY) }
    val isLocationEnabledState: State<Boolean> =
        checkAllFosterHomesViewmodel.observeIfLocationEnabled().collectAsState(initial = false)
    var nonHumanAnimalType: NonHumanAnimalType by rememberSaveable {
        mutableStateOf(NonHumanAnimalType.UNSELECTED)
    }
    var permissionState: ManagePermissionState by rememberSaveable {
        mutableStateOf(
            ManagePermissionState.IDLE
        )
    }
    var displayDialogToRequestLocationActivation: Boolean by rememberSaveable { mutableStateOf(false) }

    val authState: AuthUser? by checkAllFosterHomesViewmodel.authState.collectAsState(initial = null)
    val uiFosterHomeListState: UiState<List<UiFosterHome>> by checkAllFosterHomesViewmodel.allFosterHomesState.collectAsState()
    val isSearchButtonEnabled: Boolean by remember(
        selectedCountry,
        selectedCity,
        permissionState,
        nonHumanAnimalType,
        uiFosterHomeListState
    ) {
        derivedStateOf {
            nonHumanAnimalType != NonHumanAnimalType.UNSELECTED &&
                    uiFosterHomeListState !is UiState.Loading &&

                    if (searchOption == SearchOption.COUNTRY_CITY) {
                        selectedCountry != Country.UNSELECTED && selectedCity != City.UNSELECTED
                    } else {
                        permissionState == ManagePermissionState.PERMISSION_GRANTED
                                && isLocationEnabledState.value
                                && !displayDialogToRequestLocationActivation
                    }
        }
    }

    RmScaffold(
        title = stringResource(Res.string.check_all_foster_homes_screen_title)
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(padding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
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

                        permissionState = ManagePermissionState.CHECK_PERMISSION
                        displayDialogToRequestLocationActivation = !isLocationEnabledState.value
                    }
                }

                if (searchOption == SearchOption.LOCATION) {

                    RmManageLocationPermission(
                        explainingLocationPermissionMessage =
                            stringResource(Res.string.manage_location_permission_turn_on_location_message_check_all_foster_homes),
                        explainingLocationActivationMessage =
                            stringResource(Res.string.manage_location_permission_message_check_all_foster_homes),
                        permissionState = permissionState,
                        isLocationEnabledState = isLocationEnabledState,
                        onRequestEnableLocation = {
                            checkAllFosterHomesViewmodel.requestEnableLocation()
                        },
                        onUpdateLocation = {
                            checkAllFosterHomesViewmodel.updateLocation()
                        },
                        onBackPressed = {},
                        onUpdatePermissionState = {
                            permissionState = it
                        }
                    )
                }

                AnimatedVisibility(visible = searchOption == SearchOption.COUNTRY_CITY) {

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
                }

                Spacer(modifier = Modifier.height(8.dp))
                RmDropDownMenu(
                    dropDownLabel = stringResource(Res.string.check_all_foster_homes_screen_non_human_animal_type_label),
                    defaultElementText = nonHumanAnimalType.toEmoji() + " " + stringResource(
                        nonHumanAnimalType.toStringResource()
                    ),
                    items = NonHumanAnimalType.entries.mapNotNull {
                        if (it != NonHumanAnimalType.UNSELECTED) {
                            Pair(it, it.toEmoji() + " " + stringResource(it.toStringResource()))
                        } else {
                            null
                        }
                    },
                    onClick = { nonHumanAnimalType = it },
                )

                Spacer(modifier = Modifier.height(8.dp))
                RmButton(
                    text = stringResource(
                        Res.string.check_all_foster_homes_screen_search_foster_homes_button,
                        if (searchOption == SearchOption.COUNTRY_CITY) {
                            stringResource(Res.string.check_all_foster_homes_screen_search_by_place)
                        } else {
                            stringResource(Res.string.check_all_foster_homes_screen_search_by_location)
                        }
                    ),
                    enabled = isSearchButtonEnabled,
                    displayPleaseWait = uiFosterHomeListState is UiState.Loading
                ) {
                    if (searchOption == SearchOption.COUNTRY_CITY) {
                        checkAllFosterHomesViewmodel.fetchAllFosterHomesStateByPlace(
                            country = selectedCountry.name,
                            city = selectedCity.name,
                            nonHumanAnimalType = nonHumanAnimalType
                        )
                    } else {
                        checkAllFosterHomesViewmodel.fetchAllFosterHomesStateByLocation(
                            nonHumanAnimalType
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
                            text = stringResource(
                                Res.string.check_all_foster_homes_screen_search_available_foster_homes,
                                fosterHomeList.size
                            ),
                            fontSize = 16.sp,
                        )

                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
            if (uiFosterHomeListState is UiState.Success && (uiFosterHomeListState as UiState.Success<List<UiFosterHome>>).data.isNotEmpty()) {
                val fosterHomeList: List<UiFosterHome> =
                    (uiFosterHomeListState as UiState.Success<List<UiFosterHome>>).data
                items(
                    items = fosterHomeList,
                    key = { uiFosterHome -> uiFosterHome.hashCode() }
                ) { uiFosterHome ->

                    RmFosterHomeListItem(
                        modifier = Modifier.animateItem(),
                        title = uiFosterHome.fosterHome.title,
                        imageUrl = uiFosterHome.fosterHome.imageUrl,
                        allAcceptedNonHumanAnimals = uiFosterHome.fosterHome.allAcceptedNonHumanAnimals,
                        allResidentNonHumanAnimals = uiFosterHome.allResidentUiNonHumanAnimals,
                        distance = uiFosterHome.distance,
                        city = stringResource(
                            City
                                .valueOf(uiFosterHome.fosterHome.city)
                                .toStringResource()
                        ).substring(5),
                        onClick = {
                            if (authState?.uid == uiFosterHome.fosterHome.ownerId) {
                                onModifyFosterHome(uiFosterHome.fosterHome.id)
                            } else {
                                onCheckFosterHome(
                                    uiFosterHome.fosterHome.id,
                                    uiFosterHome.fosterHome.ownerId
                                )
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}
