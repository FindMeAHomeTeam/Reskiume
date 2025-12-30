package com.findmeahometeam.reskiume.ui.unitTests.profile

import app.cash.turbine.test
import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.authUser
import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.repository.local.LocalCacheRepository
import com.findmeahometeam.reskiume.domain.repository.local.LocalNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteNonHumanAnimal.RealtimeDatabaseRemoteNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.repository.remote.storage.StorageRepository
import com.findmeahometeam.reskiume.domain.usecases.authUser.ObserveAuthStateInAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.UploadImageToRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.localCache.InsertCacheInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.InsertNonHumanAnimalInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.InsertNonHumanAnimalInRemoteRepository
import com.findmeahometeam.reskiume.nonHumanAnimal
import com.findmeahometeam.reskiume.ui.core.components.UiState
import com.findmeahometeam.reskiume.ui.profile.createNonHumanAnimal.CreateNonHumanAnimalViewmodel
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
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class CreateNonHumanAnimalViewmodelTest : CoroutineTestDispatcher() {

    private val onInsertLocalCacheEntity = Capture.slot<(rowId: Long) -> Unit>()

    private val onUploadImageToLocal = Capture.slot<(imagePath: String) -> Unit>()

    private val onInsertNonHumanAnimalInLocal = Capture.slot<(rowId: Long) -> Unit>()

    private val onInsertNonHumanAnimalInRemote = Capture.slot<(result: DatabaseResult) -> Unit>()

    private val log: Log = mock {
        every { d(any(), any()) } calls { println(it) }
        every { e(any(), any()) } calls { println(it) }
    }

    @OptIn(ExperimentalTime::class)
    private val nonHumanAnimalId =
        Clock.System.now().epochSeconds.toString() + nonHumanAnimal.caregiverId

    private fun getCreateNonHumanAnimalViewmodel(
        databaseResultAfterCreatingRemoteNonHumanAnimalArg: DatabaseResult = DatabaseResult.Success,
        rowIdAfterCreatingLocalNonHumanAnimalArg: Long = 1L,
        rowIdAfterCreatingLocalCacheForNonHumanAnimalArg: Long = 1L
    ): CreateNonHumanAnimalViewmodel {

        val authRepository: AuthRepository = mock {
            everySuspend { authState } returns (flowOf(authUser))
        }

        val localCacheRepository: LocalCacheRepository = mock {

            everySuspend {
                insertLocalCacheEntity(
                    any(),
                    capture(onInsertLocalCacheEntity)
                )
            } calls {
                onInsertLocalCacheEntity.get()
                    .invoke(rowIdAfterCreatingLocalCacheForNonHumanAnimalArg)
            }
        }

        val realtimeDatabaseRemoteNonHumanAnimalRepository: RealtimeDatabaseRemoteNonHumanAnimalRepository =
            mock {

                everySuspend {
                    insertRemoteNonHumanAnimal(
                        nonHumanAnimal.copy(id = nonHumanAnimalId).toData(),
                        capture(onInsertNonHumanAnimalInRemote)
                    )
                } calls {
                    onInsertNonHumanAnimalInRemote.get()
                        .invoke(databaseResultAfterCreatingRemoteNonHumanAnimalArg)
                }

                everySuspend {
                    insertRemoteNonHumanAnimal(
                        nonHumanAnimal.copy(id = nonHumanAnimalId, imageUrl = "").toData(),
                        capture(onInsertNonHumanAnimalInRemote)
                    )
                } calls {
                    onInsertNonHumanAnimalInRemote.get()
                        .invoke(databaseResultAfterCreatingRemoteNonHumanAnimalArg)
                }
            }

        val storageRepository: StorageRepository = mock {

            every {
                uploadImage(
                    nonHumanAnimal.caregiverId,
                    nonHumanAnimalId,
                    Section.NON_HUMAN_ANIMALS,
                    nonHumanAnimal.imageUrl,
                    capture(onUploadImageToLocal)
                )
            } calls { onUploadImageToLocal.get().invoke(nonHumanAnimal.imageUrl) }
        }

        val localNonHumanAnimalRepository: LocalNonHumanAnimalRepository = mock {
            everySuspend {
                insertNonHumanAnimal(
                    nonHumanAnimal.copy(id = nonHumanAnimalId).toEntity(),
                    capture(onInsertNonHumanAnimalInLocal)
                )
            } calls {
                onInsertNonHumanAnimalInLocal.get()
                    .invoke(rowIdAfterCreatingLocalNonHumanAnimalArg)
            }

            everySuspend {
                insertNonHumanAnimal(
                    nonHumanAnimal.copy(id = nonHumanAnimalId, imageUrl = "").toEntity(),
                    capture(onInsertNonHumanAnimalInLocal)
                )
            } calls {
                onInsertNonHumanAnimalInLocal.get()
                    .invoke(rowIdAfterCreatingLocalNonHumanAnimalArg)
            }
        }

        val observeAuthStateInAuthDataSource =
            ObserveAuthStateInAuthDataSource(authRepository)

        val uploadImageToRemoteDataSource =
            UploadImageToRemoteDataSource(storageRepository)

        val insertNonHumanAnimalInRemoteRepository =
            InsertNonHumanAnimalInRemoteRepository(realtimeDatabaseRemoteNonHumanAnimalRepository)

        val insertNonHumanAnimalInLocalRepository =
            InsertNonHumanAnimalInLocalRepository(localNonHumanAnimalRepository, authRepository)

        val insertCacheInLocalRepository =
            InsertCacheInLocalRepository(localCacheRepository)

        return CreateNonHumanAnimalViewmodel(
            observeAuthStateInAuthDataSource,
            uploadImageToRemoteDataSource,
            insertNonHumanAnimalInRemoteRepository,
            insertNonHumanAnimalInLocalRepository,
            insertCacheInLocalRepository,
            log
        )
    }

    @Test
    fun `given a new non human animal_when the app creates them_then the non human animal is created in the local and remote repositories`() =
        runTest {
            val createNonHumanAnimalViewmodel = getCreateNonHumanAnimalViewmodel()
            createNonHumanAnimalViewmodel.saveNonHumanAnimalChanges(nonHumanAnimal)
            createNonHumanAnimalViewmodel.saveChangesUiState.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Success }
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `given a new non human animal with no image_when the app creates them_then the non human animal is created in the local and remote repositories`() =
        runTest {
            val createNonHumanAnimalViewmodel = getCreateNonHumanAnimalViewmodel()
            createNonHumanAnimalViewmodel.saveNonHumanAnimalChanges(nonHumanAnimal.copy(imageUrl = ""))
            createNonHumanAnimalViewmodel.saveChangesUiState.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Success }
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `given a new non human animal_when the app creates them but the remote repository fails_then the non human animal is not created`() =
        runTest {
            val createNonHumanAnimalViewmodel = getCreateNonHumanAnimalViewmodel(
                databaseResultAfterCreatingRemoteNonHumanAnimalArg = DatabaseResult.Error()
            )
            createNonHumanAnimalViewmodel.saveNonHumanAnimalChanges(nonHumanAnimal)
            createNonHumanAnimalViewmodel.saveChangesUiState.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Error }
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `given a new non human animal_when the app creates them but the local repository fails_then the non human animal is not created in the local repository`() =
        runTest {
            val createNonHumanAnimalViewmodel = getCreateNonHumanAnimalViewmodel(
                rowIdAfterCreatingLocalNonHumanAnimalArg = 0
            )
            createNonHumanAnimalViewmodel.saveNonHumanAnimalChanges(nonHumanAnimal)
            createNonHumanAnimalViewmodel.saveChangesUiState.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Error }
                ensureAllEventsConsumed()
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given a new non human animal_when the app creates them but fails updating the cache_then the non human animal is created in the local and remote repositories and logE is called`() =
        runTest {
            val createNonHumanAnimalViewmodel = getCreateNonHumanAnimalViewmodel(
                rowIdAfterCreatingLocalCacheForNonHumanAnimalArg = 0
            )
            createNonHumanAnimalViewmodel.saveNonHumanAnimalChanges(nonHumanAnimal)
            createNonHumanAnimalViewmodel.saveChangesUiState.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Success }
                ensureAllEventsConsumed()
            }

            runCurrent()

            verify {
                log.e(
                    "CreateNonHumanAnimalViewModel",
                    "Error creating $nonHumanAnimalId in local cache in section ${Section.NON_HUMAN_ANIMALS}"
                )
            }
        }
}
