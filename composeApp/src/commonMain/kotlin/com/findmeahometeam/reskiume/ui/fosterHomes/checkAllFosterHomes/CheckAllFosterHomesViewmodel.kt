package com.findmeahometeam.reskiume.ui.fosterHomes.checkAllFosterHomes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.findmeahometeam.reskiume.data.remote.response.AuthUser
import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.LocalCache
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimalType
import com.findmeahometeam.reskiume.domain.model.fosterHome.City
import com.findmeahometeam.reskiume.domain.model.fosterHome.Country
import com.findmeahometeam.reskiume.domain.model.fosterHome.FosterHome
import com.findmeahometeam.reskiume.domain.model.fosterHome.toStringResource
import com.findmeahometeam.reskiume.domain.usecases.authUser.ObserveAuthStateInAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.GetAllFosterHomesByCountryAndCityFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.GetAllFosterHomesByCountryAndCityFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.GetAllFosterHomesByLocationFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.GetAllFosterHomesByLocationFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.GetFosterHomeFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.InsertFosterHomeInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.ModifyFosterHomeInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.image.DownloadImageToLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.GetCompleteImagePathFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.localCache.GetDataByManagingObjectLocalCacheTimestamp
import com.findmeahometeam.reskiume.domain.usecases.localCache.InsertCacheInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.localCache.ModifyCacheInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.util.location.GetLocationFromLocationRepository
import com.findmeahometeam.reskiume.domain.usecases.util.location.ObserveIfLocationEnabledFromLocationRepository
import com.findmeahometeam.reskiume.domain.usecases.util.location.RequestEnableLocationFromLocationRepository
import com.findmeahometeam.reskiume.ui.core.components.UiState
import com.findmeahometeam.reskiume.ui.core.components.toUiState
import com.findmeahometeam.reskiume.ui.util.StringProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
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
    private val getStringProvider: StringProvider,
    private val observeAuthStateInAuthDataSource: ObserveAuthStateInAuthDataSource,
    private val getDataByManagingObjectLocalCacheTimestamp: GetDataByManagingObjectLocalCacheTimestamp,
    private val getAllFosterHomesByCountryAndCityFromRemoteRepository: GetAllFosterHomesByCountryAndCityFromRemoteRepository,
    private val downloadImageToLocalDataSource: DownloadImageToLocalDataSource,
    private val getFosterHomeFromLocalRepository: GetFosterHomeFromLocalRepository,
    private val insertFosterHomeInLocalRepository: InsertFosterHomeInLocalRepository,
    private val insertCacheInLocalRepository: InsertCacheInLocalRepository,
    private val modifyFosterHomeInLocalRepository: ModifyFosterHomeInLocalRepository,
    private val modifyCacheInLocalRepository: ModifyCacheInLocalRepository,
    private val getAllFosterHomesByCountryAndCityFromLocalRepository: GetAllFosterHomesByCountryAndCityFromLocalRepository,
    private val getCompleteImagePathFromLocalDataSource: GetCompleteImagePathFromLocalDataSource,
    private val getAllFosterHomesByLocationFromRemoteRepository: GetAllFosterHomesByLocationFromRemoteRepository,
    private val getAllFosterHomesByLocationFromLocalRepository: GetAllFosterHomesByLocationFromLocalRepository,
    private val observeIfLocationEnabledFromLocationRepository: ObserveIfLocationEnabledFromLocationRepository,
    private val requestEnableLocationFromLocationRepository: RequestEnableLocationFromLocationRepository,
    private val getLocationFromLocationRepository: GetLocationFromLocationRepository,
    private val log: Log
) : ViewModel() {

    private var myUserId: String = ""

    private var activistLongitude: Double = 0.0

    private var activistLatitude: Double = 0.0

    private var locationTimestamp: Long = 0

    private val _allFosterHomesState: MutableStateFlow<UiState<List<UiFosterHome>>> =
        MutableStateFlow(UiState.Idle())

    val allFosterHomesState: StateFlow<UiState<List<UiFosterHome>>> =
        _allFosterHomesState.asStateFlow()

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
    }

    private var _activeQuery: MutableStateFlow<Query> = MutableStateFlow(Query.Idle)

    init {
        viewModelScope.launch {

            _activeQuery.flatMapLatest {
                when (it) {
                    Query.Idle -> flowOf(UiState.Idle())

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
                }
            }.collect {
                _allFosterHomesState.value = it
            }
        }
    }

    fun allCountryItems(): Flow<List<Pair<Country, String>>> = flow {

        val value = Country.entries
            .filter { it != Country.UNSELECTED }
            .map { it to getStringProvider.getStringResource(it.toStringResource()) }
        emit(value)
    }

    fun allCityItems(selectedCountry: Country): Flow<List<Pair<City, String>>> = flow {

        val list: List<Pair<City, String>> = if (selectedCountry == Country.UNSELECTED) {
            emptyList()
        } else {
            City.entries
                .filter { it != City.UNSELECTED && it.country == selectedCountry }
                .map { it to getStringProvider.getStringResource(it.toStringResource()) }
        }
        emit(list)
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

                viewModelScope.launch {

                    val errorMessage =
                        getStringProvider.getStringResource(Res.string.check_all_foster_homes_screen_turn_on_location)
                    log.d(
                        "CheckAllFosterHomesViewmodel",
                        errorMessage
                    )
                    _allFosterHomesState.value = UiState.Error(errorMessage)
                    _activeQuery.value = Query.Idle
                }
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
        observeAuthStateInAuthDataSource()
            .flatMapConcat { authUser: AuthUser? ->

                myUserId = authUser?.uid ?: " "

                getDataByManagingObjectLocalCacheTimestamp(
                    cachedObjectId = country + city,
                    savedBy = myUserId,
                    section = Section.FOSTER_HOMES,
                    onCompletionInsertCache = {
                        getAllFosterHomesByCountryAndCityFromRemoteRepository(
                            country,
                            city,
                            viewModelScope
                        ).downloadImageAndManageFosterHomesInLocalRepository()
                    },
                    onCompletionUpdateCache = {
                        getAllFosterHomesByCountryAndCityFromRemoteRepository(
                            country,
                            city,
                            viewModelScope
                        ).downloadImageAndModifyFosterHomesInLocalRepository()
                    },
                    onVerifyCacheIsRecent = {
                        getAllFosterHomesByCountryAndCityFromLocalRepository(
                            country,
                            city,
                            viewModelScope
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
                                fosterHome.copy(
                                    imageUrl = if (fosterHome.imageUrl.isEmpty()) {
                                        fosterHome.imageUrl
                                    } else {
                                        getCompleteImagePathFromLocalDataSource(fosterHome.imageUrl)
                                    }
                                )
                            )
                        } else {
                            null
                        }
                    }.sortedBy { uiFosterHome -> uiFosterHome.fosterHome.city }
                }
            }.toUiState()

    private fun Flow<List<FosterHome>>.downloadImageAndManageFosterHomesInLocalRepository(): Flow<List<FosterHome>> =
        this.map { fosterHomeList ->
            fosterHomeList.map { fosterHome ->

                if (fosterHome.imageUrl.isNotBlank()) {

                    val localImagePath: String = downloadImageToLocalDataSource(
                        userUid = fosterHome.ownerId,
                        extraId = fosterHome.id,
                        section = Section.FOSTER_HOMES
                    )
                    val fosterHomeWithLocalImage =
                        fosterHome.copy(imageUrl = localImagePath.ifBlank { fosterHome.imageUrl })

                    val localFosterHome: FosterHome? = getFosterHomeFromLocalRepository(
                        fosterHome.id,
                        viewModelScope
                    ).firstOrNull()

                    if (localFosterHome == null) {
                        insertFosterHomeInLocalRepository(fosterHomeWithLocalImage)
                    } else {
                        modifyFosterHomeInLocalRepository(fosterHomeWithLocalImage)
                    }
                    fosterHomeWithLocalImage
                } else {
                    log.d(
                        "CheckAllFosterHomesViewmodel",
                        "Foster home ${fosterHome.id} has no avatar image to save locally."
                    )
                    val localFosterHome: FosterHome? = getFosterHomeFromLocalRepository(
                        fosterHome.id,
                        viewModelScope
                    ).firstOrNull()

                    if (localFosterHome == null) {
                        insertFosterHomeInLocalRepository(fosterHome)
                    } else {
                        modifyFosterHomeInLocalRepository(fosterHome)
                    }
                    fosterHome
                }
            }
        }

    @OptIn(ExperimentalTime::class)
    private fun insertFosterHomeInLocalRepository(fosterHome: FosterHome) {

        viewModelScope.launch {

            insertFosterHomeInLocalRepository(fosterHome) { isSuccess ->
                if (isSuccess) {
                    log.d(
                        "CheckAllFosterHomesViewmodel",
                        "Foster home ${fosterHome.id} added to local database"
                    )
                    viewModelScope.launch {

                        insertCacheInLocalRepository(
                            LocalCache(
                                cachedObjectId = fosterHome.id,
                                savedBy = myUserId,
                                section = Section.FOSTER_HOMES,
                                timestamp = Clock.System.now().epochSeconds
                            )
                        ) { rowId ->

                            if (rowId > 0) {
                                log.d(
                                    "CheckAllFosterHomesViewmodel",
                                    "${fosterHome.id} added to local cache in section ${Section.FOSTER_HOMES}"
                                )
                            } else {
                                log.e(
                                    "CheckAllFosterHomesViewmodel",
                                    "Error adding ${fosterHome.id} to local cache in section ${Section.FOSTER_HOMES}"
                                )
                            }
                        }
                    }
                } else {
                    log.e(
                        "CheckAllFosterHomesViewmodel",
                        "Error adding the foster home ${fosterHome.id} to local database"
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun modifyFosterHomeInLocalRepository(fosterHome: FosterHome) {

        viewModelScope.launch {

            modifyFosterHomeInLocalRepository(fosterHome) { isSuccess ->
                if (isSuccess) {
                    log.d(
                        "CheckAllFosterHomesViewmodel",
                        "Foster home ${fosterHome.id} modified in local database"
                    )
                    viewModelScope.launch {

                        modifyCacheInLocalRepository(
                            LocalCache(
                                cachedObjectId = fosterHome.id,
                                savedBy = myUserId,
                                section = Section.FOSTER_HOMES,
                                timestamp = Clock.System.now().epochSeconds
                            )
                        ) { rowsUpdated ->

                            if (rowsUpdated > 0) {
                                log.d(
                                    "CheckAllFosterHomesViewmodel",
                                    "${fosterHome.id} updated in local cache in section ${Section.FOSTER_HOMES}"
                                )
                            } else {
                                log.e(
                                    "CheckAllFosterHomesViewmodel",
                                    "Error updating ${fosterHome.id} in local cache in section ${Section.FOSTER_HOMES}"
                                )
                            }
                        }
                    }
                } else {
                    log.e(
                        "CheckAllFosterHomesViewmodel",
                        "Error modifying the foster home ${fosterHome.id} in local database"
                    )
                }
            }
        }
    }

    private fun Flow<List<FosterHome>>.downloadImageAndModifyFosterHomesInLocalRepository(): Flow<List<FosterHome>> =
        this.map { fosterHomeList ->
            fosterHomeList.map { fosterHome ->

                if (fosterHome.imageUrl.isNotBlank()) {

                    val localImagePath: String = downloadImageToLocalDataSource(
                        userUid = fosterHome.ownerId,
                        extraId = fosterHome.id,
                        section = Section.FOSTER_HOMES
                    )
                    val fosterHomeWithLocalImage =
                        fosterHome.copy(imageUrl = localImagePath.ifBlank { fosterHome.imageUrl })

                    modifyFosterHomeInLocalRepository(fosterHomeWithLocalImage)

                    fosterHomeWithLocalImage
                } else {
                    log.d(
                        "CheckAllFosterHomesViewmodel",
                        "Foster home ${fosterHome.id} has no avatar image to save locally."
                    )
                    modifyFosterHomeInLocalRepository(fosterHome)

                    fosterHome
                }
            }
        }

    private fun getFetchAllFosterHomesStateByLocationFlow(
        activistLongitude: Double,
        activistLatitude: Double,
        nonHumanAnimalType: NonHumanAnimalType
    ): Flow<UiState<List<UiFosterHome>>> =
        observeAuthStateInAuthDataSource()
            .flatMapConcat { authUser: AuthUser? ->

                myUserId = authUser?.uid ?: " "

                getDataByManagingObjectLocalCacheTimestamp(
                    cachedObjectId = "$activistLongitude$activistLatitude",
                    savedBy = myUserId,
                    section = Section.FOSTER_HOMES,
                    onCompletionInsertCache = {
                        getAllFosterHomesByLocationFromRemoteRepository(
                            activistLongitude = activistLongitude,
                            activistLatitude = activistLatitude,
                            rangeLongitude = getRangeLon(activistLatitude = activistLatitude),
                            rangeLatitude = getRangeLat(),
                            coroutineScope = viewModelScope
                        ).downloadImageAndManageFosterHomesInLocalRepository()
                    },
                    onCompletionUpdateCache = {
                        getAllFosterHomesByLocationFromRemoteRepository(
                            activistLongitude = activistLongitude,
                            activistLatitude = activistLatitude,
                            rangeLongitude = getRangeLon(activistLatitude = activistLatitude),
                            rangeLatitude = getRangeLat(),
                            coroutineScope = viewModelScope
                        ).downloadImageAndModifyFosterHomesInLocalRepository()
                    },
                    onVerifyCacheIsRecent = {
                        getAllFosterHomesByLocationFromLocalRepository(
                            activistLongitude = activistLongitude,
                            activistLatitude = activistLatitude,
                            rangeLongitude = getRangeLon(activistLatitude = activistLatitude),
                            rangeLatitude = getRangeLat(),
                            coroutineScope = viewModelScope
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
                                        getCompleteImagePathFromLocalDataSource(fosterHome.imageUrl)
                                    }
                                ),
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
    val distance: Double? = null
)

enum class SearchOption(val stringResource: StringResource) {
    COUNTRY_CITY(Res.string.check_all_foster_homes_screen_place_search_option),
    LOCATION(Res.string.check_all_foster_homes_screen_location_search_option),
}
