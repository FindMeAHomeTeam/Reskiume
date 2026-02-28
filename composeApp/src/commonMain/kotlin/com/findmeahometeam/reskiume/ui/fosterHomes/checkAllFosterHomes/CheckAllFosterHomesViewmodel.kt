package com.findmeahometeam.reskiume.ui.fosterHomes.checkAllFosterHomes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.findmeahometeam.reskiume.data.remote.response.AuthUser
import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimal
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimalType
import com.findmeahometeam.reskiume.domain.model.User
import com.findmeahometeam.reskiume.domain.model.fosterHome.FosterHome
import com.findmeahometeam.reskiume.domain.usecases.authUser.ObserveAuthStateInAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.GetAllFosterHomesByCountryAndCityFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.GetAllFosterHomesByCountryAndCityFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.GetAllFosterHomesByLocationFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.GetAllFosterHomesByLocationFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.image.GetImagePathForFileNameFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.localCache.GetDataByManagingObjectLocalCacheTimestamp
import com.findmeahometeam.reskiume.domain.usecases.util.location.GetLocationFromLocationRepository
import com.findmeahometeam.reskiume.domain.usecases.util.location.ObserveIfLocationEnabledFromLocationRepository
import com.findmeahometeam.reskiume.domain.usecases.util.location.RequestEnableLocationFromLocationRepository
import com.findmeahometeam.reskiume.ui.core.components.UiState
import com.findmeahometeam.reskiume.ui.core.components.UiState.Error
import com.findmeahometeam.reskiume.ui.core.components.UiState.Idle
import com.findmeahometeam.reskiume.ui.core.components.toUiState
import com.findmeahometeam.reskiume.ui.profile.checkAllMyFosterHomes.CheckAllMyFosterHomesUtil
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
import org.jetbrains.compose.resources.StringResource
import reskiume.composeapp.generated.resources.Res
import reskiume.composeapp.generated.resources.check_all_foster_homes_screen_location_search_option
import reskiume.composeapp.generated.resources.check_all_foster_homes_screen_place_search_option
import reskiume.composeapp.generated.resources.check_all_foster_homes_screen_turn_on_location
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
class CheckAllFosterHomesViewmodel(
    observeAuthStateInAuthDataSource: ObserveAuthStateInAuthDataSource,
    private val getStringProvider: StringProvider,
    private val getDataByManagingObjectLocalCacheTimestamp: GetDataByManagingObjectLocalCacheTimestamp,
    private val getAllFosterHomesByCountryAndCityFromRemoteRepository: GetAllFosterHomesByCountryAndCityFromRemoteRepository,
    private val checkAllMyFosterHomesUtil: CheckAllMyFosterHomesUtil,
    private val getAllFosterHomesByCountryAndCityFromLocalRepository: GetAllFosterHomesByCountryAndCityFromLocalRepository,
    private val checkNonHumanAnimalUtil: CheckNonHumanAnimalUtil,
    private val getImagePathForFileNameFromLocalDataSource: GetImagePathForFileNameFromLocalDataSource,
    private val getAllFosterHomesByLocationFromRemoteRepository: GetAllFosterHomesByLocationFromRemoteRepository,
    private val getAllFosterHomesByLocationFromLocalRepository: GetAllFosterHomesByLocationFromLocalRepository,
    private val observeIfLocationEnabledFromLocationRepository: ObserveIfLocationEnabledFromLocationRepository,
    private val requestEnableLocationFromLocationRepository: RequestEnableLocationFromLocationRepository,
    private val getLocationFromLocationRepository: GetLocationFromLocationRepository,
    private val log: Log
) : ViewModel() {

    private var myUid: String = ""

    private var activistLongitude: Double = 0.0

    private var activistLatitude: Double = 0.0

    private var locationTimestamp: Long = 0

    private val _allFosterHomesState: MutableStateFlow<UiState<List<UiFosterHome>>> =
        MutableStateFlow(Idle())

    val allFosterHomesState: StateFlow<UiState<List<UiFosterHome>>> =
        _allFosterHomesState.asStateFlow()

    val authState: Flow<AuthUser?> = observeAuthStateInAuthDataSource().map { authUser ->
        myUid = authUser?.uid ?: " "
        authUser
    }

    private sealed class Query {

        object Idle : Query()

        class ByPlace(
            val country: String,
            val city: String,
            val nonHumanAnimalType: NonHumanAnimalType
        ) : Query()

        class ByLocation(
            val activistLongitude: Double,
            val activistLatitude: Double,
            val nonHumanAnimalType: NonHumanAnimalType
        ) : Query()

        class Error(val errorMessage: String) : Query()
    }

    private var _activeQuery: MutableStateFlow<Query> = MutableStateFlow(Query.Idle)

    init {
        viewModelScope.launch {

            _activeQuery.flatMapLatest {
                when (it) {
                    Query.Idle -> flowOf(Idle())

                    is Query.ByPlace -> getFetchAllFosterHomesStateByPlaceFlow(
                        it.country,
                        it.city,
                        it.nonHumanAnimalType
                    )

                    is Query.ByLocation -> getFetchAllFosterHomesStateByLocationFlow(
                        it.activistLongitude,
                        it.activistLatitude,
                        it.nonHumanAnimalType
                    )

                    is Query.Error -> flowOf(Error(it.errorMessage))
                }
            }.collect {
                _allFosterHomesState.value = it
            }
        }
    }

    fun fetchAllFosterHomesStateByPlace(
        country: String,
        city: String,
        nonHumanAnimalType: NonHumanAnimalType
    ) {
        _allFosterHomesState.value = UiState.Loading()

        _activeQuery.value = Query.ByPlace(country, city, nonHumanAnimalType)
    }

    fun observeIfLocationEnabled(): Flow<Boolean> = observeIfLocationEnabledFromLocationRepository()

    fun requestEnableLocation(onResul: (isEnabled: Boolean) -> Unit) {

        requestEnableLocationFromLocationRepository(onResul)
    }

    @OptIn(ExperimentalTime::class)
    suspend fun updateLocation() {
        val locationPair: Pair<Double, Double> = getLocationFromLocationRepository()
        activistLongitude = locationPair.first
        activistLatitude = locationPair.second
        locationTimestamp = Clock.System.now().epochSeconds

        log.d(
            "CheckAllFosterHomesViewmodel",
            "Longitude and latitude: $locationPair"
        )
    }

    @OptIn(ExperimentalTime::class)
    fun fetchAllFosterHomesStateByLocation(nonHumanAnimalType: NonHumanAnimalType) {

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
                    getStringProvider.getStringResource(Res.string.check_all_foster_homes_screen_turn_on_location)
                log.d(
                    "CheckAllFosterHomesViewmodel",
                    errorMessage
                )
                _activeQuery.value = Query.Error(errorMessage)
                return@launch
            }
            _allFosterHomesState.value = UiState.Loading()
            _activeQuery.value =
                Query.ByLocation(activistLongitude, activistLatitude, nonHumanAnimalType)
        }
    }

    private fun getFetchAllFosterHomesStateByPlaceFlow(
        country: String,
        city: String,
        nonHumanAnimalType: NonHumanAnimalType
    ): Flow<UiState<List<UiFosterHome>>> =
        flowOf(myUid)
            .flatMapConcat { myUid: String ->

                getDataByManagingObjectLocalCacheTimestamp(
                    cachedObjectId = country + city,
                    savedBy = myUid,
                    section = Section.FOSTER_HOMES,
                    onCompletionInsertCache = {
                        val allFosterHomesFlow: Flow<List<FosterHome>> =
                            getAllFosterHomesByCountryAndCityFromRemoteRepository(
                                country,
                                city
                            )
                        checkAllMyFosterHomesUtil.downloadImageAndManageFosterHomesInLocalRepositoryFromFlow(
                            allFosterHomesFlow,
                            myUid,
                            viewModelScope
                        )
                    },
                    onCompletionUpdateCache = {
                        val allFosterHomesFlow: Flow<List<FosterHome>> =
                            getAllFosterHomesByCountryAndCityFromRemoteRepository(
                                country,
                                city
                            )
                        checkAllMyFosterHomesUtil.downloadImageAndModifyFosterHomesInLocalRepositoryFromFlow(
                            allFosterHomesFlow,
                            myUid,
                            viewModelScope
                        )
                    },
                    onVerifyCacheIsRecent = {
                        getAllFosterHomesByCountryAndCityFromLocalRepository(
                            country,
                            city
                        )
                    }
                ).map {
                    it.mapNotNull { fosterHome ->

                        val nonHumanAnimalTypeSet: Set<NonHumanAnimalType> =
                            fosterHome.allAcceptedNonHumanAnimals.map { acceptedNonHumanAnimalForFosterHome ->
                                acceptedNonHumanAnimalForFosterHome.acceptedNonHumanAnimalType
                            }.toSet()

                        if (nonHumanAnimalTypeSet.contains(nonHumanAnimalType)) {
                            UiFosterHome(
                                fosterHome = fosterHome.copy(
                                    imageUrl = if (fosterHome.imageUrl.isEmpty()) {
                                        fosterHome.imageUrl
                                    } else {
                                        getImagePathForFileNameFromLocalDataSource(fosterHome.imageUrl)
                                    }
                                ),
                                uiAllResidentNonHumanAnimals = fosterHome.allResidentNonHumanAnimals.mapNotNull { residentNonHumanAnimal ->

                                    checkNonHumanAnimalUtil.getNonHumanAnimalFlow(
                                        residentNonHumanAnimal.nonHumanAnimalId,
                                        residentNonHumanAnimal.caregiverId,
                                        viewModelScope
                                    ).firstOrNull()
                                }
                            )
                        } else {
                            null
                        }
                    }.sortedBy { uiFosterHome -> uiFosterHome.fosterHome.city }
                }
            }.toUiState()

    private fun getFetchAllFosterHomesStateByLocationFlow(
        activistLongitude: Double,
        activistLatitude: Double,
        nonHumanAnimalType: NonHumanAnimalType
    ): Flow<UiState<List<UiFosterHome>>> =
        flowOf(myUid)
            .flatMapConcat { myUid: String ->

                getDataByManagingObjectLocalCacheTimestamp(
                    cachedObjectId = "$activistLongitude$activistLatitude",
                    savedBy = myUid,
                    section = Section.FOSTER_HOMES,
                    onCompletionInsertCache = {
                        val allFosterHomesFlow: Flow<List<FosterHome>> =
                            getAllFosterHomesByLocationFromRemoteRepository(
                                activistLongitude = activistLongitude,
                                activistLatitude = activistLatitude,
                                rangeLongitude = getRangeLon(activistLatitude = activistLatitude),
                                rangeLatitude = getRangeLat()
                            )
                        checkAllMyFosterHomesUtil.downloadImageAndManageFosterHomesInLocalRepositoryFromFlow(
                            allFosterHomesFlow,
                            myUid,
                            viewModelScope
                        )
                    },
                    onCompletionUpdateCache = {
                        val allFosterHomesFlow: Flow<List<FosterHome>> =
                            getAllFosterHomesByLocationFromRemoteRepository(
                                activistLongitude = activistLongitude,
                                activistLatitude = activistLatitude,
                                rangeLongitude = getRangeLon(activistLatitude = activistLatitude),
                                rangeLatitude = getRangeLat()
                            )
                        checkAllMyFosterHomesUtil.downloadImageAndModifyFosterHomesInLocalRepositoryFromFlow(
                            allFosterHomesFlow,
                            myUid,
                            viewModelScope
                        )
                    },
                    onVerifyCacheIsRecent = {
                        getAllFosterHomesByLocationFromLocalRepository(
                            activistLongitude = activistLongitude,
                            activistLatitude = activistLatitude,
                            rangeLongitude = getRangeLon(activistLatitude = activistLatitude),
                            rangeLatitude = getRangeLat()
                        )
                    }
                ).map {
                    it.mapNotNull { fosterHome ->

                        val nonHumanAnimalTypeSet: Set<NonHumanAnimalType> =
                            fosterHome.allAcceptedNonHumanAnimals.map { acceptedNonHumanAnimalForFosterHome ->
                                acceptedNonHumanAnimalForFosterHome.acceptedNonHumanAnimalType
                            }.toSet()

                        if (nonHumanAnimalTypeSet.contains(nonHumanAnimalType)) {
                            UiFosterHome(
                                fosterHome = fosterHome.copy(
                                    imageUrl = if (fosterHome.imageUrl.isEmpty()) {
                                        fosterHome.imageUrl
                                    } else {
                                        getImagePathForFileNameFromLocalDataSource(fosterHome.imageUrl)
                                    }
                                ),
                                uiAllResidentNonHumanAnimals = fosterHome.allResidentNonHumanAnimals.mapNotNull { residentNonHumanAnimal ->

                                    checkNonHumanAnimalUtil.getNonHumanAnimalFlow(
                                        residentNonHumanAnimal.nonHumanAnimalId,
                                        residentNonHumanAnimal.caregiverId,
                                        viewModelScope
                                    ).firstOrNull()
                                },
                                distance = calculateDistanceHaversineKm(
                                    activistLatitude,
                                    activistLongitude,
                                    fosterHome.latitude,
                                    fosterHome.longitude
                                )
                            )
                        } else {
                            null
                        }
                    }.sortedBy { uiFosterHome -> uiFosterHome.distance }
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

data class UiFosterHome(
    val fosterHome: FosterHome,
    val uiAllResidentNonHumanAnimals: List<NonHumanAnimal>,
    val distance: Double? = null,
    val owner: User? = null
)

enum class SearchOption(val stringResource: StringResource) {
    COUNTRY_CITY(Res.string.check_all_foster_homes_screen_place_search_option),
    LOCATION(Res.string.check_all_foster_homes_screen_location_search_option),
}
