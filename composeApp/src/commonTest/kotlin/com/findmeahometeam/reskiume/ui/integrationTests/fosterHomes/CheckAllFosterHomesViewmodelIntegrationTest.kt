package com.findmeahometeam.reskiume.ui.integrationTests.fosterHomes

import app.cash.turbine.test
import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.activistLatitude
import com.findmeahometeam.reskiume.activistLongitude
import com.findmeahometeam.reskiume.authUser
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
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeAuthRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeCheckNonHumanAnimalUtil
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeFireStoreRemoteFosterHomeRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeKonnectivity
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeLocalCacheRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeLocalFosterHomeRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeLocationRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeLog
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeManageImagePath
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeStorageRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeStringProvider
import com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal.CheckNonHumanAnimalUtil
import com.findmeahometeam.reskiume.ui.util.ManageImagePath
import com.findmeahometeam.reskiume.ui.util.StringProvider
import com.findmeahometeam.reskiume.user
import com.findmeahometeam.reskiume.userPwd
import com.plusmobileapps.konnectivity.Konnectivity
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CheckAllFosterHomesViewmodelIntegrationTest : CoroutineTestDispatcher() {

    private val stringProvider: StringProvider =
        FakeStringProvider("I found a non-human animal in the street. What can I do?")

    private fun getCheckAllFosterHomesViewmodel(
        authRepository: AuthRepository = FakeAuthRepository(
            authUser = authUser,
            authEmail = user.email,
            authPassword = userPwd
        ),
        localCacheRepository: LocalCacheRepository = FakeLocalCacheRepository(),
        fireStoreRemoteFosterHomeRepository: FireStoreRemoteFosterHomeRepository = FakeFireStoreRemoteFosterHomeRepository(),
        checkNonHumanAnimalUtil: CheckNonHumanAnimalUtil = FakeCheckNonHumanAnimalUtil(),
        localFosterHomeRepository: LocalFosterHomeRepository = FakeLocalFosterHomeRepository(),
        locationRepository: LocationRepository = FakeLocationRepository(
            location = Pair(
                activistLongitude,
                activistLatitude
            )
        ),
        storageRepository: StorageRepository = FakeStorageRepository(),
        konnectivity: Konnectivity = FakeKonnectivity(),
        manageImagePath: ManageImagePath = FakeManageImagePath(),
        log: Log = FakeLog()
    ): CheckAllFosterHomesViewmodel {

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
            stringProvider,
            observeAuthStateInAuthDataSource,
            getDataByManagingObjectLocalCacheTimestamp,
            getAllFosterHomesByCountryAndCityFromRemoteRepository,
            downloadImageToLocalDataSource,
            getFosterHomeFromLocalRepository,
            insertFosterHomeInLocalRepository,
            insertCacheInLocalRepository,
            modifyFosterHomeInLocalRepository,
            modifyCacheInLocalRepository,
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
                    .map { it to stringProvider.getStringResource(it.toStringResource()) }
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
                    .map { it to stringProvider.getStringResource(it.toStringResource()) }
                assertEquals(expectedResult, awaitItem())
                awaitComplete()
            }
        }

    @Test
    fun `given a user requesting all foster homes from cordoba_when the user clicks on the search button_then foster homes are saved in the local repository and displayed`() =
        runTest {
            val checkAllFosterHomesViewmodel = getCheckAllFosterHomesViewmodel(
                fireStoreRemoteFosterHomeRepository = FakeFireStoreRemoteFosterHomeRepository(
                    remoteFosterHomeList = mutableListOf(fosterHome.toData())
                )
            )
            checkAllFosterHomesViewmodel.fetchAllFosterHomesStateByPlace(
                fosterHome.country,
                fosterHome.city,
                nonHumanAnimal.nonHumanAnimalType
            )

            checkAllFosterHomesViewmodel.allFosterHomesState.test {
                assertTrue { awaitItem() is UiState.Loading }
                assertEquals(
                    UiState.Success(listOf(UiFosterHome(fosterHome.copy(imageUrl = "${fosterHome.ownerId}${fosterHome.id}.webp")))),
                    awaitItem()
                )
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `given a user requesting all FHs from cordoba_when the user clicks on the search button but some FHs do not have avatar_then FHs are saved without image in the local repository and displayed`() =
        runTest {
            val checkAllFosterHomesViewmodel = getCheckAllFosterHomesViewmodel(
                fireStoreRemoteFosterHomeRepository = FakeFireStoreRemoteFosterHomeRepository(
                    remoteFosterHomeList = mutableListOf(fosterHome.copy(imageUrl = "").toData())
                )
            )
            checkAllFosterHomesViewmodel.fetchAllFosterHomesStateByPlace(
                fosterHome.country,
                fosterHome.city,
                nonHumanAnimal.nonHumanAnimalType
            )

            checkAllFosterHomesViewmodel.allFosterHomesState.test {
                assertTrue { awaitItem() is UiState.Loading }
                assertEquals(
                    UiState.Success(listOf(UiFosterHome(fosterHome.copy(imageUrl = "")))),
                    awaitItem()
                )
                ensureAllEventsConsumed()
            }
        }


    @Test
    fun `given a user requesting all FHs from cordoba_when the user clicks on the search button but the app fails saving FHs in the local cache_then FHs are not saved in the local cache but displayed`() =
        runTest {
            val checkAllFosterHomesViewmodel = getCheckAllFosterHomesViewmodel(
                fireStoreRemoteFosterHomeRepository = FakeFireStoreRemoteFosterHomeRepository(
                    remoteFosterHomeList = mutableListOf(fosterHome.toData())
                ),
                localCacheRepository = FakeLocalCacheRepository(
                    localCacheList = mutableListOf(
                        localCache.copy(
                            cachedObjectId = fosterHome.id,
                            section = Section.FOSTER_HOMES
                        ).toEntity()
                    )
                )
            )
            checkAllFosterHomesViewmodel.fetchAllFosterHomesStateByPlace(
                fosterHome.country,
                fosterHome.city,
                nonHumanAnimal.nonHumanAnimalType
            )

            checkAllFosterHomesViewmodel.allFosterHomesState.test {
                assertTrue { awaitItem() is UiState.Loading }
                assertEquals(
                    UiState.Success(listOf(UiFosterHome(fosterHome.copy(imageUrl = "${fosterHome.ownerId}${fosterHome.id}.webp")))),
                    awaitItem()
                )
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `given a user requesting all foster homes from cordoba with outdated cache_when the user clicks on the search button_then foster homes are modified in the local repository and displayed`() =
        runTest {
            val checkAllFosterHomesViewmodel = getCheckAllFosterHomesViewmodel(
                fireStoreRemoteFosterHomeRepository = FakeFireStoreRemoteFosterHomeRepository(
                    remoteFosterHomeList = mutableListOf(fosterHome.toData())
                ),
                localFosterHomeRepository = FakeLocalFosterHomeRepository(
                    localFosterHomeWithAllNonHumanAnimalDataList = mutableListOf(
                        fosterHomeWithAllNonHumanAnimalData
                    )
                ),
                localCacheRepository = FakeLocalCacheRepository(
                    localCacheList = mutableListOf(
                        localCache.copy(
                            cachedObjectId = fosterHome.country + fosterHome.city,
                            section = Section.FOSTER_HOMES,
                            timestamp = 123L
                        ).toEntity(),
                        localCache.copy(
                            cachedObjectId = fosterHome.id,
                            section = Section.FOSTER_HOMES,
                            timestamp = 123L
                        ).toEntity()
                    )
                )
            )
            checkAllFosterHomesViewmodel.fetchAllFosterHomesStateByPlace(
                fosterHome.country,
                fosterHome.city,
                nonHumanAnimal.nonHumanAnimalType
            )

            checkAllFosterHomesViewmodel.allFosterHomesState.test {
                assertTrue { awaitItem() is UiState.Loading }
                assertEquals(
                    UiState.Success(listOf(UiFosterHome(fosterHome.copy(imageUrl = "${fosterHome.ownerId}${fosterHome.id}.webp")))),
                    awaitItem()
                )
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `given a user requesting all foster homes from cordoba with outdated cache and no image_when the user clicks on the search button_then foster homes are modified in the local repository and displayed`() =
        runTest {
            val checkAllFosterHomesViewmodel = getCheckAllFosterHomesViewmodel(
                fireStoreRemoteFosterHomeRepository = FakeFireStoreRemoteFosterHomeRepository(
                    remoteFosterHomeList = mutableListOf(fosterHome.copy(imageUrl = "").toData())
                ),
                localFosterHomeRepository = FakeLocalFosterHomeRepository(
                    localFosterHomeWithAllNonHumanAnimalDataList = mutableListOf(
                        fosterHomeWithAllNonHumanAnimalData
                    )
                ),
                localCacheRepository = FakeLocalCacheRepository(
                    localCacheList = mutableListOf(
                        localCache.copy(
                            cachedObjectId = fosterHome.country + fosterHome.city,
                            section = Section.FOSTER_HOMES,
                            timestamp = 123L
                        ).toEntity(),
                        localCache.copy(
                            cachedObjectId = fosterHome.id,
                            section = Section.FOSTER_HOMES,
                            timestamp = 123L
                        ).toEntity()
                    )
                )
            )
            checkAllFosterHomesViewmodel.fetchAllFosterHomesStateByPlace(
                fosterHome.country,
                fosterHome.city,
                nonHumanAnimal.nonHumanAnimalType
            )

            checkAllFosterHomesViewmodel.allFosterHomesState.test {
                assertTrue { awaitItem() is UiState.Loading }
                assertEquals(
                    UiState.Success(listOf(UiFosterHome(fosterHome.copy(imageUrl = "")))),
                    awaitItem()
                )
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `given a user requesting all FHs from cordoba with recent cache_when the user clicks on the search button_then FHs are retrieved from local cache and displayed`() =
        runTest {
            val checkAllFosterHomesViewmodel = getCheckAllFosterHomesViewmodel(
                localFosterHomeRepository = FakeLocalFosterHomeRepository(
                    localFosterHomeWithAllNonHumanAnimalDataList = mutableListOf(
                        fosterHomeWithAllNonHumanAnimalData
                    )
                ),
                localCacheRepository = FakeLocalCacheRepository(
                    localCacheList = mutableListOf(
                        localCache.copy(
                            cachedObjectId = fosterHome.country + fosterHome.city,
                            section = Section.FOSTER_HOMES
                        ).toEntity(),
                        localCache.copy(
                            cachedObjectId = fosterHome.id,
                            section = Section.FOSTER_HOMES
                        ).toEntity()
                    )
                )
            )
            checkAllFosterHomesViewmodel.fetchAllFosterHomesStateByPlace(
                fosterHome.country,
                fosterHome.city,
                nonHumanAnimal.nonHumanAnimalType
            )

            checkAllFosterHomesViewmodel.allFosterHomesState.test {
                assertTrue { awaitItem() is UiState.Loading }
                assertEquals(
                    UiState.Success(listOf(UiFosterHome(fosterHome))),
                    awaitItem()
                )
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `given a user requesting all FHs by location_when the user clicks on the search button_then FHs are saved in the local repository but displayed`() =
        runTest {
            val checkAllFosterHomesViewmodel = getCheckAllFosterHomesViewmodel(
                fireStoreRemoteFosterHomeRepository = FakeFireStoreRemoteFosterHomeRepository(
                    remoteFosterHomeList = mutableListOf(fosterHome.toData())
                )
            )
            checkAllFosterHomesViewmodel.fetchAllFosterHomesStateByLocation(nonHumanAnimal.nonHumanAnimalType)

            checkAllFosterHomesViewmodel.allFosterHomesState.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertEquals(
                    UiState.Success(
                        listOf(
                            UiFosterHome(
                                fosterHome.copy(imageUrl = "${fosterHome.ownerId}${fosterHome.id}.webp"),
                                22.1
                            )
                        )
                    ),
                    awaitItem()
                )
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `given a user requesting all FHs by location_when the user clicks on the search button but there is an error updating the location_then an error is displayed`() =
        runTest {
            val checkAllFosterHomesViewmodel = getCheckAllFosterHomesViewmodel(
                fireStoreRemoteFosterHomeRepository = FakeFireStoreRemoteFosterHomeRepository(
                    remoteFosterHomeList = mutableListOf(fosterHome.toData())
                ),
                locationRepository = FakeLocationRepository(
                    location = Pair(0.0, 0.0)
                )
            )
            checkAllFosterHomesViewmodel.fetchAllFosterHomesStateByLocation(nonHumanAnimal.nonHumanAnimalType)

            checkAllFosterHomesViewmodel.allFosterHomesState.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Error }
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `given a user requesting all foster homes by location with outdated cache_when the user clicks on the search button_then foster homes are modified in the local repository and displayed`() =
        runTest {
            val checkAllFosterHomesViewmodel = getCheckAllFosterHomesViewmodel(
                fireStoreRemoteFosterHomeRepository = FakeFireStoreRemoteFosterHomeRepository(
                    remoteFosterHomeList = mutableListOf(fosterHome.toData())
                ),
                localFosterHomeRepository = FakeLocalFosterHomeRepository(
                    localFosterHomeWithAllNonHumanAnimalDataList = mutableListOf(
                        fosterHomeWithAllNonHumanAnimalData
                    )
                ),
                localCacheRepository = FakeLocalCacheRepository(
                    localCacheList = mutableListOf(
                        localCache.copy(
                            cachedObjectId = "${activistLongitude}${activistLatitude}",
                            section = Section.FOSTER_HOMES,
                            timestamp = 123L
                        ).toEntity(),
                        localCache.copy(
                            cachedObjectId = fosterHome.id,
                            section = Section.FOSTER_HOMES,
                            timestamp = 123L
                        ).toEntity()
                    )
                )
            )
            checkAllFosterHomesViewmodel.fetchAllFosterHomesStateByLocation(nonHumanAnimal.nonHumanAnimalType)

            checkAllFosterHomesViewmodel.allFosterHomesState.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertEquals(
                    UiState.Success(
                        listOf(
                            UiFosterHome(
                                fosterHome.copy(imageUrl = "${fosterHome.ownerId}${fosterHome.id}.webp"),
                                22.1
                            )
                        )
                    ),
                    awaitItem()
                )
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `given a user requesting all FHs by location with recent cache_when the user clicks on the search button_then FHs are retrieved from local cache and displayed`() =
        runTest {
            val checkAllFosterHomesViewmodel = getCheckAllFosterHomesViewmodel(
                localFosterHomeRepository = FakeLocalFosterHomeRepository(
                    localFosterHomeWithAllNonHumanAnimalDataList = mutableListOf(
                        fosterHomeWithAllNonHumanAnimalData
                    )
                ),
                localCacheRepository = FakeLocalCacheRepository(
                    localCacheList = mutableListOf(
                        localCache.copy(
                            cachedObjectId = "${activistLongitude}${activistLatitude}",
                            section = Section.FOSTER_HOMES
                        ).toEntity(),
                        localCache.copy(
                            cachedObjectId = fosterHome.id,
                            section = Section.FOSTER_HOMES
                        ).toEntity()
                    )
                )
            )
            checkAllFosterHomesViewmodel.fetchAllFosterHomesStateByLocation(nonHumanAnimal.nonHumanAnimalType)

            checkAllFosterHomesViewmodel.allFosterHomesState.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertEquals(
                    UiState.Success(listOf(UiFosterHome(fosterHome, 22.1))),
                    awaitItem()
                )
                ensureAllEventsConsumed()
            }
        }
}
