package com.findmeahometeam.reskiume.ui.unitTests.rescueEvents

import app.cash.turbine.test
import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.activistLatitude
import com.findmeahometeam.reskiume.activistLongitude
import com.findmeahometeam.reskiume.authUser
import com.findmeahometeam.reskiume.data.database.entity.LocalCacheEntity
import com.findmeahometeam.reskiume.data.database.entity.rescueEvent.RescueEventWithAllNeedsAndNonHumanAnimalData
import com.findmeahometeam.reskiume.data.remote.response.AuthUser
import com.findmeahometeam.reskiume.data.remote.response.rescueEvent.RemoteRescueEvent
import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimalState
import com.findmeahometeam.reskiume.domain.repository.local.LocalCacheRepository
import com.findmeahometeam.reskiume.domain.repository.local.LocalNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.repository.local.LocalRescueEventRepository
import com.findmeahometeam.reskiume.domain.repository.local.LocalUserRepository
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteRescueEvent.FireStoreRemoteRescueEventRepository
import com.findmeahometeam.reskiume.domain.repository.remote.storage.StorageRepository
import com.findmeahometeam.reskiume.domain.repository.util.location.LocationRepository
import com.findmeahometeam.reskiume.domain.usecases.authUser.ObserveAuthStateInAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.DownloadImageToLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.GetImagePathForFileNameFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.localCache.GetDataByManagingObjectLocalCacheTimestamp
import com.findmeahometeam.reskiume.domain.usecases.localCache.InsertCacheInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.localCache.ModifyCacheInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.GetAllRescueEventsByCountryAndCityFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.GetAllRescueEventsByCountryAndCityFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.GetAllRescueEventsByLocationFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.GetAllRescueEventsByLocationFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.GetRescueEventFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.InsertRescueEventInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.ModifyRescueEventInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.user.GetUserFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.util.location.GetLocationFromLocationRepository
import com.findmeahometeam.reskiume.domain.usecases.util.location.ObserveIfLocationEnabledFromLocationRepository
import com.findmeahometeam.reskiume.domain.usecases.util.location.RequestEnableLocationFromLocationRepository
import com.findmeahometeam.reskiume.localCache
import com.findmeahometeam.reskiume.nonHumanAnimal
import com.findmeahometeam.reskiume.rescueEvent
import com.findmeahometeam.reskiume.rescueEventWithAllNeedsAndNonHumanAnimalData
import com.findmeahometeam.reskiume.ui.core.components.UiState
import com.findmeahometeam.reskiume.ui.profile.checkAllMyRescueEvents.CheckAllMyRescueEventsUtilImpl
import com.findmeahometeam.reskiume.ui.profile.checkAllMyRescueEvents.UiRescueEvent
import com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal.CheckNonHumanAnimalUtil
import com.findmeahometeam.reskiume.ui.rescueEvents.checkAllRescueEvents.CheckAllRescueEventsViewmodel
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
import kotlin.collections.List
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CheckAllRescueEventsViewmodelTest : CoroutineTestDispatcher() {

    private val onInsertLocalCacheEntity = Capture.slot<(rowId: Long) -> Unit>()

    private val onModifyLocalCacheEntity = Capture.slot<(rowsUpdated: Int) -> Unit>()

    private val onSaveImageToLocalForNonHumanAnimal = Capture.slot<(String) -> Unit>()

    private val onSaveImageToLocalForSecondNonHumanAnimal = Capture.slot<(String) -> Unit>()

    private val onSaveImageToLocalForRescueEvent = Capture.slot<(String) -> Unit>()

    private val onInsertRescueEvent = Capture.slot<suspend (rowId: Long) -> Unit>()

    private val onInsertRescueEventWithoutImage = Capture.slot<suspend (rowId: Long) -> Unit>()

    private val onInsertNeedToCoverForRescueEvent = Capture.slot<(rowId: Long) -> Unit>()

    private val onInsertSecondNeedToCoverForRescueEvent =
        Capture.slot<(rowId: Long) -> Unit>()

    private val onInsertNonHumanAnimalToRescueForRescueEvent =
        Capture.slot<(rowId: Long) -> Unit>()

    private val onInsertSecondNonHumanAnimalToRescueForRescueEvent =
        Capture.slot<(rowId: Long) -> Unit>()

    private val onModifyRescueEvent = Capture.slot<suspend (rowsUpdated: Int) -> Unit>()

    private val onModifyRescueEventWithoutImage = Capture.slot<suspend (rowsUpdated: Int) -> Unit>()

    private val onInsertNonHumanAnimalInLocal = Capture.slot<(rowId: Long) -> Unit>()

    private val onModifyNonHumanAnimalInLocal = Capture.slot<(Int) -> Unit>()

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

    private fun getCheckAllRescueEventsViewmodel(
        authStateReturn: AuthUser? = authUser,
        getLocalCacheEntityReturnForCountryCity: LocalCacheEntity? =
            localCache.copy(
                cachedObjectId = rescueEvent.country + rescueEvent.city,
                section = Section.RESCUE_EVENTS
            ).toEntity(),
        getLocalCacheEntityReturnForRescueEvent: LocalCacheEntity? =
            localCache.copy(
                cachedObjectId = rescueEvent.id,
                section = Section.RESCUE_EVENTS
            ).toEntity(),
        getLocalCacheEntityReturnForLocation: LocalCacheEntity? =
            localCache.copy(
                cachedObjectId = "${activistLongitude}${activistLatitude}",
                section = Section.RESCUE_EVENTS
            ).toEntity(),
        localCacheIdInsertedInLocalDatasourceArg: Long = 1L,
        localCacheUpdatedInLocalDatasourceArg: Int = 1,
        remoteRescueEventsByCountryAndCity: Flow<List<RemoteRescueEvent>> = flowOf(
            listOf(
                rescueEvent.toData()
            )
        ),
        remoteRescueEventsByLocation: Flow<List<RemoteRescueEvent>> = flowOf(listOf(rescueEvent.toData())),
        absolutePathArgForNonHumanAnimal: String = nonHumanAnimal.imageUrl,
        absolutePathArgForSecondNonHumanAnimal: String = nonHumanAnimal.imageUrl,
        absolutePathArgForRescueEvent: String = rescueEvent.imageUrl,
        insertedRescueEventInLocalRowIdArg: Long = 1L,
        insertedRescueEventWithoutImageInLocalRowIdArg: Long = 1L,
        insertedNeedToCoverForRescueEventInLocalRowIdArg: Long = 1L,
        insertedSecondNeedToCoverForRescueEventInLocalRowIdArg: Long = 1L,
        insertedRowIdOfNonHumanAnimalToRescueForRescueEventInLocalArg: Long = 1L,
        insertedRowIdOfSecondNonHumanAnimalToRescueForRescueEventInLocalArg: Long = 1L,
        modifiedRescueEventInLocalRowsUpdatedArg: Int = 1,
        modifiedRescueEventWithoutImageInLocalRowsUpdatedArg: Int = 1,
        rescueEventWithAllNonHumanAnimalLocalDataReturn: RescueEventWithAllNeedsAndNonHumanAnimalData? = rescueEventWithAllNeedsAndNonHumanAnimalData,
        rescueEventWithAllNonHumanAnimalLocalDataByCountryCityReturn: Flow<List<RescueEventWithAllNeedsAndNonHumanAnimalData>> = flowOf(
            listOf(rescueEventWithAllNeedsAndNonHumanAnimalData)
        ),
        rescueEventWithAllNonHumanAnimalLocalDataByLocationReturn: Flow<List<RescueEventWithAllNeedsAndNonHumanAnimalData>> = flowOf(
            listOf(rescueEventWithAllNeedsAndNonHumanAnimalData)
        ),
        nonHumanAnimalIdInsertedInLocalDatasourceArg: Long = 1L,
        rowsUpdatedNonHumanAnimalArg: Int = 1,
        locationReturn: Pair<Double, Double> = Pair(activistLongitude, activistLatitude)
    ): CheckAllRescueEventsViewmodel {

        val authRepository: AuthRepository = mock {
            everySuspend { authState } returns (flowOf(authStateReturn))
        }

        val localUserRepository: LocalUserRepository = mock {
            everySuspend {
                getUser(user.uid)
            } returns user
        }

        val localCacheRepository: LocalCacheRepository = mock {

            everySuspend {
                getLocalCacheEntity(
                    rescueEvent.country + rescueEvent.city,
                    Section.RESCUE_EVENTS
                )
            } returns getLocalCacheEntityReturnForCountryCity

            everySuspend {
                getLocalCacheEntity(
                    rescueEvent.id,
                    Section.RESCUE_EVENTS
                )
            } returns getLocalCacheEntityReturnForRescueEvent

            everySuspend {
                getLocalCacheEntity(
                    "${activistLongitude}${activistLatitude}",
                    Section.RESCUE_EVENTS
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

        val fireStoreRemoteRescueEventRepository: FireStoreRemoteRescueEventRepository = mock {

            everySuspend {
                getAllRemoteRescueEventsByCountryAndCity(
                    rescueEvent.country,
                    rescueEvent.city
                )
            } returns remoteRescueEventsByCountryAndCity


            everySuspend {
                getAllRemoteRescueEventsByLocation(
                    activistLongitude,
                    activistLatitude,
                    any(),
                    any()
                )
            } returns remoteRescueEventsByLocation
        }

        val checkNonHumanAnimalUtil: CheckNonHumanAnimalUtil = mock {

            every {
                getNonHumanAnimalFlow(
                    nonHumanAnimal.id,
                    nonHumanAnimal.caregiverId,
                    any()
                )
            } returns flowOf(nonHumanAnimal)

            every {
                getNonHumanAnimalFlow(
                    nonHumanAnimal.id + "second",
                    nonHumanAnimal.caregiverId,
                    any()
                )
            } returns flowOf(nonHumanAnimal.copy(id = nonHumanAnimal.id + "second"))
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
                    nonHumanAnimal.id + "second",
                    Section.NON_HUMAN_ANIMALS,
                    capture(onSaveImageToLocalForSecondNonHumanAnimal)
                )
            } calls {
                onSaveImageToLocalForSecondNonHumanAnimal.get()
                    .invoke(absolutePathArgForSecondNonHumanAnimal)
            }

            every {
                downloadImage(
                    user.uid,
                    rescueEvent.id,
                    Section.RESCUE_EVENTS,
                    capture(onSaveImageToLocalForRescueEvent)
                )
            } calls { onSaveImageToLocalForRescueEvent.get().invoke(absolutePathArgForRescueEvent) }
        }

        val localRescueEventRepository: LocalRescueEventRepository = mock {

            everySuspend {
                insertRescueEvent(
                    rescueEvent.copy(savedBy = authUser.uid).toEntity(),
                    capture(onInsertRescueEvent)
                )
            } calls {
                onInsertRescueEvent.get().invoke(insertedRescueEventInLocalRowIdArg)
            }

            everySuspend {
                insertRescueEvent(
                    rescueEvent.copy(savedBy = authUser.uid, imageUrl = "").toEntity(),
                    capture(onInsertRescueEventWithoutImage)
                )
            } calls {
                onInsertRescueEventWithoutImage.get()
                    .invoke(insertedRescueEventWithoutImageInLocalRowIdArg)
            }

            everySuspend {
                insertNeedToCoverEntityForRescueEvent(
                    rescueEvent.allNeedsToCover[0].toEntity(),
                    capture(onInsertNeedToCoverForRescueEvent)
                )
            } calls {
                onInsertNeedToCoverForRescueEvent.get()
                    .invoke(insertedNeedToCoverForRescueEventInLocalRowIdArg)
            }

            everySuspend {
                insertNeedToCoverEntityForRescueEvent(
                    rescueEvent.allNeedsToCover[1].toEntity(),
                    capture(onInsertSecondNeedToCoverForRescueEvent)
                )
            } calls {
                onInsertSecondNeedToCoverForRescueEvent.get()
                    .invoke(insertedSecondNeedToCoverForRescueEventInLocalRowIdArg)
            }

            everySuspend {
                insertNonHumanAnimalToRescueEntityForRescueEvent(
                    rescueEvent.allNonHumanAnimalsToRescue[0].toEntity(),
                    capture(onInsertNonHumanAnimalToRescueForRescueEvent)
                )
            } calls {
                onInsertNonHumanAnimalToRescueForRescueEvent.get()
                    .invoke(insertedRowIdOfNonHumanAnimalToRescueForRescueEventInLocalArg)
            }

            everySuspend {
                insertNonHumanAnimalToRescueEntityForRescueEvent(
                    rescueEvent.allNonHumanAnimalsToRescue[1].toEntity(),
                    capture(onInsertSecondNonHumanAnimalToRescueForRescueEvent)
                )
            } calls {
                onInsertSecondNonHumanAnimalToRescueForRescueEvent.get()
                    .invoke(insertedRowIdOfSecondNonHumanAnimalToRescueForRescueEventInLocalArg)
            }

            everySuspend {
                modifyRescueEvent(
                    rescueEvent.copy(savedBy = authUser.uid).toEntity(),
                    capture(onModifyRescueEvent)
                )
            } calls {
                onModifyRescueEvent.get().invoke(modifiedRescueEventInLocalRowsUpdatedArg)
            }

            everySuspend {
                modifyRescueEvent(
                    rescueEvent.copy(savedBy = authUser.uid, imageUrl = "").toEntity(),
                    capture(onModifyRescueEventWithoutImage)
                )
            } calls {
                onModifyRescueEventWithoutImage.get()
                    .invoke(modifiedRescueEventWithoutImageInLocalRowsUpdatedArg)
            }

            everySuspend {
                getRescueEvent(rescueEvent.id)
            } returns rescueEventWithAllNonHumanAnimalLocalDataReturn

            every {
                getAllRescueEventsByCountryAndCity(rescueEvent.country, rescueEvent.city)
            } returns rescueEventWithAllNonHumanAnimalLocalDataByCountryCityReturn

            everySuspend {
                getAllRescueEventsByLocation(
                    activistLongitude,
                    activistLatitude,
                    any(),
                    any()
                )
            } returns rescueEventWithAllNonHumanAnimalLocalDataByLocationReturn
        }

        val localNonHumanAnimalRepository: LocalNonHumanAnimalRepository = mock {
            everySuspend {
                insertNonHumanAnimal(
                    nonHumanAnimal.toEntity(),
                    capture(onInsertNonHumanAnimalInLocal)
                )
            } calls {
                onInsertNonHumanAnimalInLocal.get()
                    .invoke(nonHumanAnimalIdInsertedInLocalDatasourceArg)
            }

            everySuspend {
                insertNonHumanAnimal(
                    nonHumanAnimal.copy(id = nonHumanAnimal.id + "second").toEntity(),
                    capture(onInsertNonHumanAnimalInLocal)
                )
            } calls {
                onInsertNonHumanAnimalInLocal.get()
                    .invoke(nonHumanAnimalIdInsertedInLocalDatasourceArg)
            }

            everySuspend {
                insertNonHumanAnimal(
                    nonHumanAnimal.copy(imageUrl = "").toEntity(),
                    capture(onInsertNonHumanAnimalInLocal)
                )
            } calls {
                onInsertNonHumanAnimalInLocal.get()
                    .invoke(nonHumanAnimalIdInsertedInLocalDatasourceArg)
            }

            everySuspend {
                modifyNonHumanAnimal(
                    nonHumanAnimal.copy(nonHumanAnimalState = NonHumanAnimalState.NEEDS_TO_BE_RESCUED)
                        .toEntity(),
                    capture(onModifyNonHumanAnimalInLocal)
                )
            } calls { onModifyNonHumanAnimalInLocal.get().invoke(rowsUpdatedNonHumanAnimalArg) }

            everySuspend {
                modifyNonHumanAnimal(
                    nonHumanAnimal.copy(
                        id = nonHumanAnimal.id + "second",
                        nonHumanAnimalState = NonHumanAnimalState.NEEDS_TO_BE_RESCUED
                    ).toEntity(),
                    capture(onModifyNonHumanAnimalInLocal)
                )
            } calls { onModifyNonHumanAnimalInLocal.get().invoke(rowsUpdatedNonHumanAnimalArg) }

            everySuspend {
                modifyNonHumanAnimal(
                    nonHumanAnimal.copy(imageUrl = "").toEntity(),
                    capture(onModifyNonHumanAnimalInLocal)
                )
            } calls { onModifyNonHumanAnimalInLocal.get().invoke(rowsUpdatedNonHumanAnimalArg) }

            every {
                getAllMyNonHumanAnimals(user.uid)
            } returns flowOf(listOf(nonHumanAnimal.toEntity()))
        }

        val manageImagePath: ManageImagePath = mock {

            every { getImagePathForFileName(nonHumanAnimal.imageUrl) } returns nonHumanAnimal.imageUrl

            every { getFileNameFromLocalImagePath(nonHumanAnimal.imageUrl) } returns nonHumanAnimal.imageUrl

            every { getImagePathForFileName(rescueEvent.imageUrl) } returns rescueEvent.imageUrl

            every { getFileNameFromLocalImagePath(rescueEvent.imageUrl) } returns rescueEvent.imageUrl

            every { getFileNameFromLocalImagePath("") } returns ""
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

        val getUserFromLocalDataSource =
            GetUserFromLocalDataSource(localUserRepository)

        val getDataByManagingObjectLocalCacheTimestamp =
            GetDataByManagingObjectLocalCacheTimestamp(localCacheRepository, log, konnectivity)

        val getAllRescueEventsByCountryAndCityFromRemoteRepository =
            GetAllRescueEventsByCountryAndCityFromRemoteRepository(
                fireStoreRemoteRescueEventRepository
            )

        val downloadImageToLocalDataSource =
            DownloadImageToLocalDataSource(storageRepository)

        val getRescueEventFromLocalRepository =
            GetRescueEventFromLocalRepository(localRescueEventRepository)

        val insertRescueEventInLocalRepository =
            InsertRescueEventInLocalRepository(
                checkNonHumanAnimalUtil,
                localRescueEventRepository,
                localNonHumanAnimalRepository,
                manageImagePath,
                authRepository,
                log
            )

        val insertCacheInLocalRepository =
            InsertCacheInLocalRepository(localCacheRepository)

        val modifyRescueEventInLocalRepository =
            ModifyRescueEventInLocalRepository(
                manageImagePath,
                checkNonHumanAnimalUtil,
                localRescueEventRepository,
                localNonHumanAnimalRepository,
                authRepository,
                log
            )

        val modifyCacheInLocalRepository = ModifyCacheInLocalRepository(localCacheRepository)

        val checkAllMyRescueEventsUtil = CheckAllMyRescueEventsUtilImpl(
            downloadImageToLocalDataSource,
            getRescueEventFromLocalRepository,
            insertRescueEventInLocalRepository,
            insertCacheInLocalRepository,
            modifyRescueEventInLocalRepository,
            modifyCacheInLocalRepository,
            log
        )

        val getAllRescueEventsByCountryAndCityFromLocalRepository =
            GetAllRescueEventsByCountryAndCityFromLocalRepository(localRescueEventRepository)

        val getImagePathForFileNameFromLocalDataSource =
            GetImagePathForFileNameFromLocalDataSource(manageImagePath)

        val getAllRescueEventsByLocationFromRemoteRepository =
            GetAllRescueEventsByLocationFromRemoteRepository(fireStoreRemoteRescueEventRepository)

        val getAllRescueEventsByLocationFromLocalRepository =
            GetAllRescueEventsByLocationFromLocalRepository(localRescueEventRepository)

        val observeIfLocationEnabledFromLocationRepository =
            ObserveIfLocationEnabledFromLocationRepository(locationRepository)

        val requestEnableLocationFromLocationRepository =
            RequestEnableLocationFromLocationRepository(locationRepository)

        val getLocationFromLocationRepository =
            GetLocationFromLocationRepository(locationRepository)

        return CheckAllRescueEventsViewmodel(
            observeAuthStateInAuthDataSource,
            getUserFromLocalDataSource,
            getStringProvider,
            getDataByManagingObjectLocalCacheTimestamp,
            getAllRescueEventsByCountryAndCityFromRemoteRepository,
            checkAllMyRescueEventsUtil,
            getAllRescueEventsByCountryAndCityFromLocalRepository,
            checkNonHumanAnimalUtil,
            getImagePathForFileNameFromLocalDataSource,
            getAllRescueEventsByLocationFromRemoteRepository,
            getAllRescueEventsByLocationFromLocalRepository,
            observeIfLocationEnabledFromLocationRepository,
            requestEnableLocationFromLocationRepository,
            getLocationFromLocationRepository,
            log
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given a user requesting all rescue events from cordoba_when the user clicks on the search button_then rescue events are saved in the local repository and displayed`() =
        runTest {
            val checkAllRescueEventsViewmodel = getCheckAllRescueEventsViewmodel(
                getLocalCacheEntityReturnForCountryCity = null,
                getLocalCacheEntityReturnForRescueEvent = null,
                rescueEventWithAllNonHumanAnimalLocalDataReturn = null
            )
            checkAllRescueEventsViewmodel.fetchAllRescueEventsStateByPlace(
                rescueEvent.country,
                rescueEvent.city
            )

            runCurrent()

            checkAllRescueEventsViewmodel.allRescueEventsState.test {
                assertEquals(
                    UiState.Success(
                        listOf(
                            UiRescueEvent(
                                rescueEvent,
                                listOf(
                                    nonHumanAnimal,
                                    nonHumanAnimal.copy(id = nonHumanAnimal.id + "second")
                                )
                            )
                        )
                    ), awaitItem()
                )
                ensureAllEventsConsumed()
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given a user requesting all REs from cordoba_when the user clicks on the search button but some REs do not have avatar_then REs are saved without image in the local repository and displayed`() =
        runTest {
            val checkAllRescueEventsViewmodel = getCheckAllRescueEventsViewmodel(
                remoteRescueEventsByCountryAndCity = flowOf(
                    listOf(rescueEvent.copy(imageUrl = "").toData())
                ),
                getLocalCacheEntityReturnForCountryCity = null,
                getLocalCacheEntityReturnForRescueEvent = null,
                rescueEventWithAllNonHumanAnimalLocalDataReturn = null,
                rescueEventWithAllNonHumanAnimalLocalDataByCountryCityReturn = flowOf(
                    listOf(
                        rescueEventWithAllNeedsAndNonHumanAnimalData.copy(
                            rescueEventEntity = rescueEvent.copy(
                                imageUrl = ""
                            ).toEntity()
                        )
                    )
                )
            )
            checkAllRescueEventsViewmodel.fetchAllRescueEventsStateByPlace(
                rescueEvent.country,
                rescueEvent.city
            )

            runCurrent()

            checkAllRescueEventsViewmodel.allRescueEventsState.test {
                assertEquals(
                    UiState.Success(
                        listOf(
                            UiRescueEvent(
                                rescueEvent.copy(imageUrl = ""),
                                listOf(
                                    nonHumanAnimal,
                                    nonHumanAnimal.copy(id = nonHumanAnimal.id + "second")
                                )
                            )
                        )
                    ),
                    awaitItem()
                )
                ensureAllEventsConsumed()
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given a user requesting all REs from cordoba_when the user clicks on the search button but the app fails saving REs in the local repo_then REs are not saved in the local repo but displayed`() =
        runTest {
            val checkAllRescueEventsViewmodel = getCheckAllRescueEventsViewmodel(
                getLocalCacheEntityReturnForCountryCity = null,
                getLocalCacheEntityReturnForRescueEvent = null,
                rescueEventWithAllNonHumanAnimalLocalDataReturn = null,
                insertedRescueEventInLocalRowIdArg = 0
            )
            checkAllRescueEventsViewmodel.fetchAllRescueEventsStateByPlace(
                rescueEvent.country,
                rescueEvent.city
            )

            runCurrent()

            checkAllRescueEventsViewmodel.allRescueEventsState.test {
                assertEquals(
                    UiState.Success(
                        listOf(
                            UiRescueEvent(
                                rescueEvent, listOf(
                                    nonHumanAnimal,
                                    nonHumanAnimal.copy(id = nonHumanAnimal.id + "second")
                                )
                            )
                        )
                    ), awaitItem()
                )
                ensureAllEventsConsumed()
            }
            verify {
                log.e(
                    "CheckAllMyRescueEventsUtilImpl",
                    "insertRescueEventInLocalRepo: Error adding the rescue event ${rescueEvent.id} to local database"
                )
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given a user requesting all REs from cordoba_when the user clicks on the search button but the app fails saving REs in the local cache_then REs are not saved in the local cache but displayed`() =
        runTest {
            val checkAllRescueEventsViewmodel = getCheckAllRescueEventsViewmodel(
                getLocalCacheEntityReturnForCountryCity = null,
                getLocalCacheEntityReturnForRescueEvent = null,
                rescueEventWithAllNonHumanAnimalLocalDataReturn = null,
                localCacheIdInsertedInLocalDatasourceArg = 0
            )
            checkAllRescueEventsViewmodel.fetchAllRescueEventsStateByPlace(
                rescueEvent.country,
                rescueEvent.city
            )

            runCurrent()

            checkAllRescueEventsViewmodel.allRescueEventsState.test {
                assertEquals(
                    UiState.Success(
                        listOf(
                            UiRescueEvent(
                                rescueEvent, listOf(
                                    nonHumanAnimal,
                                    nonHumanAnimal.copy(id = nonHumanAnimal.id + "second")
                                )
                            )
                        )
                    ), awaitItem()
                )
                ensureAllEventsConsumed()
            }
            verify {
                log.e(
                    "CheckAllMyRescueEventsUtilImpl",
                    "insertRescueEventInLocalRepo: Error adding ${rescueEvent.id} to local cache in section ${Section.RESCUE_EVENTS}"
                )
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given a user requesting all rescue events from cordoba with outdated cache_when the user clicks on the search button_then rescue events are modified in the local repository and displayed`() =
        runTest {
            val checkAllRescueEventsViewmodel = getCheckAllRescueEventsViewmodel(
                getLocalCacheEntityReturnForCountryCity = localCache.copy(
                    cachedObjectId = rescueEvent.country + rescueEvent.city,
                    section = Section.RESCUE_EVENTS,
                    timestamp = 123L
                ).toEntity(),
                getLocalCacheEntityReturnForRescueEvent = localCache.copy(
                    cachedObjectId = rescueEvent.id,
                    section = Section.RESCUE_EVENTS,
                    timestamp = 123L
                ).toEntity()
            )
            checkAllRescueEventsViewmodel.fetchAllRescueEventsStateByPlace(
                rescueEvent.country,
                rescueEvent.city
            )

            runCurrent()

            checkAllRescueEventsViewmodel.allRescueEventsState.test {
                assertEquals(
                    UiState.Success(
                        listOf(
                            UiRescueEvent(
                                rescueEvent, listOf(
                                    nonHumanAnimal,
                                    nonHumanAnimal.copy(id = nonHumanAnimal.id + "second")
                                )
                            )
                        )
                    ), awaitItem()
                )
                ensureAllEventsConsumed()
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given a user requesting all rescue events from cordoba with outdated cache and no image_when the user clicks on the search button_then rescue events are modified in the local repository and displayed`() =
        runTest {
            val checkAllRescueEventsViewmodel = getCheckAllRescueEventsViewmodel(
                getLocalCacheEntityReturnForCountryCity = localCache.copy(
                    cachedObjectId = rescueEvent.country + rescueEvent.city,
                    section = Section.RESCUE_EVENTS,
                    timestamp = 123L
                ).toEntity(),
                getLocalCacheEntityReturnForRescueEvent = localCache.copy(
                    cachedObjectId = rescueEvent.id,
                    section = Section.RESCUE_EVENTS,
                    timestamp = 123L
                ).toEntity(),
                remoteRescueEventsByCountryAndCity = flowOf(
                    listOf(
                        rescueEvent.copy(imageUrl = "").toData()
                    )
                ),
                rescueEventWithAllNonHumanAnimalLocalDataByCountryCityReturn = flowOf(
                    listOf(
                        rescueEventWithAllNeedsAndNonHumanAnimalData.copy(
                            rescueEventEntity = rescueEvent.copy(
                                imageUrl = ""
                            ).toEntity()
                        )
                    )
                )
            )
            checkAllRescueEventsViewmodel.fetchAllRescueEventsStateByPlace(
                rescueEvent.country,
                rescueEvent.city
            )

            runCurrent()

            checkAllRescueEventsViewmodel.allRescueEventsState.test {
                assertEquals(
                    UiState.Success(
                        listOf(
                            UiRescueEvent(
                                rescueEvent.copy(imageUrl = ""), listOf(
                                    nonHumanAnimal,
                                    nonHumanAnimal.copy(id = nonHumanAnimal.id + "second")
                                )
                            )
                        )
                    ),
                    awaitItem()
                )
                ensureAllEventsConsumed()
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given a user requesting all REs from cordoba with outdated cache_when the user clicks on search but the app fails saving REs in the local repo_then REs are not saved in repo but displayed`() =
        runTest {
            val checkAllRescueEventsViewmodel = getCheckAllRescueEventsViewmodel(
                getLocalCacheEntityReturnForCountryCity = localCache.copy(
                    cachedObjectId = rescueEvent.country + rescueEvent.city,
                    section = Section.RESCUE_EVENTS,
                    timestamp = 123L
                ).toEntity(),
                getLocalCacheEntityReturnForRescueEvent = localCache.copy(
                    cachedObjectId = rescueEvent.id,
                    section = Section.RESCUE_EVENTS,
                    timestamp = 123L
                ).toEntity(),
                modifiedRescueEventInLocalRowsUpdatedArg = 0
            )
            checkAllRescueEventsViewmodel.fetchAllRescueEventsStateByPlace(
                rescueEvent.country,
                rescueEvent.city
            )

            runCurrent()

            checkAllRescueEventsViewmodel.allRescueEventsState.test {
                assertEquals(
                    UiState.Success(
                        listOf(
                            UiRescueEvent(
                                rescueEvent, listOf(
                                    nonHumanAnimal,
                                    nonHumanAnimal.copy(id = nonHumanAnimal.id + "second")
                                )
                            )
                        )
                    ), awaitItem()
                )
                ensureAllEventsConsumed()
            }
            verify {
                log.e(
                    "CheckAllMyRescueEventsUtilImpl",
                    "modifyRescueEventInLocalRepo: Error modifying the rescue event ${rescueEvent.id} in local database"
                )
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given a user requesting all REs from cordoba with outdated cache_when the user clicks on the search button but app fails saving REs in the local cache_then REs are not saved in cache but displayed`() =
        runTest {
            val checkAllRescueEventsViewmodel = getCheckAllRescueEventsViewmodel(
                getLocalCacheEntityReturnForCountryCity = localCache.copy(
                    cachedObjectId = rescueEvent.country + rescueEvent.city,
                    section = Section.RESCUE_EVENTS,
                    timestamp = 123L
                ).toEntity(),
                getLocalCacheEntityReturnForRescueEvent = localCache.copy(
                    cachedObjectId = rescueEvent.id,
                    section = Section.RESCUE_EVENTS,
                    timestamp = 123L
                ).toEntity(),
                localCacheUpdatedInLocalDatasourceArg = 0
            )
            checkAllRescueEventsViewmodel.fetchAllRescueEventsStateByPlace(
                rescueEvent.country,
                rescueEvent.city
            )

            runCurrent()

            checkAllRescueEventsViewmodel.allRescueEventsState.test {
                assertEquals(
                    UiState.Success(
                        listOf(
                            UiRescueEvent(
                                rescueEvent, listOf(
                                    nonHumanAnimal,
                                    nonHumanAnimal.copy(id = nonHumanAnimal.id + "second")
                                )
                            )
                        )
                    ), awaitItem()
                )
                ensureAllEventsConsumed()
            }
            verify {
                log.e(
                    "CheckAllMyRescueEventsUtilImpl",
                    "modifyRescueEventInLocalRepo: Error updating ${rescueEvent.id} in local cache in section ${Section.RESCUE_EVENTS}"
                )
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given a user requesting all REs from cordoba with recent cache_when the user clicks on the search button_then REs are retrieved from local cache and displayed`() =
        runTest {
            val checkAllRescueEventsViewmodel = getCheckAllRescueEventsViewmodel()
            checkAllRescueEventsViewmodel.fetchAllRescueEventsStateByPlace(
                rescueEvent.country,
                rescueEvent.city
            )

            runCurrent()

            checkAllRescueEventsViewmodel.allRescueEventsState.test {
                assertEquals(
                    UiState.Success(
                        listOf(
                            UiRescueEvent(
                                rescueEvent, listOf(
                                    nonHumanAnimal,
                                    nonHumanAnimal.copy(id = nonHumanAnimal.id + "second")
                                )
                            )
                        )
                    ), awaitItem()
                )
                ensureAllEventsConsumed()
            }
            verify {
                log.d(
                    "GetDataByManagingObjectLocalCacheTimestamp",
                    "Cache for ${rescueEvent.country + rescueEvent.city} in section ${Section.RESCUE_EVENTS} is up-to-date."
                )
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given a user requesting all REs by location_when the user clicks on the search button_then REs are saved in the local repository and displayed`() =
        runTest {
            val checkAllRescueEventsViewmodel = getCheckAllRescueEventsViewmodel(
                getLocalCacheEntityReturnForLocation = null,
                rescueEventWithAllNonHumanAnimalLocalDataReturn = null
            )
            checkAllRescueEventsViewmodel.fetchAllRescueEventsStateByLocation()

            runCurrent()

            checkAllRescueEventsViewmodel.allRescueEventsState.test {
                assertEquals(
                    UiState.Success(
                        listOf(
                            UiRescueEvent(
                                rescueEvent, listOf(
                                    nonHumanAnimal,
                                    nonHumanAnimal.copy(id = nonHumanAnimal.id + "second")
                                ), 22.1
                            )
                        )
                    ), awaitItem()
                )
                ensureAllEventsConsumed()
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given a user requesting all REs by location_when the user clicks on the search button but there is an error updating the location_then an error is displayed`() =
        runTest {
            val checkAllRescueEventsViewmodel = getCheckAllRescueEventsViewmodel(
                getLocalCacheEntityReturnForLocation = null,
                rescueEventWithAllNonHumanAnimalLocalDataReturn = null,
                locationReturn = Pair(0.0, 0.0)
            )
            checkAllRescueEventsViewmodel.fetchAllRescueEventsStateByLocation()

            runCurrent()

            checkAllRescueEventsViewmodel.allRescueEventsState.test {
                assertTrue { awaitItem() is UiState.Error }
                ensureAllEventsConsumed()
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given a user requesting all rescue events by location with outdated cache_when the user clicks on the search button_then rescue events are modified in the local repository and displayed`() =
        runTest {
            val checkAllRescueEventsViewmodel = getCheckAllRescueEventsViewmodel(
                getLocalCacheEntityReturnForLocation = localCache.copy(
                    cachedObjectId = "${activistLongitude}${activistLatitude}",
                    section = Section.RESCUE_EVENTS,
                    timestamp = 123L
                ).toEntity()
            )
            checkAllRescueEventsViewmodel.fetchAllRescueEventsStateByLocation()

            runCurrent()

            checkAllRescueEventsViewmodel.allRescueEventsState.test {
                assertEquals(
                    UiState.Success(
                        listOf(
                            UiRescueEvent(
                                rescueEvent, listOf(
                                    nonHumanAnimal,
                                    nonHumanAnimal.copy(id = nonHumanAnimal.id + "second")
                                ), 22.1
                            )
                        )
                    ), awaitItem()
                )
                ensureAllEventsConsumed()
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given a user requesting all REs by location with recent cache_when the user clicks on the search button_then REs are retrieved from local cache and displayed`() =
        runTest {
            val checkAllRescueEventsViewmodel = getCheckAllRescueEventsViewmodel()
            checkAllRescueEventsViewmodel.fetchAllRescueEventsStateByLocation()

            runCurrent()

            checkAllRescueEventsViewmodel.allRescueEventsState.test {
                assertEquals(
                    UiState.Success(
                        listOf(
                            UiRescueEvent(
                                rescueEvent, listOf(
                                    nonHumanAnimal,
                                    nonHumanAnimal.copy(id = nonHumanAnimal.id + "second")
                                ), 22.1
                            )
                        )
                    ), awaitItem()
                )
                ensureAllEventsConsumed()
            }
            verify {
                log.d(
                    "GetDataByManagingObjectLocalCacheTimestamp",
                    "Cache for ${activistLongitude}${activistLatitude} in section ${Section.RESCUE_EVENTS} is up-to-date."
                )
            }
        }
}
