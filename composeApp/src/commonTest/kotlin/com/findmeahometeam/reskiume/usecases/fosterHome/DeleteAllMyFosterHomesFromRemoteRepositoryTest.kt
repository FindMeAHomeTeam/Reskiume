package com.findmeahometeam.reskiume.usecases.fosterHome

import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteFosterHome.FireStoreRemoteFosterHomeRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.DeleteAllMyFosterHomesFromRemoteRepository
import com.findmeahometeam.reskiume.fosterHome
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class DeleteAllMyFosterHomesFromRemoteRepositoryTest {

    private val fireStoreRemoteFosterHomeRepository: FireStoreRemoteFosterHomeRepository = mock {
        everySuspend {
            deleteAllMyRemoteFosterHomes(fosterHome.ownerId, any())
        } returns Unit
    }

    private val deleteAllMyFosterHomesFromRemoteRepository =
        DeleteAllMyFosterHomesFromRemoteRepository(fireStoreRemoteFosterHomeRepository)

    @Test
    fun `given my own remote foster homes_when the app deletes them on account deletion_then deleteAllMyRemoteFosterHomes is called`() =
        runTest {
            deleteAllMyFosterHomesFromRemoteRepository(fosterHome.ownerId) {}
            verifySuspend {
                fireStoreRemoteFosterHomeRepository.deleteAllMyRemoteFosterHomes(fosterHome.ownerId, any())
            }
        }
}
