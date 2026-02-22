package com.findmeahometeam.reskiume.ui.integrationTests.profile

import app.cash.turbine.test
import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.authUser
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
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeAuthRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeLocalCacheRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeLocalNonHumanAnimalRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeLog
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeManageImagePath
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeRealtimeDatabaseRemoteNonHumanAnimalRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeStorageRepository
import com.findmeahometeam.reskiume.ui.profile.createNonHumanAnimal.CreateNonHumanAnimalViewmodel
import com.findmeahometeam.reskiume.ui.util.ManageImagePath
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

class CreateNonHumanAnimalViewmodelIntegrationTest : CoroutineTestDispatcher() {

    private fun getCreateNonHumanAnimalViewmodel(
        authRepository: AuthRepository = FakeAuthRepository(authUser = authUser),
        localCacheRepository: LocalCacheRepository = FakeLocalCacheRepository(),
        realtimeDatabaseRemoteNonHumanAnimalRepository: RealtimeDatabaseRemoteNonHumanAnimalRepository = FakeRealtimeDatabaseRemoteNonHumanAnimalRepository(),
        manageImagePath: ManageImagePath = FakeManageImagePath(),
        storageRepository: StorageRepository = FakeStorageRepository(),
        localNonHumanAnimalRepository: LocalNonHumanAnimalRepository = FakeLocalNonHumanAnimalRepository(),
        log: Log = FakeLog()
    ): CreateNonHumanAnimalViewmodel {

        val observeAuthStateInAuthDataSource =
            ObserveAuthStateInAuthDataSource(authRepository)

        val uploadImageToRemoteDataSource =
            UploadImageToRemoteDataSource(storageRepository)

        val insertNonHumanAnimalInRemoteRepository =
            InsertNonHumanAnimalInRemoteRepository(realtimeDatabaseRemoteNonHumanAnimalRepository)

        val insertNonHumanAnimalInLocalRepository =
            InsertNonHumanAnimalInLocalRepository(manageImagePath, localNonHumanAnimalRepository, authRepository)

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
}
