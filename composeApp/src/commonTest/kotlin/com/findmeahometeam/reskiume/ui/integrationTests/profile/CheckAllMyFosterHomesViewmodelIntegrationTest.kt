package com.findmeahometeam.reskiume.ui.integrationTests.profile

import app.cash.turbine.test
import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.repository.local.LocalCacheRepository
import com.findmeahometeam.reskiume.domain.repository.local.LocalFosterHomeRepository
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteFosterHome.FireStoreRemoteFosterHomeRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.GetAllMyFosterHomesFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.GetAllMyFosterHomesFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.image.GetImagePathForFileNameFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.localCache.GetDataByManagingObjectLocalCacheTimestamp
import com.findmeahometeam.reskiume.fosterHome
import com.findmeahometeam.reskiume.fosterHomeWithAllNonHumanAnimalData
import com.findmeahometeam.reskiume.localCache
import com.findmeahometeam.reskiume.ui.core.components.UiState
import com.findmeahometeam.reskiume.ui.core.navigation.CheckAllMyFosterHomes
import com.findmeahometeam.reskiume.ui.core.navigation.SaveStateHandleProvider
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeCheckAllMyFosterHomesUtil
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeCheckNonHumanAnimalUtil
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeFireStoreRemoteFosterHomeRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeKonnectivity
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeLocalCacheRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeLocalFosterHomeRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeLog
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeManageImagePath
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeSaveStateHandleProvider
import com.findmeahometeam.reskiume.ui.profile.checkAllMyFosterHomes.CheckAllMyFosterHomesUtil
import com.findmeahometeam.reskiume.ui.profile.checkAllMyFosterHomes.CheckAllMyFosterHomesViewmodel
import com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal.CheckNonHumanAnimalUtil
import com.findmeahometeam.reskiume.ui.util.ManageImagePath
import com.findmeahometeam.reskiume.user
import com.plusmobileapps.konnectivity.Konnectivity
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class CheckAllMyFosterHomesViewmodelIntegrationTest : CoroutineTestDispatcher() {

    private fun getCheckAllMyFosterHomesViewmodel(
        saveStateHandleProvider: SaveStateHandleProvider = FakeSaveStateHandleProvider(CheckAllMyFosterHomes(user.uid)),
        localCacheRepository: LocalCacheRepository = FakeLocalCacheRepository(),
        log: Log = FakeLog(),
        konnectivity: Konnectivity = FakeKonnectivity(),
        fireStoreRemoteFosterHomeRepository: FireStoreRemoteFosterHomeRepository = FakeFireStoreRemoteFosterHomeRepository(),
        checkNonHumanAnimalUtil: CheckNonHumanAnimalUtil = FakeCheckNonHumanAnimalUtil(),
        manageImagePath: ManageImagePath = FakeManageImagePath(),
        localFosterHomeRepository: LocalFosterHomeRepository = FakeLocalFosterHomeRepository(),
        checkAllMyFosterHomesUtil: CheckAllMyFosterHomesUtil = FakeCheckAllMyFosterHomesUtil()
    ): CheckAllMyFosterHomesViewmodel {

        val getDataByManagingObjectLocalCacheTimestamp =
            GetDataByManagingObjectLocalCacheTimestamp(localCacheRepository, log, konnectivity)

        val getAllMyFosterHomesFromRemoteRepository =
            GetAllMyFosterHomesFromRemoteRepository(
                fireStoreRemoteFosterHomeRepository,
                checkNonHumanAnimalUtil
            )

        val getImagePathForFileNameFromLocalDataSource =
            GetImagePathForFileNameFromLocalDataSource(manageImagePath)

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
            getImagePathForFileNameFromLocalDataSource
        )
    }

    @Test
    fun `given a user requesting their foster homes_when the user enters on their foster homes section_then foster homes are saved in the local repository and displayed`() =
        runTest {
            val checkAllMyFosterHomesViewmodel = getCheckAllMyFosterHomesViewmodel(
                fireStoreRemoteFosterHomeRepository = FakeFireStoreRemoteFosterHomeRepository(
                    remoteFosterHomeList = mutableListOf(fosterHome.toData())
                )
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
                            cachedObjectId = fosterHome.ownerId,
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
            val checkAllMyFosterHomesViewmodel = getCheckAllMyFosterHomesViewmodel(
                localFosterHomeRepository = FakeLocalFosterHomeRepository(
                    localFosterHomeWithAllNonHumanAnimalDataList = mutableListOf(
                        fosterHomeWithAllNonHumanAnimalData
                    )
                ),
                localCacheRepository = FakeLocalCacheRepository(
                    localCacheList = mutableListOf(
                        localCache.copy(
                            cachedObjectId = fosterHome.ownerId,
                            section = Section.FOSTER_HOMES
                        ).toEntity(),
                        localCache.copy(
                            cachedObjectId = fosterHome.id,
                            section = Section.FOSTER_HOMES
                        ).toEntity()
                    )
                )
            )

            checkAllMyFosterHomesViewmodel.fetchAllMyFosterHomes().test {
                assertEquals(
                    UiState.Success(listOf(fosterHome)),
                    awaitItem()
                )
                awaitComplete()
            }
        }
}
