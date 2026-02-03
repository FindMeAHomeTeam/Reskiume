package com.findmeahometeam.reskiume.usecases.fosterHome

import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteFosterHome.FireStoreRemoteFosterHomeRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.DeleteFosterHomeFromRemoteRepository
import com.findmeahometeam.reskiume.fosterHome
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class DeleteFosterHomeFromRemoteRepositoryTest {

    private val fireStoreRemoteFosterHomeRepository: FireStoreRemoteFosterHomeRepository = mock {
        everySuspend {
            deleteRemoteFosterHome(fosterHome.id, fosterHome.ownerId, any())
        } returns Unit
    }

    private val deleteFosterHomeFromRemoteRepository =
        DeleteFosterHomeFromRemoteRepository(fireStoreRemoteFosterHomeRepository)

    @Test
    fun `given a remote foster home_when the app deletes it_then deleteRemoteFosterHome is called`() =
        runTest {
            deleteFosterHomeFromRemoteRepository(fosterHome.id, fosterHome.ownerId) {}
            verifySuspend {
                fireStoreRemoteFosterHomeRepository.deleteRemoteFosterHome(fosterHome.id, fosterHome.ownerId, any())
            }
        }
}
