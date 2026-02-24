package com.findmeahometeam.reskiume.ui.unitTests.fosterHomes

import app.cash.turbine.test
import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.authUser
import com.findmeahometeam.reskiume.data.database.entity.LocalCacheEntity
import com.findmeahometeam.reskiume.data.database.entity.fosterHome.FosterHomeWithAllNonHumanAnimalData
import com.findmeahometeam.reskiume.data.remote.response.AuthUser
import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.remote.response.fosterHome.RemoteFosterHome
import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.AdoptionState
import com.findmeahometeam.reskiume.domain.model.Gender
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimalType
import com.findmeahometeam.reskiume.domain.model.fosterHome.AcceptedNonHumanAnimalForFosterHome
import com.findmeahometeam.reskiume.domain.model.fosterHome.ResidentNonHumanAnimalForFosterHome
import com.findmeahometeam.reskiume.domain.repository.local.LocalCacheRepository
import com.findmeahometeam.reskiume.domain.repository.local.LocalFosterHomeRepository
import com.findmeahometeam.reskiume.domain.repository.local.LocalNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteNonHumanAnimal.RealtimeDatabaseRemoteNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteFosterHome.FireStoreRemoteFosterHomeRepository
import com.findmeahometeam.reskiume.domain.repository.remote.storage.StorageRepository
import com.findmeahometeam.reskiume.domain.usecases.authUser.ObserveAuthStateInAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.GetFosterHomeFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.GetFosterHomeFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.ModifyFosterHomeInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.ModifyFosterHomeInRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.image.DeleteImageFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.DeleteImageFromRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.GetImagePathForFileNameFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.UploadImageToRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.localCache.ModifyCacheInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.GetAllNonHumanAnimalsFromLocalRepository
import com.findmeahometeam.reskiume.fosterHome
import com.findmeahometeam.reskiume.fosterHomeWithAllNonHumanAnimalData
import com.findmeahometeam.reskiume.localCache
import com.findmeahometeam.reskiume.nonHumanAnimal
import com.findmeahometeam.reskiume.ui.core.components.UiState
import com.findmeahometeam.reskiume.ui.core.navigation.ModifyFosterHome
import com.findmeahometeam.reskiume.ui.core.navigation.SaveStateHandleProvider
import com.findmeahometeam.reskiume.ui.fosterHomes.checkAllFosterHomes.UiFosterHome
import com.findmeahometeam.reskiume.ui.fosterHomes.modifyFosterHome.DeleteFosterHomeUtil
import com.findmeahometeam.reskiume.ui.fosterHomes.modifyFosterHome.ModifyFosterHomeViewmodel
import com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal.CheckNonHumanAnimalUtil
import com.findmeahometeam.reskiume.ui.profile.modifyNonHumanAnimal.DeleteNonHumanAnimalUtil
import com.findmeahometeam.reskiume.ui.util.ManageImagePath
import com.findmeahometeam.reskiume.user
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ModifyFosterHomeViewmodelTest : CoroutineTestDispatcher() {

    private val onModifyLocalCacheEntity = Capture.slot<(rowsUpdated: Int) -> Unit>()

    private val onDeleteLocalCacheEntity = Capture.slot<(rowsDeleted: Int) -> Unit>()

    private val onImageDeletedFromRemoteForFosterHome = Capture.slot<(isDeleted: Boolean) -> Unit>()

    private val onImageDeletedFromLocalForFosterHome = Capture.slot<(isDeleted: Boolean) -> Unit>()

    private val onUploadImageToRemoteForFosterHome = Capture.slot<(imagePath: String) -> Unit>()

    private val onModifyRemoteFosterHome = Capture.slot<(DatabaseResult) -> Unit>()

    private val onModifyRemoteNonHumanAnimal = Capture.slot<(DatabaseResult) -> Unit>()

    private val onInsertAcceptedNonHumanAnimalForFosterHome = Capture.slot<(rowId: Long) -> Unit>()

    private val onInsertAcceptedSecondNonHumanAnimalForFosterHome =
        Capture.slot<(rowId: Long) -> Unit>()

    private val onInsertAcceptedThirdNonHumanAnimalForFosterHome =
        Capture.slot<(rowId: Long) -> Unit>()

    private val onInsertResidentNonHumanAnimalIdForFosterHome =
        Capture.slot<(rowId: Long) -> Unit>()

    private val onInsertSecondResidentNonHumanAnimalIdForFosterHome =
        Capture.slot<(rowId: Long) -> Unit>()

    private val onModifyFosterHome = Capture.slot<suspend (rowsUpdated: Int) -> Unit>()

    private val onModifyFosterHomeWithoutImage = Capture.slot<suspend (rowsUpdated: Int) -> Unit>()

    private val onModifyAcceptedNonHumanAnimalForFosterHome =
        Capture.slot<(rowsUpdated: Int) -> Unit>()

    private val onModifyAcceptedSecondNonHumanAnimalForFosterHome =
        Capture.slot<(rowsUpdated: Int) -> Unit>()

    private val onModifyResidentNonHumanAnimalIdForFosterHome =
        Capture.slot<(rowsUpdated: Int) -> Unit>()

    private val modifyNonHumanAnimalInLocalRepository = Capture.slot<(rowsUpdated: Int) -> Unit>()

    private val onCompletedDeleteFosterHome = Capture.slot<() -> Unit>()

    private val log: Log = mock {
        every { d(any(), any()) } calls { println(it) }
        every { e(any(), any()) } calls { println(it) }
    }

    private fun getModifyFosterHomeViewmodel(
        authStateReturn: AuthUser? = authUser,
        getLocalCacheEntityReturnForFosterHome: LocalCacheEntity? =
            localCache.copy(
                cachedObjectId = fosterHome.id,
                section = Section.FOSTER_HOMES
            ).toEntity(),
        localCacheUpdatedInLocalDatasourceArg: Int = 1,
        numberOfRowsDeletedInLocalDatasourceArg: Int = 1,
        remoteFosterHomeReturn: Flow<RemoteFosterHome> = flowOf(fosterHome.toData()),
        databaseResultOfModifyingFosterHomesInRemoteRepositoryArg: DatabaseResult = DatabaseResult.Success,
        databaseResultOfModifyingNonHumanAnimalInRemoteRepositoryArg: DatabaseResult = DatabaseResult.Success,
        isRemoteImageDeletedFlagForFosterHome: Boolean = true,
        isLocalImageDeletedFlagForFosterHome: Boolean = true,
        imagePathToUploadToRemoteForFosterHome: String = fosterHome.imageUrl,
        insertedAcceptedNonHumanAnimalForFosterHomeInLocalRowIdArg: Long = 1L,
        insertedAcceptedSecondNonHumanAnimalForFosterHomeInLocalRowIdArg: Long = 1L,
        insertedResidentNonHumanAnimalIdForFosterHomeInLocalRowIdArg: Long = 1L,
        modifiedFosterHomeInLocalRowsUpdatedArg: Int = 1,
        modifiedFosterHomeWithoutImageInLocalRowsUpdatedArg: Int = 1,
        modifiedAcceptedNonHumanAnimalForFosterHomeInLocalRowsUpdatedArg: Int = 1,
        modifiedAcceptedSecondNonHumanAnimalForFosterHomeInLocalRowsUpdatedArg: Int = 1,
        modifiedResidentNonHumanAnimalIdForFosterHomeInLocalRowsUpdatedArg: Int = 1,
        fosterHomeWithAllNonHumanAnimalLocalDataReturn: FosterHomeWithAllNonHumanAnimalData? = fosterHomeWithAllNonHumanAnimalData,
        numberOfNonHumanAnimalsUpdatedInLocalRepositoryArg: Int = 1
    ): ModifyFosterHomeViewmodel {

        val saveStateHandleProvider: SaveStateHandleProvider = mock {
            every {
                provideObjectRoute<ModifyFosterHome>(any(), any())
            } returns ModifyFosterHome(fosterHome.id)
        }

        val authRepository: AuthRepository = mock {
            everySuspend { authState } returns (flowOf(authStateReturn))
        }

        val localCacheRepository: LocalCacheRepository = mock {

            everySuspend {
                getLocalCacheEntity(
                    fosterHome.id,
                    Section.FOSTER_HOMES
                )
            } returns getLocalCacheEntityReturnForFosterHome

            everySuspend {
                modifyLocalCacheEntity(
                    any(),
                    capture(onModifyLocalCacheEntity)
                )
            } calls { onModifyLocalCacheEntity.get().invoke(localCacheUpdatedInLocalDatasourceArg) }

            everySuspend {
                deleteLocalCacheEntity(
                    fosterHome.id,
                    capture(onDeleteLocalCacheEntity)
                )
            } calls {
                onDeleteLocalCacheEntity.get().invoke(numberOfRowsDeletedInLocalDatasourceArg)
            }
        }

        val fireStoreRemoteFosterHomeRepository: FireStoreRemoteFosterHomeRepository = mock {

            everySuspend {
                getRemoteFosterHome(fosterHome.id)
            } returns remoteFosterHomeReturn

            everySuspend {

                val acceptedNonHumanAnimal = AcceptedNonHumanAnimalForFosterHome(
                    acceptedNonHumanAnimalId = 123456,
                    fosterHomeId = fosterHome.id,
                    acceptedNonHumanAnimalType = NonHumanAnimalType.BIRD,
                    acceptedNonHumanAnimalGender = Gender.MALE
                )

                val residentNonHumanAnimal = ResidentNonHumanAnimalForFosterHome(
                    nonHumanAnimalId = nonHumanAnimal.id + "789",
                    caregiverId = nonHumanAnimal.caregiverId,
                    fosterHomeId = fosterHome.id
                )

                val updatedFosterHome = fosterHome.copy(
                    allAcceptedNonHumanAnimals = fosterHome.allAcceptedNonHumanAnimals + acceptedNonHumanAnimal,
                    allResidentNonHumanAnimals = fosterHome.allResidentNonHumanAnimals + residentNonHumanAnimal
                )

                modifyRemoteFosterHome(
                    updatedFosterHome.toData(),
                    capture(onModifyRemoteFosterHome)
                )
            } calls {
                onModifyRemoteFosterHome.get()
                    .invoke(databaseResultOfModifyingFosterHomesInRemoteRepositoryArg)
            }

            everySuspend {

                val acceptedNonHumanAnimal = AcceptedNonHumanAnimalForFosterHome(
                    acceptedNonHumanAnimalId = 123456,
                    fosterHomeId = fosterHome.id,
                    acceptedNonHumanAnimalType = NonHumanAnimalType.BIRD,
                    acceptedNonHumanAnimalGender = Gender.MALE
                )

                val residentNonHumanAnimal = ResidentNonHumanAnimalForFosterHome(
                    nonHumanAnimalId = nonHumanAnimal.id + "789",
                    caregiverId = nonHumanAnimal.caregiverId,
                    fosterHomeId = fosterHome.id
                )

                val updatedFosterHome = fosterHome.copy(
                    allAcceptedNonHumanAnimals = fosterHome.allAcceptedNonHumanAnimals + acceptedNonHumanAnimal,
                    allResidentNonHumanAnimals = fosterHome.allResidentNonHumanAnimals + residentNonHumanAnimal
                )

                modifyRemoteFosterHome(
                    updatedFosterHome.copy(imageUrl = "").toData(),
                    capture(onModifyRemoteFosterHome)
                )
            } calls {
                onModifyRemoteFosterHome.get()
                    .invoke(databaseResultOfModifyingFosterHomesInRemoteRepositoryArg)
            }
        }

        val realtimeDatabaseRemoteNonHumanAnimalRepository: RealtimeDatabaseRemoteNonHumanAnimalRepository =
            mock {
                everySuspend {
                    getRemoteNonHumanAnimal(nonHumanAnimal.id, nonHumanAnimal.caregiverId)
                } returns flowOf(nonHumanAnimal.toData())

                everySuspend {
                    getRemoteNonHumanAnimal(nonHumanAnimal.id + "789", nonHumanAnimal.caregiverId)
                } returns flowOf(nonHumanAnimal.toData())

                everySuspend {
                    modifyRemoteNonHumanAnimal(
                        nonHumanAnimal.copy(
                            adoptionState = AdoptionState.REHOMED,
                            fosterHomeId = fosterHome.id
                        ).toData(),
                        capture(onModifyRemoteNonHumanAnimal)
                    )
                } calls {
                    onModifyRemoteNonHumanAnimal.get()
                        .invoke(databaseResultOfModifyingNonHumanAnimalInRemoteRepositoryArg)
                }

                everySuspend {
                    modifyRemoteNonHumanAnimal(
                        nonHumanAnimal.toData(),
                        capture(onModifyRemoteNonHumanAnimal)
                    )
                } calls {
                    onModifyRemoteNonHumanAnimal.get()
                        .invoke(databaseResultOfModifyingNonHumanAnimalInRemoteRepositoryArg)
                }
            }

        val deleteNonHumanAnimalUtil: DeleteNonHumanAnimalUtil = mock {
            everySuspend {
                deleteNonHumanAnimal(
                    id = nonHumanAnimal.id,
                    caregiverId = nonHumanAnimal.caregiverId,
                    coroutineScope = any(),
                    onlyDeleteOnLocal = false,
                    onError = any(),
                    onComplete = any()
                )
            } returns Unit
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
                    nonHumanAnimal.id + "789",
                    nonHumanAnimal.caregiverId,
                    any()
                )
            } returns flowOf(nonHumanAnimal.copy(id = nonHumanAnimal.id + "789"))
        }

        val storageRepository: StorageRepository = mock {

            everySuspend {
                deleteRemoteImage(
                    user.uid,
                    fosterHome.id,
                    Section.FOSTER_HOMES,
                    capture(onImageDeletedFromRemoteForFosterHome)
                )
            } calls {
                onImageDeletedFromRemoteForFosterHome.get()
                    .invoke(isRemoteImageDeletedFlagForFosterHome)
            }

            every {
                deleteLocalImage(
                    fosterHome.imageUrl,
                    capture(onImageDeletedFromLocalForFosterHome)
                )
            } calls {
                onImageDeletedFromLocalForFosterHome.get()
                    .invoke(isLocalImageDeletedFlagForFosterHome)
            }

            every {
                uploadImage(
                    user.uid,
                    fosterHome.id,
                    Section.FOSTER_HOMES,
                    fosterHome.imageUrl,
                    capture(onUploadImageToRemoteForFosterHome)
                )
            } calls {
                onUploadImageToRemoteForFosterHome.get()
                    .invoke(imagePathToUploadToRemoteForFosterHome)
            }
        }

        val localFosterHomeRepository: LocalFosterHomeRepository = mock {

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
                insertAcceptedNonHumanAnimalForFosterHome(
                    AcceptedNonHumanAnimalForFosterHome(
                        acceptedNonHumanAnimalId = 123456,
                        fosterHomeId = fosterHome.id,
                        acceptedNonHumanAnimalType = NonHumanAnimalType.BIRD,
                        acceptedNonHumanAnimalGender = Gender.MALE
                    ).toEntity(),
                    capture(onInsertAcceptedThirdNonHumanAnimalForFosterHome)
                )
            } calls {
                onInsertAcceptedThirdNonHumanAnimalForFosterHome.get()
                    .invoke(insertedAcceptedSecondNonHumanAnimalForFosterHomeInLocalRowIdArg)
            }

            everySuspend {
                insertResidentNonHumanAnimalIdForFosterHome(
                    fosterHome.allResidentNonHumanAnimals[0].toEntity(),
                    capture(onInsertResidentNonHumanAnimalIdForFosterHome)
                )
            } calls {
                onInsertResidentNonHumanAnimalIdForFosterHome.get()
                    .invoke(insertedResidentNonHumanAnimalIdForFosterHomeInLocalRowIdArg)
            }

            everySuspend {
                insertResidentNonHumanAnimalIdForFosterHome(
                    ResidentNonHumanAnimalForFosterHome(
                        nonHumanAnimalId = nonHumanAnimal.id + "789",
                        caregiverId = nonHumanAnimal.caregiverId,
                        fosterHomeId = fosterHome.id
                    ).toEntity(),
                    capture(onInsertSecondResidentNonHumanAnimalIdForFosterHome)
                )
            } calls {
                onInsertSecondResidentNonHumanAnimalIdForFosterHome.get()
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
                    fosterHome.allResidentNonHumanAnimals[0].toEntity(),
                    capture(onModifyResidentNonHumanAnimalIdForFosterHome)
                )
            } calls {
                onModifyResidentNonHumanAnimalIdForFosterHome.get()
                    .invoke(modifiedResidentNonHumanAnimalIdForFosterHomeInLocalRowsUpdatedArg)
            }

            everySuspend {
                getFosterHome(fosterHome.id)
            } returns fosterHomeWithAllNonHumanAnimalLocalDataReturn
        }

        val localNonHumanAnimalRepository: LocalNonHumanAnimalRepository = mock {

            every {
                getAllNonHumanAnimals()
            } returns flowOf(listOf(nonHumanAnimal.toEntity()))

            everySuspend {
                getNonHumanAnimal(nonHumanAnimal.id)
            } returns nonHumanAnimal.toEntity()

            everySuspend {
                modifyNonHumanAnimal(
                    nonHumanAnimal.copy(
                        id = nonHumanAnimal.id + "789",
                        adoptionState = AdoptionState.REHOMED,
                        fosterHomeId = fosterHome.id
                    ).toEntity(),
                    capture(modifyNonHumanAnimalInLocalRepository)
                )
            } calls {
                modifyNonHumanAnimalInLocalRepository.get()
                    .invoke(numberOfNonHumanAnimalsUpdatedInLocalRepositoryArg)
            }

            everySuspend {
                modifyNonHumanAnimal(
                    nonHumanAnimal.toEntity(),
                    capture(modifyNonHumanAnimalInLocalRepository)
                )
            } calls {
                modifyNonHumanAnimalInLocalRepository.get()
                    .invoke(numberOfNonHumanAnimalsUpdatedInLocalRepositoryArg)
            }
        }

        val manageImagePath: ManageImagePath = mock {

            every { getImagePathForFileName(nonHumanAnimal.imageUrl) } returns nonHumanAnimal.imageUrl

            every { getFileNameFromLocalImagePath(nonHumanAnimal.imageUrl) } returns nonHumanAnimal.imageUrl

            every { getImagePathForFileName(fosterHome.imageUrl) } returns fosterHome.imageUrl

            every { getFileNameFromLocalImagePath(fosterHome.imageUrl) } returns fosterHome.imageUrl

            every { getFileNameFromLocalImagePath("") } returns ""
        }

        val deleteFosterHomeUtil: DeleteFosterHomeUtil = mock {
            every {
                deleteFosterHome(
                    id = fosterHome.id,
                    ownerId = fosterHome.ownerId,
                    coroutineScope = any(),
                    onError = any(),
                    onComplete = capture(onCompletedDeleteFosterHome)
                )
            } calls { onCompletedDeleteFosterHome.get().invoke() }
        }

        val getFosterHomeFromLocalRepository =
            GetFosterHomeFromLocalRepository(localFosterHomeRepository)

        val getImagePathForFileNameFromLocalDataSource =
            GetImagePathForFileNameFromLocalDataSource(manageImagePath)

        val getAllNonHumanAnimalsFromLocalRepository =
            GetAllNonHumanAnimalsFromLocalRepository(localNonHumanAnimalRepository)

        val getFosterHomeFromRemoteRepository =
            GetFosterHomeFromRemoteRepository(fireStoreRemoteFosterHomeRepository)

        val deleteImageFromRemoteDataSource =
            DeleteImageFromRemoteDataSource(storageRepository)

        val deleteImageFromLocalDataSource =
            DeleteImageFromLocalDataSource(storageRepository)

        val uploadImageToRemoteDataSource =
            UploadImageToRemoteDataSource(storageRepository)

        val observeAuthStateInAuthDataSource =
            ObserveAuthStateInAuthDataSource(authRepository)

        val modifyFosterHomeInRemoteRepository =
            ModifyFosterHomeInRemoteRepository(
                observeAuthStateInAuthDataSource,
                fireStoreRemoteFosterHomeRepository,
                realtimeDatabaseRemoteNonHumanAnimalRepository,
                deleteNonHumanAnimalUtil,
                log
            )

        val modifyFosterHomeInLocalRepository =
            ModifyFosterHomeInLocalRepository(
                manageImagePath,
                localFosterHomeRepository,
                localNonHumanAnimalRepository,
                checkNonHumanAnimalUtil,
                authRepository,
                log
            )

        val modifyCacheInLocalRepository =
            ModifyCacheInLocalRepository(localCacheRepository)

        return ModifyFosterHomeViewmodel(
            saveStateHandleProvider,
            getFosterHomeFromLocalRepository,
            getImagePathForFileNameFromLocalDataSource,
            checkNonHumanAnimalUtil,
            getAllNonHumanAnimalsFromLocalRepository,
            getFosterHomeFromRemoteRepository,
            deleteImageFromRemoteDataSource,
            deleteImageFromLocalDataSource,
            uploadImageToRemoteDataSource,
            modifyFosterHomeInRemoteRepository,
            modifyFosterHomeInLocalRepository,
            modifyCacheInLocalRepository,
            deleteFosterHomeUtil,
            log
        )
    }

    @Test
    fun `given my foster home_when I click to modify it_then foster home is retrieved`() =
        runTest {
            getModifyFosterHomeViewmodel().fosterHomeFlow.test {
                assertEquals(
                    UiState.Success(
                        UiFosterHome(
                            fosterHome,
                            listOf(nonHumanAnimal)
                        )
                    ),
                    awaitItem()
                )
                awaitComplete()
            }
        }

    @Test
    fun `given my foster home to modify_when I want to add residents_then foster home list available non human animals`() =
        runTest {
            getModifyFosterHomeViewmodel().allAvailableNonHumanAnimalsLookingForAdoptionFlow.test {
                assertEquals(listOf(nonHumanAnimal), awaitItem())
                awaitComplete()
            }
        }

    @Test
    fun `given my foster home to modify_when I add accepted and resident non human animals_then I click to update my foster home`() =
        runTest {
            val modifyFosterHomeViewmodel = getModifyFosterHomeViewmodel()

            val acceptedNonHumanAnimal = AcceptedNonHumanAnimalForFosterHome(
                acceptedNonHumanAnimalId = 123456,
                fosterHomeId = fosterHome.id,
                acceptedNonHumanAnimalType = NonHumanAnimalType.BIRD,
                acceptedNonHumanAnimalGender = Gender.MALE
            )

            val residentNonHumanAnimal = ResidentNonHumanAnimalForFosterHome(
                nonHumanAnimalId = nonHumanAnimal.id + "789",
                caregiverId = nonHumanAnimal.caregiverId,
                fosterHomeId = fosterHome.id
            )

            val updatedFosterHome = fosterHome.copy(
                allAcceptedNonHumanAnimals = fosterHome.allAcceptedNonHumanAnimals + acceptedNonHumanAnimal,
                allResidentNonHumanAnimals = fosterHome.allResidentNonHumanAnimals + residentNonHumanAnimal
            )

            modifyFosterHomeViewmodel.saveFosterHomeChanges(true, updatedFosterHome)

            modifyFosterHomeViewmodel.manageChangesUiState.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Success }
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `given my foster home to modify_when I click to update my foster home but fails deleting the remote foster home image_then the app retrieves an error`() =
        runTest {
            val modifyFosterHomeViewmodel = getModifyFosterHomeViewmodel(
                isRemoteImageDeletedFlagForFosterHome = false
            )

            modifyFosterHomeViewmodel.saveFosterHomeChanges(true, fosterHome)

            modifyFosterHomeViewmodel.manageChangesUiState.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Error }
                ensureAllEventsConsumed()
            }
            verify {
                log.e(
                    "ModifyFosterHomeViewModel",
                    "deleteCurrentImageFromRemoteDataSource: failed to delete the image from the foster home ${fosterHome.id} in remote data source"
                )
            }
        }

    @Test
    fun `given my foster home to modify_when click to update my foster home but fails retrieving the local foster home_then the app retrieves an error`() =
        runTest {
            val modifyFosterHomeViewmodel = getModifyFosterHomeViewmodel(
                fosterHomeWithAllNonHumanAnimalLocalDataReturn = null
            )

            modifyFosterHomeViewmodel.saveFosterHomeChanges(true, fosterHome)

            modifyFosterHomeViewmodel.manageChangesUiState.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Error }
                ensureAllEventsConsumed()
            }
            verify {
                log.e(
                    "ModifyFosterHomeViewModel",
                    "deleteCurrentImageFromLocalDataSource: failed to delete the image from the foster home ${fosterHome.id} in the local data source because the local foster home does not exist!"
                )
            }
        }

    @Test
    fun `given my foster home to modify_when I click to update my foster home but fails deleting the local foster home image_then the app retrieves an error`() =
        runTest {
            val modifyFosterHomeViewmodel = getModifyFosterHomeViewmodel(
                isLocalImageDeletedFlagForFosterHome = false
            )

            modifyFosterHomeViewmodel.saveFosterHomeChanges(true, fosterHome)

            modifyFosterHomeViewmodel.manageChangesUiState.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Error }
                ensureAllEventsConsumed()
            }
            verify {
                log.e(
                    "ModifyFosterHomeViewModel",
                    "deleteCurrentImageFromLocalDataSource: failed to delete the image from the foster home ${fosterHome.id} in the local data source"
                )
            }
        }

    @Test
    fun `given my foster home to modify_when I click to update my foster home but there is no foster home image_then the foster home is updated`() =
        runTest {
            val modifyFosterHomeViewmodel = getModifyFosterHomeViewmodel()

            val acceptedNonHumanAnimal = AcceptedNonHumanAnimalForFosterHome(
                acceptedNonHumanAnimalId = 123456,
                fosterHomeId = fosterHome.id,
                acceptedNonHumanAnimalType = NonHumanAnimalType.BIRD,
                acceptedNonHumanAnimalGender = Gender.MALE
            )

            val residentNonHumanAnimal = ResidentNonHumanAnimalForFosterHome(
                nonHumanAnimalId = nonHumanAnimal.id + "789",
                caregiverId = nonHumanAnimal.caregiverId,
                fosterHomeId = fosterHome.id
            )

            val updatedFosterHome = fosterHome.copy(
                imageUrl = "",
                allAcceptedNonHumanAnimals = fosterHome.allAcceptedNonHumanAnimals + acceptedNonHumanAnimal,
                allResidentNonHumanAnimals = fosterHome.allResidentNonHumanAnimals + residentNonHumanAnimal
            )

            modifyFosterHomeViewmodel.saveFosterHomeChanges(true, updatedFosterHome)

            modifyFosterHomeViewmodel.manageChangesUiState.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Success }
                ensureAllEventsConsumed()
            }
            verify {
                log.d(
                    "ModifyFosterHomeViewModel",
                    "uploadNewImageToRemoteDataSource: the download URI from the foster home ${updatedFosterHome.id} is blank"
                )
            }
        }

    @Test
    fun `given my foster home to modify_when I click to update my foster home but fails modifying the foster home in the remote repo_then the app retrieves an error`() =
        runTest {
            val modifyFosterHomeViewmodel = getModifyFosterHomeViewmodel(
                databaseResultOfModifyingFosterHomesInRemoteRepositoryArg = DatabaseResult.Error()
            )

            val acceptedNonHumanAnimal = AcceptedNonHumanAnimalForFosterHome(
                acceptedNonHumanAnimalId = 123456,
                fosterHomeId = fosterHome.id,
                acceptedNonHumanAnimalType = NonHumanAnimalType.BIRD,
                acceptedNonHumanAnimalGender = Gender.MALE
            )

            val residentNonHumanAnimal = ResidentNonHumanAnimalForFosterHome(
                nonHumanAnimalId = nonHumanAnimal.id + "789",
                caregiverId = nonHumanAnimal.caregiverId,
                fosterHomeId = fosterHome.id
            )

            val updatedFosterHome = fosterHome.copy(
                allAcceptedNonHumanAnimals = fosterHome.allAcceptedNonHumanAnimals + acceptedNonHumanAnimal,
                allResidentNonHumanAnimals = fosterHome.allResidentNonHumanAnimals + residentNonHumanAnimal
            )

            modifyFosterHomeViewmodel.saveFosterHomeChanges(false, updatedFosterHome)

            modifyFosterHomeViewmodel.manageChangesUiState.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Error }
                ensureAllEventsConsumed()
            }
            verify {
                log.e(
                    "ModifyFosterHomeViewModel",
                    "modifyFosterHomeInRemoteDataSource: failed to update the foster home ${updatedFosterHome.id} in remote data source"
                )
            }
        }

    @Test
    fun `given my foster home to modify_when I click to update my foster home but fails modifying the foster home in the local repo_then the app retrieves an error`() =
        runTest {
            val modifyFosterHomeViewmodel = getModifyFosterHomeViewmodel(
                modifiedFosterHomeInLocalRowsUpdatedArg = 0
            )

            val acceptedNonHumanAnimal = AcceptedNonHumanAnimalForFosterHome(
                acceptedNonHumanAnimalId = 123456,
                fosterHomeId = fosterHome.id,
                acceptedNonHumanAnimalType = NonHumanAnimalType.BIRD,
                acceptedNonHumanAnimalGender = Gender.MALE
            )

            val residentNonHumanAnimal = ResidentNonHumanAnimalForFosterHome(
                nonHumanAnimalId = nonHumanAnimal.id + "789",
                caregiverId = nonHumanAnimal.caregiverId,
                fosterHomeId = fosterHome.id
            )

            val updatedFosterHome = fosterHome.copy(
                allAcceptedNonHumanAnimals = fosterHome.allAcceptedNonHumanAnimals + acceptedNonHumanAnimal,
                allResidentNonHumanAnimals = fosterHome.allResidentNonHumanAnimals + residentNonHumanAnimal
            )

            modifyFosterHomeViewmodel.saveFosterHomeChanges(false, updatedFosterHome)

            modifyFosterHomeViewmodel.manageChangesUiState.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Error }
                ensureAllEventsConsumed()
            }
            verify {
                log.e(
                    "ModifyFosterHomeViewModel",
                    "modifyFosterHomeInLocalDataSource: failed to update the foster home ${updatedFosterHome.id} in the local data source"
                )
            }
        }

    @Test
    fun `given my foster home to modify_when I click to update my foster home but fails modifying the foster home cache_then the foster home is updated`() =
        runTest {
            val modifyFosterHomeViewmodel = getModifyFosterHomeViewmodel(
                localCacheUpdatedInLocalDatasourceArg = 0
            )

            val acceptedNonHumanAnimal = AcceptedNonHumanAnimalForFosterHome(
                acceptedNonHumanAnimalId = 123456,
                fosterHomeId = fosterHome.id,
                acceptedNonHumanAnimalType = NonHumanAnimalType.BIRD,
                acceptedNonHumanAnimalGender = Gender.MALE
            )

            val residentNonHumanAnimal = ResidentNonHumanAnimalForFosterHome(
                nonHumanAnimalId = nonHumanAnimal.id + "789",
                caregiverId = nonHumanAnimal.caregiverId,
                fosterHomeId = fosterHome.id
            )

            val updatedFosterHome = fosterHome.copy(
                allAcceptedNonHumanAnimals = fosterHome.allAcceptedNonHumanAnimals + acceptedNonHumanAnimal,
                allResidentNonHumanAnimals = fosterHome.allResidentNonHumanAnimals + residentNonHumanAnimal
            )

            modifyFosterHomeViewmodel.saveFosterHomeChanges(false, updatedFosterHome)

            modifyFosterHomeViewmodel.manageChangesUiState.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Success }
                ensureAllEventsConsumed()
            }
            verify {
                log.e(
                    "ModifyFosterHomeViewModel",
                    "modifyCacheForFosterHomeInLocalDataSource: Error updating ${fosterHome.id} in local cache in section ${Section.FOSTER_HOMES}"
                )
            }
        }

    @Test
    fun `given my foster home to modify_when I click to delete my foster home_then the foster home is deleted`() =
        runTest {
            val modifyFosterHomeViewmodel = getModifyFosterHomeViewmodel()

            modifyFosterHomeViewmodel.deleteFosterHome(fosterHome.id, fosterHome.ownerId)

            modifyFosterHomeViewmodel.manageChangesUiState.test {
                assertTrue { awaitItem() is UiState.Success }
                ensureAllEventsConsumed()
            }
        }
}
