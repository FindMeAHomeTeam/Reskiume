package com.findmeahometeam.reskiume.ui.unitTests.fosterHomes

import app.cash.turbine.test
import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.activistLatitude
import com.findmeahometeam.reskiume.activistLongitude
import com.findmeahometeam.reskiume.authUser
import com.findmeahometeam.reskiume.data.database.entity.LocalCacheEntity
import com.findmeahometeam.reskiume.data.database.entity.fosterHome.FosterHomeWithAllNonHumanAnimalData
import com.findmeahometeam.reskiume.data.remote.response.AuthUser
import com.findmeahometeam.reskiume.data.remote.response.fosterHome.RemoteFosterHome
import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.fosterHome.City
import com.findmeahometeam.reskiume.domain.model.fosterHome.Country
import com.findmeahometeam.reskiume.domain.model.fosterHome.toStringResource
import com.findmeahometeam.reskiume.domain.repository.local.LocalCacheRepository
import com.findmeahometeam.reskiume.domain.repository.local.LocalFosterHomeRepository
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteFosterHome.FireStoreRemoteFosterHomeRepository
import com.findmeahometeam.reskiume.domain.repository.remote.storage.StorageRepository
import com.findmeahometeam.reskiume.domain.repository.util.location.LocationRepository
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
import com.findmeahometeam.reskiume.fosterHome
import com.findmeahometeam.reskiume.fosterHomeWithAllNonHumanAnimalData
import com.findmeahometeam.reskiume.localCache
import com.findmeahometeam.reskiume.nonHumanAnimal
import com.findmeahometeam.reskiume.ui.core.components.UiState
import com.findmeahometeam.reskiume.ui.fosterHomes.checkAllFosterHomes.CheckAllFosterHomesViewmodel
import com.findmeahometeam.reskiume.ui.fosterHomes.checkAllFosterHomes.UiFosterHome
import com.findmeahometeam.reskiume.ui.profile.checkAllMyFosterHomes.CheckAllMyFosterHomesUtilImpl
import com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal.CheckNonHumanAnimalUtil
import com.findmeahometeam.reskiume.ui.util.ManageImagePath
import com.findmeahometeam.reskiume.ui.util.StringProvider
import com.findmeahometeam.reskiume.user
import com.plusmobileapps.konnectivity.Konnectivity
import com.plusmobileapps.konnectivity.NetworkConnection
import dev.mokkery.answering.calls
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.matcher.capture.Capture
import dev.mokkery.matcher.capture.capture
import dev.mokkery.matcher.capture.get
import dev.mokkery.mock
import dev.mokkery.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CheckAllFosterHomesViewmodelTest : CoroutineTestDispatcher() {

    private val onInsertLocalCacheEntity = Capture.slot<(rowId: Long) -> Unit>()

    private val onModifyLocalCacheEntity = Capture.slot<(rowsUpdated: Int) -> Unit>()

    private val onSaveImageToLocalForNonHumanAnimal = Capture.slot<(String) -> Unit>()

    private val onSaveImageToLocalForFosterHome = Capture.slot<(String) -> Unit>()

    private val onInsertFosterHome = Capture.slot<suspend (rowId: Long) -> Unit>()

    private val onInsertFosterHomeWithoutImage = Capture.slot<suspend (rowId: Long) -> Unit>()

    private val onInsertAcceptedNonHumanAnimalForFosterHome = Capture.slot<(rowId: Long) -> Unit>()

    private val onInsertAcceptedSecondNonHumanAnimalForFosterHome =
        Capture.slot<(rowId: Long) -> Unit>()

    private val onInsertResidentNonHumanAnimalIdForFosterHome =
        Capture.slot<(rowId: Long) -> Unit>()

    private val onModifyFosterHome = Capture.slot<suspend (rowsUpdated: Int) -> Unit>()

    private val onModifyFosterHomeWithoutImage = Capture.slot<suspend (rowsUpdated: Int) -> Unit>()

    private val onModifyAcceptedNonHumanAnimalForFosterHome =
        Capture.slot<(rowsUpdated: Int) -> Unit>()

    private val onModifyAcceptedSecondNonHumanAnimalForFosterHome =
        Capture.slot<(rowsUpdated: Int) -> Unit>()

    private val onModifyResidentNonHumanAnimalIdForFosterHome =
        Capture.slot<(rowsUpdated: Int) -> Unit>()

    private val onRequestEnableLocation = Capture.slot<(isEnabled: Boolean) -> Unit>()

    private val getStringProvider: StringProvider = mock {
        everySuspend {
            getStringResource(any())
        } returns "I found a non-human animal in the street. What can I do?"
    }

    private val log: Log = mock {
        every { d(any(), any()) } calls { println(it) }
        every { e(any(), any()) } calls { println(it) }
    }

    private val konnectivity: Konnectivity = mock {
        every { isConnected } returns true
        every { currentNetworkConnection } returns NetworkConnection.WIFI
        every { isConnectedState } returns MutableStateFlow(true)
        every { currentNetworkConnectionState } returns MutableStateFlow(NetworkConnection.WIFI)
    }

    private fun getCheckAllFosterHomesViewmodel(
        authStateReturn: AuthUser? = authUser,
        getLocalCacheEntityReturnForCountryCity: LocalCacheEntity? =
            localCache.copy(
                cachedObjectId = fosterHome.country + fosterHome.city,
                section = Section.FOSTER_HOMES
            ).toEntity(),
        getLocalCacheEntityReturnForLocation: LocalCacheEntity? =
            localCache.copy(
                cachedObjectId = "${activistLongitude}${activistLatitude}",
                section = Section.FOSTER_HOMES
            ).toEntity(),
        localCacheIdInsertedInLocalDatasourceArg: Long = 1L,
        localCacheUpdatedInLocalDatasourceArg: Int = 1,
        remoteFosterHomesByCountryAndCity: Flow<List<RemoteFosterHome>> = flowOf(listOf(fosterHome.toData())),
        remoteFosterHomesByLocation: Flow<List<RemoteFosterHome>> = flowOf(listOf(fosterHome.toData())),
        absolutePathArgForNonHumanAnimal: String = nonHumanAnimal.imageUrl,
        absolutePathArgForFosterHome: String = fosterHome.imageUrl,
        insertedFosterHomeInLocalRowIdArg: Long = 1L,
        insertedFosterHomeWithoutImageInLocalRowIdArg: Long = 1L,
        insertedAcceptedNonHumanAnimalForFosterHomeInLocalRowIdArg: Long = 1L,
        insertedAcceptedSecondNonHumanAnimalForFosterHomeInLocalRowIdArg: Long = 1L,
        insertedResidentNonHumanAnimalIdForFosterHomeInLocalRowIdArg: Long = 1L,
        modifiedFosterHomeInLocalRowsUpdatedArg: Int = 1,
        modifiedFosterHomeWithoutImageInLocalRowsUpdatedArg: Int = 1,
        modifiedAcceptedNonHumanAnimalForFosterHomeInLocalRowsUpdatedArg: Int = 1,
        modifiedAcceptedSecondNonHumanAnimalForFosterHomeInLocalRowsUpdatedArg: Int = 1,
        modifiedResidentNonHumanAnimalIdForFosterHomeInLocalRowsUpdatedArg: Int = 1,
        fosterHomeWithAllNonHumanAnimalLocalDataReturn: FosterHomeWithAllNonHumanAnimalData? = fosterHomeWithAllNonHumanAnimalData,
        fosterHomeWithAllNonHumanAnimalLocalDataByCountryCityReturn: Flow<List<FosterHomeWithAllNonHumanAnimalData>> = flowOf(
            listOf(fosterHomeWithAllNonHumanAnimalData)
        ),
        fosterHomeWithAllNonHumanAnimalLocalDataByLocationReturn: Flow<List<FosterHomeWithAllNonHumanAnimalData>> = flowOf(
            listOf(fosterHomeWithAllNonHumanAnimalData)
        ),
        locationReturn: Pair<Double, Double> = Pair(activistLongitude, activistLatitude)
    ): CheckAllFosterHomesViewmodel {

        val authRepository: AuthRepository = mock {
            everySuspend { authState } returns (flowOf(authStateReturn))
        }

        val localCacheRepository: LocalCacheRepository = mock {

            everySuspend {
                getLocalCacheEntity(
                    fosterHome.country + fosterHome.city,
                    Section.FOSTER_HOMES
                )
            } returns getLocalCacheEntityReturnForCountryCity

            everySuspend {
                getLocalCacheEntity(
                    fosterHome.id,
                    Section.FOSTER_HOMES
                )
            } returns getLocalCacheEntityReturnForCountryCity

            everySuspend {
                getLocalCacheEntity(
                    "${activistLongitude}${activistLatitude}",
                    Section.FOSTER_HOMES
                )
            } returns getLocalCacheEntityReturnForLocation

            everySuspend {
                insertLocalCacheEntity(
                    any(),
                    capture(onInsertLocalCacheEntity)
                )
            } calls {
                onInsertLocalCacheEntity.get().invoke(localCacheIdInsertedInLocalDatasourceArg)
            }

            everySuspend {
                modifyLocalCacheEntity(
                    any(),
                    capture(onModifyLocalCacheEntity)
                )
            } calls { onModifyLocalCacheEntity.get().invoke(localCacheUpdatedInLocalDatasourceArg) }
        }

        val fireStoreRemoteFosterHomeRepository: FireStoreRemoteFosterHomeRepository = mock {

            everySuspend {
                getAllRemoteFosterHomesByCountryAndCity(
                    fosterHome.country,
                    fosterHome.city
                )
            } returns remoteFosterHomesByCountryAndCity


            everySuspend {
                getAllRemoteFosterHomesByLocation(
                    activistLongitude,
                    activistLatitude,
                    any(),
                    any()
                )
            } returns remoteFosterHomesByLocation
        }

        val checkNonHumanAnimalUtil: CheckNonHumanAnimalUtil = mock {

            every {
                getNonHumanAnimalFlow(
                    any(),
                    nonHumanAnimal.id,
                    nonHumanAnimal.caregiverId
                )
            } returns flowOf(UiState.Success(nonHumanAnimal))
        }

        val storageRepository: StorageRepository = mock {

            every {
                downloadImage(
                    user.uid,
                    nonHumanAnimal.id,
                    Section.NON_HUMAN_ANIMALS,
                    capture(onSaveImageToLocalForNonHumanAnimal)
                )
            } calls {
                onSaveImageToLocalForNonHumanAnimal.get().invoke(absolutePathArgForNonHumanAnimal)
            }

            every {
                downloadImage(
                    user.uid,
                    fosterHome.id,
                    Section.FOSTER_HOMES,
                    capture(onSaveImageToLocalForFosterHome)
                )
            } calls { onSaveImageToLocalForFosterHome.get().invoke(absolutePathArgForFosterHome) }
        }

        val localFosterHomeRepository: LocalFosterHomeRepository = mock {

            everySuspend {
                insertFosterHome(
                    fosterHome.copy(savedBy = authUser.uid).toEntity(),
                    capture(onInsertFosterHome)
                )
            } calls {
                onInsertFosterHome.get().invoke(insertedFosterHomeInLocalRowIdArg)
            }

            everySuspend {
                insertFosterHome(
                    fosterHome.copy(savedBy = authUser.uid, imageUrl = "").toEntity(),
                    capture(onInsertFosterHomeWithoutImage)
                )
            } calls {
                onInsertFosterHomeWithoutImage.get()
                    .invoke(insertedFosterHomeWithoutImageInLocalRowIdArg)
            }

            everySuspend {
                insertAcceptedNonHumanAnimalForFosterHome(
                    fosterHome.allAcceptedNonHumanAnimals[0].toEntity(),
                    capture(onInsertAcceptedNonHumanAnimalForFosterHome)
                )
            } calls {
                onInsertAcceptedNonHumanAnimalForFosterHome.get()
                    .invoke(insertedAcceptedNonHumanAnimalForFosterHomeInLocalRowIdArg)
            }

            everySuspend {
                insertAcceptedNonHumanAnimalForFosterHome(
                    fosterHome.allAcceptedNonHumanAnimals[1].toEntity(),
                    capture(onInsertAcceptedSecondNonHumanAnimalForFosterHome)
                )
            } calls {
                onInsertAcceptedSecondNonHumanAnimalForFosterHome.get()
                    .invoke(insertedAcceptedSecondNonHumanAnimalForFosterHomeInLocalRowIdArg)
            }

            everySuspend {
                insertResidentNonHumanAnimalIdForFosterHome(
                    fosterHome.allResidentNonHumanAnimals[0].toEntityForId(),
                    capture(onInsertResidentNonHumanAnimalIdForFosterHome)
                )
            } calls {
                onInsertResidentNonHumanAnimalIdForFosterHome.get()
                    .invoke(insertedResidentNonHumanAnimalIdForFosterHomeInLocalRowIdArg)
            }

            everySuspend {
                modifyFosterHome(
                    fosterHome.copy(savedBy = authUser.uid).toEntity(),
                    capture(onModifyFosterHome)
                )
            } calls {
                onModifyFosterHome.get().invoke(modifiedFosterHomeInLocalRowsUpdatedArg)
            }

            everySuspend {
                modifyFosterHome(
                    fosterHome.copy(savedBy = authUser.uid, imageUrl = "").toEntity(),
                    capture(onModifyFosterHomeWithoutImage)
                )
            } calls {
                onModifyFosterHomeWithoutImage.get()
                    .invoke(modifiedFosterHomeWithoutImageInLocalRowsUpdatedArg)
            }

            everySuspend {
                modifyAcceptedNonHumanAnimalForFosterHome(
                    fosterHome.allAcceptedNonHumanAnimals[0].toEntity(),
                    capture(onModifyAcceptedNonHumanAnimalForFosterHome)
                )
            } calls {
                onModifyAcceptedNonHumanAnimalForFosterHome.get()
                    .invoke(modifiedAcceptedNonHumanAnimalForFosterHomeInLocalRowsUpdatedArg)
            }

            everySuspend {
                modifyAcceptedNonHumanAnimalForFosterHome(
                    fosterHome.allAcceptedNonHumanAnimals[1].toEntity(),
                    capture(onModifyAcceptedSecondNonHumanAnimalForFosterHome)
                )
            } calls {
                onModifyAcceptedSecondNonHumanAnimalForFosterHome.get()
                    .invoke(modifiedAcceptedSecondNonHumanAnimalForFosterHomeInLocalRowsUpdatedArg)
            }

            everySuspend {
                modifyResidentNonHumanAnimalIdForFosterHome(
                    fosterHome.allResidentNonHumanAnimals[0].toEntityForId(),
                    capture(onModifyResidentNonHumanAnimalIdForFosterHome)
                )
            } calls {
                onModifyResidentNonHumanAnimalIdForFosterHome.get()
                    .invoke(modifiedResidentNonHumanAnimalIdForFosterHomeInLocalRowsUpdatedArg)
            }

            everySuspend {
                getFosterHome(fosterHome.id)
            } returns fosterHomeWithAllNonHumanAnimalLocalDataReturn

            every {
                getAllFosterHomesByCountryAndCity(fosterHome.country, fosterHome.city)
            } returns fosterHomeWithAllNonHumanAnimalLocalDataByCountryCityReturn

            everySuspend {
                getAllFosterHomesByLocation(
                    activistLongitude,
                    activistLatitude,
                    any(),
                    any()
                )
            } returns fosterHomeWithAllNonHumanAnimalLocalDataByLocationReturn
        }

        val manageImagePath: ManageImagePath = mock {

            every { getCompleteImagePath(nonHumanAnimal.imageUrl) } returns nonHumanAnimal.imageUrl

            every { getCompleteImagePath(fosterHome.imageUrl) } returns fosterHome.imageUrl
        }

        val locationRepository: LocationRepository = mock {

            everySuspend {
                observeIfLocationEnabledFlow()
            } returns flowOf(true)

            every {
                requestEnableLocation(
                    capture(onRequestEnableLocation)
                )
            } calls {
                onRequestEnableLocation.get().invoke(true)
            }

            everySuspend {
                getLocation()
            } returns locationReturn
        }

        val observeAuthStateInAuthDataSource =
            ObserveAuthStateInAuthDataSource(authRepository)

        val getDataByManagingObjectLocalCacheTimestamp =
            GetDataByManagingObjectLocalCacheTimestamp(localCacheRepository, log, konnectivity)

        val getAllFosterHomesByCountryAndCityFromRemoteRepository =
            GetAllFosterHomesByCountryAndCityFromRemoteRepository(
                fireStoreRemoteFosterHomeRepository,
                checkNonHumanAnimalUtil
            )

        val downloadImageToLocalDataSource =
            DownloadImageToLocalDataSource(storageRepository)

        val getFosterHomeFromLocalRepository =
            GetFosterHomeFromLocalRepository(localFosterHomeRepository, checkNonHumanAnimalUtil)

        val insertFosterHomeInLocalRepository =
            InsertFosterHomeInLocalRepository(localFosterHomeRepository, authRepository)

        val insertCacheInLocalRepository =
            InsertCacheInLocalRepository(localCacheRepository)

        val modifyFosterHomeInLocalRepository =
            ModifyFosterHomeInLocalRepository(localFosterHomeRepository, authRepository)

        val modifyCacheInLocalRepository = ModifyCacheInLocalRepository(localCacheRepository)

        val checkAllMyFosterHomesUtil = CheckAllMyFosterHomesUtilImpl(
            downloadImageToLocalDataSource,
            getFosterHomeFromLocalRepository,
            insertFosterHomeInLocalRepository,
            insertCacheInLocalRepository,
            modifyFosterHomeInLocalRepository,
            modifyCacheInLocalRepository,
            log
        )

        val getAllFosterHomesByCountryAndCityFromLocalRepository =
            GetAllFosterHomesByCountryAndCityFromLocalRepository(
                localFosterHomeRepository,
                checkNonHumanAnimalUtil
            )

        val getCompleteImagePathFromLocalDataSource =
            GetCompleteImagePathFromLocalDataSource(manageImagePath)

        val getAllFosterHomesByLocationFromRemoteRepository =
            GetAllFosterHomesByLocationFromRemoteRepository(
                fireStoreRemoteFosterHomeRepository,
                checkNonHumanAnimalUtil
            )

        val getAllFosterHomesByLocationFromLocalRepository =
            GetAllFosterHomesByLocationFromLocalRepository(
                localFosterHomeRepository,
                checkNonHumanAnimalUtil
            )

        val observeIfLocationEnabledFromLocationRepository =
            ObserveIfLocationEnabledFromLocationRepository(locationRepository)

        val requestEnableLocationFromLocationRepository =
            RequestEnableLocationFromLocationRepository(locationRepository)

        val getLocationFromLocationRepository =
            GetLocationFromLocationRepository(locationRepository)

        return CheckAllFosterHomesViewmodel(
            getStringProvider,
            observeAuthStateInAuthDataSource,
            getDataByManagingObjectLocalCacheTimestamp,
            getAllFosterHomesByCountryAndCityFromRemoteRepository,
            checkAllMyFosterHomesUtil,
            getAllFosterHomesByCountryAndCityFromLocalRepository,
            getCompleteImagePathFromLocalDataSource,
            getAllFosterHomesByLocationFromRemoteRepository,
            getAllFosterHomesByLocationFromLocalRepository,
            observeIfLocationEnabledFromLocationRepository,
            requestEnableLocationFromLocationRepository,
            getLocationFromLocationRepository,
            log
        )
    }

    @Test
    fun `given a user requesting all countries_when the user clicks on the country selector_then the available countries are displayed`() =
        runTest {
            getCheckAllFosterHomesViewmodel().allCountryItems().test {
                val expectedResult: List<Pair<Country, String>> = Country.entries
                    .filter { it != Country.UNSELECTED }
                    .map { it to getStringProvider.getStringResource(it.toStringResource()) }
                assertEquals(expectedResult, awaitItem())
                awaitComplete()
            }
        }

    @Test
    fun `given a user requesting all cities by country_when the user clicks on the city selector_then the available cities are displayed`() =
        runTest {
            getCheckAllFosterHomesViewmodel().allCityItems(Country.SPAIN).test {
                val expectedResult: List<Pair<City, String>> = City.entries
                    .filter { it != City.UNSELECTED && it.country == Country.SPAIN }
                    .map { it to getStringProvider.getStringResource(it.toStringResource()) }
                assertEquals(expectedResult, awaitItem())
                awaitComplete()
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given a user requesting all foster homes from cordoba_when the user clicks on the search button_then foster homes are saved in the local repository and displayed`() =
        runTest {
            val checkAllFosterHomesViewmodel = getCheckAllFosterHomesViewmodel(
                getLocalCacheEntityReturnForCountryCity = null,
                fosterHomeWithAllNonHumanAnimalLocalDataReturn = null
            )
            checkAllFosterHomesViewmodel.fetchAllFosterHomesStateByPlace(
                fosterHome.country,
                fosterHome.city,
                nonHumanAnimal.nonHumanAnimalType
            )

            runCurrent()

            checkAllFosterHomesViewmodel.allFosterHomesState.test {
                assertEquals(UiState.Success(listOf(UiFosterHome(fosterHome))), awaitItem())
                ensureAllEventsConsumed()
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given a user requesting all FHs from cordoba_when the user clicks on the search button but some FHs do not have avatar_then FHs are saved without image in the local repository and displayed`() =
        runTest {
            val checkAllFosterHomesViewmodel = getCheckAllFosterHomesViewmodel(
                remoteFosterHomesByCountryAndCity = flowOf(
                    listOf(
                        fosterHome.copy(imageUrl = "").toData()
                    )
                ),
                getLocalCacheEntityReturnForCountryCity = null,
                fosterHomeWithAllNonHumanAnimalLocalDataReturn = null
            )
            checkAllFosterHomesViewmodel.fetchAllFosterHomesStateByPlace(
                fosterHome.country,
                fosterHome.city,
                nonHumanAnimal.nonHumanAnimalType
            )

            runCurrent()

            checkAllFosterHomesViewmodel.allFosterHomesState.test {
                assertEquals(
                    UiState.Success(listOf(UiFosterHome(fosterHome.copy(imageUrl = "")))),
                    awaitItem()
                )
                ensureAllEventsConsumed()
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given a user requesting all FHs from cordoba_when the user clicks on the search button but the app fails saving FHs in the local repo_then FHs are not saved in the local repo but displayed`() =
        runTest {
            val checkAllFosterHomesViewmodel = getCheckAllFosterHomesViewmodel(
                getLocalCacheEntityReturnForCountryCity = null,
                fosterHomeWithAllNonHumanAnimalLocalDataReturn = null,
                insertedFosterHomeInLocalRowIdArg = 0
            )
            checkAllFosterHomesViewmodel.fetchAllFosterHomesStateByPlace(
                fosterHome.country,
                fosterHome.city,
                nonHumanAnimal.nonHumanAnimalType
            )

            runCurrent()

            checkAllFosterHomesViewmodel.allFosterHomesState.test {
                assertEquals(UiState.Success(listOf(UiFosterHome(fosterHome))), awaitItem())
                ensureAllEventsConsumed()
            }
            verify {
                log.e(
                    "CheckAllMyFosterHomesUtilImpl",
                    "Error adding the foster home ${fosterHome.id} to local database"
                )
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given a user requesting all FHs from cordoba_when the user clicks on the search button but the app fails saving FHs in the local cache_then FHs are not saved in the local cache but displayed`() =
        runTest {
            val checkAllFosterHomesViewmodel = getCheckAllFosterHomesViewmodel(
                getLocalCacheEntityReturnForCountryCity = null,
                fosterHomeWithAllNonHumanAnimalLocalDataReturn = null,
                localCacheIdInsertedInLocalDatasourceArg = 0
            )
            checkAllFosterHomesViewmodel.fetchAllFosterHomesStateByPlace(
                fosterHome.country,
                fosterHome.city,
                nonHumanAnimal.nonHumanAnimalType
            )

            runCurrent()

            checkAllFosterHomesViewmodel.allFosterHomesState.test {
                assertEquals(UiState.Success(listOf(UiFosterHome(fosterHome))), awaitItem())
                ensureAllEventsConsumed()
            }
            verify {
                log.e(
                    "CheckAllMyFosterHomesUtilImpl",
                    "Error adding ${fosterHome.id} to local cache in section ${Section.FOSTER_HOMES}"
                )
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given a user requesting all foster homes from cordoba with outdated cache_when the user clicks on the search button_then foster homes are modified in the local repository and displayed`() =
        runTest {
            val checkAllFosterHomesViewmodel = getCheckAllFosterHomesViewmodel(
                getLocalCacheEntityReturnForCountryCity = localCache.copy(
                    cachedObjectId = fosterHome.country + fosterHome.city,
                    section = Section.FOSTER_HOMES,
                    timestamp = 123L
                ).toEntity()
            )
            checkAllFosterHomesViewmodel.fetchAllFosterHomesStateByPlace(
                fosterHome.country,
                fosterHome.city,
                nonHumanAnimal.nonHumanAnimalType
            )

            runCurrent()

            checkAllFosterHomesViewmodel.allFosterHomesState.test {
                assertEquals(UiState.Success(listOf(UiFosterHome(fosterHome))), awaitItem())
                ensureAllEventsConsumed()
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given a user requesting all foster homes from cordoba with outdated cache and no image_when the user clicks on the search button_then foster homes are modified in the local repository and displayed`() =
        runTest {
            val checkAllFosterHomesViewmodel = getCheckAllFosterHomesViewmodel(
                getLocalCacheEntityReturnForCountryCity = localCache.copy(
                    cachedObjectId = fosterHome.country + fosterHome.city,
                    section = Section.FOSTER_HOMES,
                    timestamp = 123L
                ).toEntity(),
                remoteFosterHomesByCountryAndCity = flowOf(
                    listOf(
                        fosterHome.copy(imageUrl = "").toData()
                    )
                )
            )
            checkAllFosterHomesViewmodel.fetchAllFosterHomesStateByPlace(
                fosterHome.country,
                fosterHome.city,
                nonHumanAnimal.nonHumanAnimalType
            )

            runCurrent()

            checkAllFosterHomesViewmodel.allFosterHomesState.test {
                assertEquals(
                    UiState.Success(listOf(UiFosterHome(fosterHome.copy(imageUrl = "")))),
                    awaitItem()
                )
                ensureAllEventsConsumed()
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given a user requesting all FHs from cordoba with outdated cache_when the user clicks on the search button but the app fails saving FHs in the local repo_then FHs are not saved in repo but displayed`() =
        runTest {
            val checkAllFosterHomesViewmodel = getCheckAllFosterHomesViewmodel(
                getLocalCacheEntityReturnForCountryCity = localCache.copy(
                    cachedObjectId = fosterHome.country + fosterHome.city,
                    section = Section.FOSTER_HOMES,
                    timestamp = 123L
                ).toEntity(),
                modifiedFosterHomeInLocalRowsUpdatedArg = 0
            )
            checkAllFosterHomesViewmodel.fetchAllFosterHomesStateByPlace(
                fosterHome.country,
                fosterHome.city,
                nonHumanAnimal.nonHumanAnimalType
            )

            runCurrent()

            checkAllFosterHomesViewmodel.allFosterHomesState.test {
                assertEquals(UiState.Success(listOf(UiFosterHome(fosterHome))), awaitItem())
                ensureAllEventsConsumed()
            }
            verify {
                log.e(
                    "CheckAllMyFosterHomesUtilImpl",
                    "Error modifying the foster home ${fosterHome.id} in local database"
                )
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given a user requesting all FHs from cordoba with outdated cache_when the user clicks on the search button but app fails saving FHs in the local cache_then FHs are not saved in cache but displayed`() =
        runTest {
            val checkAllFosterHomesViewmodel = getCheckAllFosterHomesViewmodel(
                getLocalCacheEntityReturnForCountryCity = localCache.copy(
                    cachedObjectId = fosterHome.country + fosterHome.city,
                    section = Section.FOSTER_HOMES,
                    timestamp = 123L
                ).toEntity(),
                localCacheUpdatedInLocalDatasourceArg = 0
            )
            checkAllFosterHomesViewmodel.fetchAllFosterHomesStateByPlace(
                fosterHome.country,
                fosterHome.city,
                nonHumanAnimal.nonHumanAnimalType
            )

            runCurrent()

            checkAllFosterHomesViewmodel.allFosterHomesState.test {
                assertEquals(UiState.Success(listOf(UiFosterHome(fosterHome))), awaitItem())
                ensureAllEventsConsumed()
            }
            verify {
                log.e(
                    "CheckAllMyFosterHomesUtilImpl",
                    "Error updating ${fosterHome.id} in local cache in section ${Section.FOSTER_HOMES}"
                )
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given a user requesting all FHs from cordoba with recent cache_when the user clicks on the search button_then FHs are retrieved from local cache and displayed`() =
        runTest {
            val checkAllFosterHomesViewmodel = getCheckAllFosterHomesViewmodel()
            checkAllFosterHomesViewmodel.fetchAllFosterHomesStateByPlace(
                fosterHome.country,
                fosterHome.city,
                nonHumanAnimal.nonHumanAnimalType
            )

            runCurrent()

            checkAllFosterHomesViewmodel.allFosterHomesState.test {
                assertEquals(UiState.Success(listOf(UiFosterHome(fosterHome))), awaitItem())
                ensureAllEventsConsumed()
            }
            verify {
                log.d(
                    "GetDataByManagingObjectLocalCacheTimestamp",
                    "Cache for ${fosterHome.country + fosterHome.city} in section ${Section.FOSTER_HOMES} is up-to-date."
                )
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given a user requesting all FHs by location_when the user clicks on the search button_then FHs are saved in the local repository but displayed`() =
        runTest {
            val checkAllFosterHomesViewmodel = getCheckAllFosterHomesViewmodel(
                getLocalCacheEntityReturnForLocation = null,
                fosterHomeWithAllNonHumanAnimalLocalDataReturn = null
            )
            checkAllFosterHomesViewmodel.fetchAllFosterHomesStateByLocation(
                nonHumanAnimal.nonHumanAnimalType
            )

            runCurrent()

            checkAllFosterHomesViewmodel.allFosterHomesState.test {
                assertEquals(UiState.Success(listOf(UiFosterHome(fosterHome, 22.1))), awaitItem())
                ensureAllEventsConsumed()
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given a user requesting all FHs by location_when the user clicks on the search button but there is an error updating the location_then an error is displayed`() =
        runTest {
            val checkAllFosterHomesViewmodel = getCheckAllFosterHomesViewmodel(
                getLocalCacheEntityReturnForLocation = null,
                fosterHomeWithAllNonHumanAnimalLocalDataReturn = null,
                locationReturn = Pair(0.0, 0.0)
            )
            checkAllFosterHomesViewmodel.fetchAllFosterHomesStateByLocation(nonHumanAnimal.nonHumanAnimalType)

            runCurrent()

            checkAllFosterHomesViewmodel.allFosterHomesState.test {
                assertTrue { awaitItem() is UiState.Error }
                ensureAllEventsConsumed()
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given a user requesting all foster homes by location with outdated cache_when the user clicks on the search button_then foster homes are modified in the local repository and displayed`() =
        runTest {
            val checkAllFosterHomesViewmodel = getCheckAllFosterHomesViewmodel(
                getLocalCacheEntityReturnForLocation = localCache.copy(
                    cachedObjectId = "${activistLongitude}${activistLatitude}",
                    section = Section.FOSTER_HOMES,
                    timestamp = 123L
                ).toEntity()
            )
            checkAllFosterHomesViewmodel.fetchAllFosterHomesStateByLocation(nonHumanAnimal.nonHumanAnimalType)

            runCurrent()

            checkAllFosterHomesViewmodel.allFosterHomesState.test {
                assertEquals(UiState.Success(listOf(UiFosterHome(fosterHome, 22.1))), awaitItem())
                ensureAllEventsConsumed()
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given a user requesting all FHs by location with recent cache_when the user clicks on the search button_then FHs are retrieved from local cache and displayed`() =
        runTest {
            val checkAllFosterHomesViewmodel = getCheckAllFosterHomesViewmodel()
            checkAllFosterHomesViewmodel.fetchAllFosterHomesStateByLocation(nonHumanAnimal.nonHumanAnimalType)

            runCurrent()

            checkAllFosterHomesViewmodel.allFosterHomesState.test {
                assertEquals(UiState.Success(listOf(UiFosterHome(fosterHome, 22.1))), awaitItem())
                ensureAllEventsConsumed()
            }
            verify {
                log.d(
                    "GetDataByManagingObjectLocalCacheTimestamp",
                    "Cache for ${activistLongitude}${activistLatitude} in section ${Section.FOSTER_HOMES} is up-to-date."
                )
            }
        }
}
