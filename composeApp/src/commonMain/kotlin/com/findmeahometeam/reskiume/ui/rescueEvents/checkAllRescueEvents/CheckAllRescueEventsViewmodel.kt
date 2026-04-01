package com.findmeahometeam.reskiume.ui.rescueEvents.checkAllRescueEvents

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.findmeahometeam.reskiume.data.remote.response.AuthUser
import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.rescueEvent.RescueEvent
import com.findmeahometeam.reskiume.domain.usecases.authUser.ObserveAuthStateInAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.GetImagePathForFileNameFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.localCache.GetDataByManagingObjectLocalCacheTimestamp
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.GetAllRescueEventsByCountryAndCityFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.GetAllRescueEventsByCountryAndCityFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.GetAllRescueEventsByLocationFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.GetAllRescueEventsByLocationFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.user.GetUserFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.util.location.GetLocationFromLocationRepository
import com.findmeahometeam.reskiume.domain.usecases.util.location.ObserveIfLocationEnabledFromLocationRepository
import com.findmeahometeam.reskiume.domain.usecases.util.location.RequestEnableLocationFromLocationRepository
import com.findmeahometeam.reskiume.ui.core.components.UiState
import com.findmeahometeam.reskiume.ui.core.components.UiState.Error
import com.findmeahometeam.reskiume.ui.core.components.UiState.Idle
import com.findmeahometeam.reskiume.ui.core.components.toUiState
import com.findmeahometeam.reskiume.ui.profile.checkAllMyRescueEvents.CheckAllMyRescueEventsUtil
import com.findmeahometeam.reskiume.ui.profile.checkAllMyRescueEvents.UiRescueEvent
import com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal.CheckNonHumanAnimalUtil
import com.findmeahometeam.reskiume.ui.util.StringProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import reskiume.composeapp.generated.resources.Res
import reskiume.composeapp.generated.resources.check_all_rescue_events_screen_turn_on_location
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

private const val WAITING_TIME: Int = 2 * 60 // 2 min

@OptIn(ExperimentalCoroutinesApi::class)
class CheckAllRescueEventsViewmodel(
    observeAuthStateInAuthDataSource: ObserveAuthStateInAuthDataSource,
    private val getUserFromLocalDataSource: GetUserFromLocalDataSource,
    private val getStringProvider: StringProvider,
    private val getDataByManagingObjectLocalCacheTimestamp: GetDataByManagingObjectLocalCacheTimestamp,
    private val getAllRescueEventsByCountryAndCityFromRemoteRepository: GetAllRescueEventsByCountryAndCityFromRemoteRepository,
    private val checkAllMyRescueEventsUtil: CheckAllMyRescueEventsUtil,
    private val getAllRescueEventsByCountryAndCityFromLocalRepository: GetAllRescueEventsByCountryAndCityFromLocalRepository,
    private val checkNonHumanAnimalUtil: CheckNonHumanAnimalUtil,
    private val getImagePathForFileNameFromLocalDataSource: GetImagePathForFileNameFromLocalDataSource,
    private val getAllRescueEventsByLocationFromRemoteRepository: GetAllRescueEventsByLocationFromRemoteRepository,
    private val getAllRescueEventsByLocationFromLocalRepository: GetAllRescueEventsByLocationFromLocalRepository,
    private val observeIfLocationEnabledFromLocationRepository: ObserveIfLocationEnabledFromLocationRepository,
    private val requestEnableLocationFromLocationRepository: RequestEnableLocationFromLocationRepository,
    private val getLocationFromLocationRepository: GetLocationFromLocationRepository,
    private val log: Log
) : ViewModel() {

    private var myUid: String = ""

    private var activistLongitude: Double = 0.0

    private var activistLatitude: Double = 0.0

    private var locationTimestamp: Long = 0

    private val _allRescueEventsState: MutableStateFlow<UiState<List<UiRescueEvent>>> =
        MutableStateFlow(Idle())

    val allRescueEventsState: StateFlow<UiState<List<UiRescueEvent>>> =
        _allRescueEventsState.asStateFlow()

    val authState: Flow<AuthUser?> = observeAuthStateInAuthDataSource().map { authUser ->

        val user = if (authUser != null) getUserFromLocalDataSource(authUser.uid) else null

        if (user == null || !user.isLoggedIn) {
            myUid = " "
            null
        } else {
            myUid = user.uid
            authUser
        }
    }

    private sealed class Query {

        object Idle : Query()

        class ByPlace(
            val country: String,
            val city: String
        ) : Query()

        class ByLocation(
            val activistLongitude: Double,
            val activistLatitude: Double
        ) : Query()

        class Error(val errorMessage: String) : Query()
    }

    private var _activeQuery: MutableStateFlow<Query> = MutableStateFlow(Query.Idle)

    init {
        viewModelScope.launch {

            _activeQuery.flatMapLatest {
                when (it) {
                    Query.Idle -> flowOf(Idle())

                    is Query.ByPlace -> getFetchAllRescueEventsStateByPlaceFlow(
                        it.country,
                        it.city
                    )

                    is Query.ByLocation -> getFetchAllRescueEventsStateByLocationFlow(
                        it.activistLongitude,
                        it.activistLatitude
                    )

                    is Query.Error -> flowOf(Error(it.errorMessage))
                }
            }.collect {
                _allRescueEventsState.value = it
            }
        }
    }

    fun fetchAllRescueEventsStateByPlace(
        country: String,
        city: String
    ) {
        _allRescueEventsState.value = UiState.Loading()

        _activeQuery.value = Query.ByPlace(country, city)
    }

    fun observeIfLocationEnabled(): Flow<Boolean> = observeIfLocationEnabledFromLocationRepository()

    suspend fun requestEnableLocation(): Boolean {

        return suspendCoroutine { continuation ->
            requestEnableLocationFromLocationRepository { isEnabled: Boolean ->
                continuation.resume(isEnabled)
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    suspend fun updateLocation() {
        val locationPair: Pair<Double, Double> = getLocationFromLocationRepository()
        activistLongitude = locationPair.first
        activistLatitude = locationPair.second
        locationTimestamp = Clock.System.now().epochSeconds

        log.d(
            "CheckAllRescueEventsViewmodel",
            "Longitude and latitude: $locationPair"
        )
    }

    @OptIn(ExperimentalTime::class)
    fun fetchAllRescueEventsStateByLocation() {

        viewModelScope.launch {

            val currentTime: Long = Clock.System.now().epochSeconds
            if (currentTime - locationTimestamp >= WAITING_TIME) {

                // Init values
                activistLongitude = 0.0
                activistLatitude = 0.0

                updateLocation()
            }
            if (activistLongitude == 0.0 && activistLatitude == 0.0) {

                val errorMessage =
                    getStringProvider.getStringResource(Res.string.check_all_rescue_events_screen_turn_on_location)
                log.d(
                    "CheckAllRescueEventsViewmodel",
                    errorMessage
                )
                _activeQuery.value = Query.Error(errorMessage)
                return@launch
            }
            _allRescueEventsState.value = UiState.Loading()
            _activeQuery.value =
                Query.ByLocation(activistLongitude, activistLatitude)
        }
    }

    private fun getFetchAllRescueEventsStateByPlaceFlow(
        country: String,
        city: String
    ): Flow<UiState<List<UiRescueEvent>>> =
        flowOf(myUid)
            .flatMapConcat { myUid: String ->

                getDataByManagingObjectLocalCacheTimestamp(
                    cachedObjectId = country + city,
                    savedBy = myUid,
                    section = Section.RESCUE_EVENTS,
                    onCompletionInsertCache = {
                        val allRescueEventsFlow: Flow<List<RescueEvent>> =
                            getAllRescueEventsByCountryAndCityFromRemoteRepository(
                                country,
                                city
                            )
                        checkAllMyRescueEventsUtil.downloadImageAndManageRescueEventsInLocalRepositoryFromFlow(
                            allRescueEventsFlow,
                            myUid,
                            viewModelScope
                        ).flatMapConcat {

                            getAllRescueEventsByCountryAndCityFromLocalRepository(
                                country,
                                city
                            )
                        }
                    },
                    onCompletionUpdateCache = {
                        val allRescueEventsFlow: Flow<List<RescueEvent>> =
                            getAllRescueEventsByCountryAndCityFromRemoteRepository(
                                country,
                                city
                            )
                        checkAllMyRescueEventsUtil.downloadImageAndManageRescueEventsInLocalRepositoryFromFlow(
                            allRescueEventsFlow,
                            myUid,
                            viewModelScope
                        ).flatMapConcat {

                            getAllRescueEventsByCountryAndCityFromLocalRepository(
                                country,
                                city
                            )
                        }
                    },
                    onVerifyCacheIsRecent = {
                        getAllRescueEventsByCountryAndCityFromLocalRepository(
                            country,
                            city
                        )
                    }
                ).map {
                    it.map { rescueEvent ->
                        UiRescueEvent(
                            rescueEvent = rescueEvent.copy(
                                imageUrl = if (rescueEvent.imageUrl.isEmpty()) {
                                    rescueEvent.imageUrl
                                } else {
                                    getImagePathForFileNameFromLocalDataSource(rescueEvent.imageUrl)
                                }
                            ),
                            allUiNonHumanAnimalsToRescue = rescueEvent.allNonHumanAnimalsToRescue.mapNotNull { nonHumanAnimalToRescue ->

                                checkNonHumanAnimalUtil.getNonHumanAnimalFlow(
                                    nonHumanAnimalToRescue.nonHumanAnimalId,
                                    nonHumanAnimalToRescue.caregiverId,
                                    viewModelScope
                                ).firstOrNull()
                            }
                        )
                    }.sortedBy { uiRescueEvent -> uiRescueEvent.rescueEvent.city }
                }
            }.toUiState()

    private fun getFetchAllRescueEventsStateByLocationFlow(
        activistLongitude: Double,
        activistLatitude: Double
    ): Flow<UiState<List<UiRescueEvent>>> =
        flowOf(myUid)
            .flatMapConcat { myUid: String ->

                getDataByManagingObjectLocalCacheTimestamp(
                    cachedObjectId = "$activistLongitude$activistLatitude",
                    savedBy = myUid,
                    section = Section.RESCUE_EVENTS,
                    onCompletionInsertCache = {
                        val allRescueEventsFlow: Flow<List<RescueEvent>> =
                            getAllRescueEventsByLocationFromRemoteRepository(
                                activistLongitude = activistLongitude,
                                activistLatitude = activistLatitude,
                                rangeLongitude = getRangeLon(activistLatitude = activistLatitude),
                                rangeLatitude = getRangeLat()
                            )
                        checkAllMyRescueEventsUtil.downloadImageAndManageRescueEventsInLocalRepositoryFromFlow(
                            allRescueEventsFlow,
                            myUid,
                            viewModelScope
                        ).flatMapConcat {

                            getAllRescueEventsByLocationFromLocalRepository(
                                activistLongitude = activistLongitude,
                                activistLatitude = activistLatitude,
                                rangeLongitude = getRangeLon(activistLatitude = activistLatitude),
                                rangeLatitude = getRangeLat()
                            )
                        }
                    },
                    onCompletionUpdateCache = {
                        val allRescueEventsFlow: Flow<List<RescueEvent>> =
                            getAllRescueEventsByLocationFromRemoteRepository(
                                activistLongitude = activistLongitude,
                                activistLatitude = activistLatitude,
                                rangeLongitude = getRangeLon(activistLatitude = activistLatitude),
                                rangeLatitude = getRangeLat()
                            )
                        checkAllMyRescueEventsUtil.downloadImageAndManageRescueEventsInLocalRepositoryFromFlow(
                            allRescueEventsFlow,
                            myUid,
                            viewModelScope
                        ).flatMapConcat {

                            getAllRescueEventsByLocationFromLocalRepository(
                                activistLongitude = activistLongitude,
                                activistLatitude = activistLatitude,
                                rangeLongitude = getRangeLon(activistLatitude = activistLatitude),
                                rangeLatitude = getRangeLat()
                            )
                        }
                    },
                    onVerifyCacheIsRecent = {
                        getAllRescueEventsByLocationFromLocalRepository(
                            activistLongitude = activistLongitude,
                            activistLatitude = activistLatitude,
                            rangeLongitude = getRangeLon(activistLatitude = activistLatitude),
                            rangeLatitude = getRangeLat()
                        )
                    }
                ).map {
                    it.map { rescueEvent ->

                        UiRescueEvent(
                            rescueEvent = rescueEvent.copy(
                                imageUrl = if (rescueEvent.imageUrl.isEmpty()) {
                                    rescueEvent.imageUrl
                                } else {
                                    getImagePathForFileNameFromLocalDataSource(rescueEvent.imageUrl)
                                }
                            ),
                            allUiNonHumanAnimalsToRescue = rescueEvent.allNonHumanAnimalsToRescue.mapNotNull { nonHumanAnimalToRescue ->

                                checkNonHumanAnimalUtil.getNonHumanAnimalFlow(
                                    nonHumanAnimalToRescue.nonHumanAnimalId,
                                    nonHumanAnimalToRescue.caregiverId,
                                    viewModelScope
                                ).firstOrNull()
                            },
                            distance = calculateDistanceHaversineKm(
                                activistLatitude,
                                activistLongitude,
                                rescueEvent.latitude,
                                rescueEvent.longitude
                            )
                        )
                    }.sortedBy { uiRescueEvent -> uiRescueEvent.distance }
                }
            }
            .toUiState()


    private fun getRangeLon(maxDistanceInKm: Double = 150.0, activistLatitude: Double): Double =
        1.0 * maxDistanceInKm / (111.320 * cos((activistLatitude * PI) / (180)))

    private fun getRangeLat(maxDistanceInKm: Double = 150.0): Double =
        1.0 * maxDistanceInKm / 110.574

    // Calculate the distance in km between two points using the Haversine formula
    // and rounds the result to one decimal place
    private fun calculateDistanceHaversineKm(
        lat1: Double,
        lng1: Double,
        lat2: Double,
        lng2: Double
    ): Double {
        if (lat1 == lat2 && lng1 == lng2) return 0.0

        val radEarth = 6378.1

        val phi1 = lat1 * (PI / 180.0)
        val phi2 = lat2 * (PI / 180.0)

        val delta1 = (lat2 - lat1) * (PI / 180.0)
        val delta2 = (lng2 - lng1) * (PI / 180.0)

        val cal1 = sin(delta1 / 2).pow(2) + cos(phi1) * cos(phi2) * sin(delta2 / 2).pow(2)

        val cal2 = 2 * atan2(sqrt(cal1), sqrt(1 - cal1))

        val distance = radEarth * cal2

        return round(distance * 10.0) / 10.0
    }
}
