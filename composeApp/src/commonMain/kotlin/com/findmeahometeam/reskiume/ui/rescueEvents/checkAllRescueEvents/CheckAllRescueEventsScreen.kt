package com.findmeahometeam.reskiume.ui.rescueEvents.checkAllRescueEvents

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
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
import com.findmeahometeam.reskiume.domain.model.fosterHome.City
import com.findmeahometeam.reskiume.domain.model.fosterHome.Country
import com.findmeahometeam.reskiume.domain.model.fosterHome.toStringResource
import com.findmeahometeam.reskiume.ui.core.backgroundColor
import com.findmeahometeam.reskiume.ui.core.components.ManagePermissionState
import com.findmeahometeam.reskiume.ui.core.components.RmButton
import com.findmeahometeam.reskiume.ui.core.components.RmCountryAndCitySelectors
import com.findmeahometeam.reskiume.ui.core.components.RmDisplaySingleChoiceSegmentedButtonRow
import com.findmeahometeam.reskiume.ui.core.components.RmExtendedFloatingActionButton
import com.findmeahometeam.reskiume.ui.core.components.RmManageLocationPermission
import com.findmeahometeam.reskiume.ui.core.components.RmRescueEventListItem
import com.findmeahometeam.reskiume.ui.core.components.RmResultState
import com.findmeahometeam.reskiume.ui.core.components.RmScaffold
import com.findmeahometeam.reskiume.ui.core.components.RmSecondaryText
import com.findmeahometeam.reskiume.ui.core.components.RmText
import com.findmeahometeam.reskiume.ui.core.components.UiState
import com.findmeahometeam.reskiume.ui.fosterHomes.checkAllFosterHomes.PlaceUtil
import com.findmeahometeam.reskiume.ui.fosterHomes.checkAllFosterHomes.SearchOption
import com.findmeahometeam.reskiume.ui.fosterHomes.checkAllFosterHomes.isScrollingUp
import com.findmeahometeam.reskiume.ui.profile.checkAllMyRescueEvents.UiRescueEvent
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import reskiume.composeapp.generated.resources.Res
import reskiume.composeapp.generated.resources.check_all_rescue_events_screen_no_rescue_event_found
import reskiume.composeapp.generated.resources.check_all_rescue_events_screen_register_rescue_event
import reskiume.composeapp.generated.resources.check_all_rescue_events_screen_search_available_rescue_events
import reskiume.composeapp.generated.resources.check_all_rescue_events_screen_search_by_location
import reskiume.composeapp.generated.resources.check_all_rescue_events_screen_search_by_place
import reskiume.composeapp.generated.resources.check_all_rescue_events_screen_search_rescue_events_button
import reskiume.composeapp.generated.resources.check_all_rescue_events_screen_title
import reskiume.composeapp.generated.resources.ic_add
import reskiume.composeapp.generated.resources.manage_location_permission_message_check_all
import reskiume.composeapp.generated.resources.rescue_event
import reskiume.composeapp.generated.resources.manage_location_permission_turn_on_location_message_check_all

@Composable
fun CheckAllRescueEventsScreen(
    onCreateRescueEvent: (creatorId: String) -> Unit,
    onModifyRescueEvent: (rescueEventId: String) -> Unit,
    onCheckRescueEvent: (rescueEventId: String, creatorId: String) -> Unit
) {
    val checkAllRescueEventsViewmodel: CheckAllRescueEventsViewmodel =
        koinViewModel<CheckAllRescueEventsViewmodel>()

    val placeUtil: PlaceUtil = koinInject<PlaceUtil>()

    var selectedCountry: Country by rememberSaveable { mutableStateOf(Country.UNSELECTED) }
    var selectedCity: City by rememberSaveable { mutableStateOf(City.UNSELECTED) }

    var searchOption: SearchOption by rememberSaveable { mutableStateOf(SearchOption.COUNTRY_CITY) }
    val isLocationEnabledState: State<Boolean> =
        checkAllRescueEventsViewmodel.observeIfLocationEnabled().collectAsState(initial = false)
    var permissionState: ManagePermissionState by rememberSaveable {
        mutableStateOf(
            ManagePermissionState.IDLE
        )
    }
    var displayDialogToRequestLocationActivation: Boolean by rememberSaveable { mutableStateOf(false) }

    val authState: AuthUser? by checkAllRescueEventsViewmodel.authState.collectAsState(initial = null)
    val uiRescueEventListState: UiState<List<UiRescueEvent>> by checkAllRescueEventsViewmodel.allRescueEventsState.collectAsState()

    val isSearchButtonEnabled: Boolean by remember(
        selectedCountry,
        selectedCity,
        permissionState,
        uiRescueEventListState
    ) {
        derivedStateOf {
            uiRescueEventListState !is UiState.Loading &&

                    if (searchOption == SearchOption.COUNTRY_CITY) {
                        selectedCountry != Country.UNSELECTED && selectedCity != City.UNSELECTED
                    } else {
                        permissionState == ManagePermissionState.PERMISSION_GRANTED
                                && isLocationEnabledState.value
                                && !displayDialogToRequestLocationActivation
                    }
        }
    }

    val lazyListState = remember { LazyListState() }

    RmScaffold(
        title = stringResource(Res.string.check_all_rescue_events_screen_title),
        floatingActionButton = {
            DisplayExtendedFloatingActionButtonToCreateRescueEventIfLoggedIn(
                authState,
                lazyListState.isScrollingUp(),
                onCreateRescueEvent
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(padding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            state = lazyListState,
            contentPadding = PaddingValues(bottom = if (authState?.uid == null) 0.dp else 72.dp) // Add space to the FAB
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
                            stringResource(
                                Res.string.manage_location_permission_turn_on_location_message_check_all,
                                stringResource(Res.string.rescue_event)
                            ),
                        explainingLocationActivationMessage =
                            stringResource(
                                Res.string.manage_location_permission_message_check_all,
                                stringResource(Res.string.rescue_event)
                            ),
                        permissionState = permissionState,
                        isLocationEnabledState = isLocationEnabledState,
                        onRequestEnableLocation = {
                            checkAllRescueEventsViewmodel.requestEnableLocation()
                        },
                        onUpdateLocation = {
                            checkAllRescueEventsViewmodel.updateLocation()
                        },
                        onBackPressed = {},
                        onUpdatePermissionState = {
                            permissionState = it
                        }
                    )
                }

                AnimatedVisibility(visible = searchOption == SearchOption.COUNTRY_CITY) {

                    RmCountryAndCitySelectors(
                        placeUtil = placeUtil,
                        selectedCountry = selectedCountry,
                        selectedCity = selectedCity,
                        onSelectedCountry = {
                            selectedCountry = it
                        },
                        onSelectedCity = {
                            selectedCity = it
                        }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                RmButton(
                    text = stringResource(
                        Res.string.check_all_rescue_events_screen_search_rescue_events_button,
                        if (searchOption == SearchOption.COUNTRY_CITY) {
                            stringResource(Res.string.check_all_rescue_events_screen_search_by_place)
                        } else {
                            stringResource(Res.string.check_all_rescue_events_screen_search_by_location)
                        }
                    ),
                    enabled = isSearchButtonEnabled,
                    displayPleaseWait = uiRescueEventListState is UiState.Loading
                ) {
                    if (searchOption == SearchOption.COUNTRY_CITY) {
                        checkAllRescueEventsViewmodel.fetchAllRescueEventsStateByPlace(
                            country = selectedCountry.name,
                            city = selectedCity.name
                        )
                    } else {
                        checkAllRescueEventsViewmodel.fetchAllRescueEventsStateByLocation()
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                RmResultState(uiRescueEventListState) { rescueEventList: List<UiRescueEvent> ->

                    AnimatedVisibility(visible = rescueEventList.isEmpty()) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Spacer(modifier = Modifier.weight(1f))
                            RmText(
                                modifier = Modifier.fillMaxWidth(),
                                text = stringResource(Res.string.check_all_rescue_events_screen_no_rescue_event_found),
                                textAlign = TextAlign.Center,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black
                            )
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }

                    if (rescueEventList.isNotEmpty()) {

                        Spacer(modifier = Modifier.height(8.dp))
                        RmSecondaryText(
                            modifier = Modifier.fillMaxWidth(),
                            text = stringResource(
                                Res.string.check_all_rescue_events_screen_search_available_rescue_events,
                                rescueEventList.size
                            ),
                            fontSize = 16.sp,
                        )

                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
            if (uiRescueEventListState is UiState.Success && (uiRescueEventListState as UiState.Success<List<UiRescueEvent>>).data.isNotEmpty()) {
                val rescueEventList: List<UiRescueEvent> =
                    (uiRescueEventListState as UiState.Success<List<UiRescueEvent>>).data
                items(
                    items = rescueEventList,
                    key = { uiRescueEvent -> uiRescueEvent.hashCode() }
                ) { uiRescueEvent ->

                    RmRescueEventListItem(
                        modifier = Modifier.animateItem(),
                        title = uiRescueEvent.rescueEvent.title,
                        imageUrl = uiRescueEvent.rescueEvent.imageUrl,
                        allNeedsToCover = uiRescueEvent.rescueEvent.allNeedsToCover,
                        allNonHumanAnimalsToRescue = uiRescueEvent.allUiNonHumanAnimalsToRescue,
                        distance = uiRescueEvent.distance,
                        city = stringResource(
                            City
                                .valueOf(uiRescueEvent.rescueEvent.city)
                                .toStringResource()
                        ).substring(5),
                        onClick = {
                            if (authState?.uid == uiRescueEvent.rescueEvent.creatorId) {
                                onModifyRescueEvent(uiRescueEvent.rescueEvent.id)
                            } else {
                                onCheckRescueEvent(
                                    uiRescueEvent.rescueEvent.id,
                                    uiRescueEvent.rescueEvent.creatorId
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

@Composable
private fun DisplayExtendedFloatingActionButtonToCreateRescueEventIfLoggedIn(
    authState: AuthUser?,
    expanded: Boolean,
    onCreateRescueEvent: (creatorId: String) -> Unit
) {
    if (authState != null) {

        RmExtendedFloatingActionButton(
            drawableResource = Res.drawable.ic_add,
            text = stringResource(Res.string.check_all_rescue_events_screen_register_rescue_event),
            expanded = expanded,
            onClick = {
                onCreateRescueEvent(authState.uid)
            }
        )
    }
}
