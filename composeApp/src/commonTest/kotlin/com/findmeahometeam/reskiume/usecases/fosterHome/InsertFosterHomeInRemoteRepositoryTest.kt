package com.findmeahometeam.reskiume.usecases.fosterHome

import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteFosterHome.FireStoreRemoteFosterHomeRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.InsertFosterHomeInRemoteRepository
import com.findmeahometeam.reskiume.fosterHome
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class InsertFosterHomeInRemoteRepositoryTest {

    private val fireStoreRemoteFosterHomeRepository: FireStoreRemoteFosterHomeRepository = mock {
        everySuspend {
            insertRemoteFosterHome(fosterHome.toData(), any())
        } returns Unit
    }

    private val insertFosterHomeInRemoteRepository =
        InsertFosterHomeInRemoteRepository(fireStoreRemoteFosterHomeRepository)

    @Test
    fun `given a remote foster home_when the app inserts it_then insertRemoteFosterHome is called`() =
        runTest {
            insertFosterHomeInRemoteRepository(fosterHome) {}
            verifySuspend {
                fireStoreRemoteFosterHomeRepository.insertRemoteFosterHome(
                    fosterHome.toData(),
                    any()
                )
            }
        }
}
