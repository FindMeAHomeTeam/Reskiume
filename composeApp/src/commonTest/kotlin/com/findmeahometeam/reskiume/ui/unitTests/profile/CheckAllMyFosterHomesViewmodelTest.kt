package com.findmeahometeam.reskiume.ui.unitTests.profile

import app.cash.turbine.test
import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.authUser
import com.findmeahometeam.reskiume.data.database.entity.LocalCacheEntity
import com.findmeahometeam.reskiume.data.database.entity.fosterHome.FosterHomeWithAllNonHumanAnimalData
import com.findmeahometeam.reskiume.data.remote.response.fosterHome.RemoteFosterHome
import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.fosterHome.FosterHome
import com.findmeahometeam.reskiume.domain.repository.local.LocalCacheRepository
import com.findmeahometeam.reskiume.domain.repository.local.LocalFosterHomeRepository
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteFosterHome.FireStoreRemoteFosterHomeRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.GetAllMyFosterHomesFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.GetAllMyFosterHomesFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.image.GetCompleteImagePathFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.localCache.GetDataByManagingObjectLocalCacheTimestamp
import com.findmeahometeam.reskiume.fosterHome
import com.findmeahometeam.reskiume.fosterHomeWithAllNonHumanAnimalData
import com.findmeahometeam.reskiume.localCache
import com.findmeahometeam.reskiume.nonHumanAnimal
import com.findmeahometeam.reskiume.ui.core.components.UiState
import com.findmeahometeam.reskiume.ui.core.navigation.CheckAllMyFosterHomes
import com.findmeahometeam.reskiume.ui.core.navigation.SaveStateHandleProvider
import com.findmeahometeam.reskiume.ui.profile.checkAllMyFosterHomes.CheckAllMyFosterHomesUtil
import com.findmeahometeam.reskiume.ui.profile.checkAllMyFosterHomes.CheckAllMyFosterHomesViewmodel
import com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal.CheckNonHumanAnimalUtil
import com.findmeahometeam.reskiume.ui.util.ManageImagePath
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class CheckAllMyFosterHomesViewmodelTest : CoroutineTestDispatcher() {

    private val onInsertLocalCacheEntity = Capture.slot<(rowId: Long) -> Unit>()

    private val onModifyLocalCacheEntity = Capture.slot<(rowsUpdated: Int) -> Unit>()

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

    private fun getCheckAllMyFosterHomesViewmodel(
        getLocalCacheEntityReturnForMyFosterHome: LocalCacheEntity? =
            localCache.copy(
                cachedObjectId = fosterHome.ownerId,
                section = Section.FOSTER_HOMES
            ).toEntity(),
        getLocalCacheEntityReturnForFosterHome: LocalCacheEntity? =
            localCache.copy(
                cachedObjectId = fosterHome.id,
                section = Section.FOSTER_HOMES
            ).toEntity(),
        localCacheIdInsertedInLocalDatasourceArg: Long = 1L,
        localCacheUpdatedInLocalDatasourceArg: Int = 1,
        myRemoteFosterHomes: Flow<List<RemoteFosterHome>> = flowOf(listOf(fosterHome.toData())),
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
        myFosterHomeWithAllNonHumanAnimalLocalDataReturn: FosterHomeWithAllNonHumanAnimalData? = fosterHomeWithAllNonHumanAnimalData,
        allMyFosterHomeWithAllNonHumanAnimalLocalDataReturn: Flow<List<FosterHomeWithAllNonHumanAnimalData>> = flowOf(
            listOf(fosterHomeWithAllNonHumanAnimalData)
        ),
        allMyManagedFosterHomesFromLocalReturn: Flow<List<FosterHome>> = flowOf(listOf(fosterHome)),
        allMyModifiedFosterHomesFromLocalReturn: Flow<List<FosterHome>> = flowOf(listOf(fosterHome))
    ): CheckAllMyFosterHomesViewmodel {

        val saveStateHandleProvider: SaveStateHandleProvider = mock {
            every {
                provideObjectRoute<CheckAllMyFosterHomes>(any(), any())
            } returns CheckAllMyFosterHomes(user.uid)
        }

        val localCacheRepository: LocalCacheRepository = mock {

            everySuspend {
                getLocalCacheEntity(
                    user.uid,
                    Section.FOSTER_HOMES
                )
            } returns getLocalCacheEntityReturnForMyFosterHome

            everySuspend {
                getLocalCacheEntity(
                    fosterHome.id,
                    Section.FOSTER_HOMES
                )
            } returns getLocalCacheEntityReturnForFosterHome

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
                getAllMyRemoteFosterHomes(
                    fosterHome.ownerId
                )
            } returns myRemoteFosterHomes
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
            } returns myFosterHomeWithAllNonHumanAnimalLocalDataReturn

            every {
                getAllMyFosterHomes(fosterHome.ownerId)
            } returns allMyFosterHomeWithAllNonHumanAnimalLocalDataReturn
        }

        val checkAllMyFosterHomesUtil: CheckAllMyFosterHomesUtil = mock {

            every {
                downloadImageAndManageFosterHomesInLocalRepositoryFromFlow(
                    any(),
                    any(),
                    user.uid
                )
            } returns allMyManagedFosterHomesFromLocalReturn

            every {
                downloadImageAndModifyFosterHomesInLocalRepositoryFromFlow(
                    any(),
                    any(),
                    user.uid
                )
            } returns allMyModifiedFosterHomesFromLocalReturn
        }

        val manageImagePath: ManageImagePath = mock {

            every { getCompleteImagePath(nonHumanAnimal.imageUrl) } returns nonHumanAnimal.imageUrl

            every { getCompleteImagePath(fosterHome.imageUrl) } returns fosterHome.imageUrl
        }

        val getDataByManagingObjectLocalCacheTimestamp =
            GetDataByManagingObjectLocalCacheTimestamp(localCacheRepository, log, konnectivity)

        val getAllMyFosterHomesFromRemoteRepository =
            GetAllMyFosterHomesFromRemoteRepository(
                fireStoreRemoteFosterHomeRepository,
                checkNonHumanAnimalUtil
            )

        val getCompleteImagePathFromLocalDataSource =
            GetCompleteImagePathFromLocalDataSource(manageImagePath)

        val getAllMyFosterHomesFromLocalRepository =
            GetAllMyFosterHomesFromLocalRepository(
                localFosterHomeRepository,
                checkNonHumanAnimalUtil
            )

        return CheckAllMyFosterHomesViewmodel(
            saveStateHandleProvider,
            getDataByManagingObjectLocalCacheTimestamp,
            getAllMyFosterHomesFromRemoteRepository,
            checkAllMyFosterHomesUtil,
            getAllMyFosterHomesFromLocalRepository,
            getCompleteImagePathFromLocalDataSource
        )
    }

    @Test
    fun `given a user requesting their foster homes_when the user enters on their foster homes section_then foster homes are saved in the local repository and displayed`() =
        runTest {
            val checkAllMyFosterHomesViewmodel = getCheckAllMyFosterHomesViewmodel(
                getLocalCacheEntityReturnForMyFosterHome = null,
                getLocalCacheEntityReturnForFosterHome = null,
            )

            checkAllMyFosterHomesViewmodel.fetchAllMyFosterHomes().test {
                assertEquals(UiState.Success(listOf(fosterHome)), awaitItem())
                awaitComplete()
            }
        }

    @Test
    fun `given a user requesting their foster homes with outdated cache_when the user enters on their foster homes section_then FHs are modified in the local repo and displayed`() =
        runTest {
            val checkAllMyFosterHomesViewmodel = getCheckAllMyFosterHomesViewmodel(
                getLocalCacheEntityReturnForMyFosterHome =
                    localCache.copy(
                        cachedObjectId = fosterHome.ownerId,
                        section = Section.FOSTER_HOMES,
                        timestamp = 123L
                    ).toEntity(),
                getLocalCacheEntityReturnForFosterHome =
                    localCache.copy(
                        cachedObjectId = fosterHome.id,
                        section = Section.FOSTER_HOMES,
                        timestamp = 123L
                    ).toEntity()
            )

            checkAllMyFosterHomesViewmodel.fetchAllMyFosterHomes().test {
                assertEquals(
                    UiState.Success(listOf(fosterHome)),
                    awaitItem()
                )
                awaitComplete()
            }
        }

    @Test
    fun `given a user requesting their foster homes with recent cache_when the user enters on their foster homes section_then FHs are retrieved from local cache and displayed`() =
        runTest {
            val checkAllMyFosterHomesViewmodel = getCheckAllMyFosterHomesViewmodel()

            checkAllMyFosterHomesViewmodel.fetchAllMyFosterHomes().test {
                assertEquals(
                    UiState.Success(listOf(fosterHome)),
                    awaitItem()
                )
                awaitComplete()
            }
        }
}
