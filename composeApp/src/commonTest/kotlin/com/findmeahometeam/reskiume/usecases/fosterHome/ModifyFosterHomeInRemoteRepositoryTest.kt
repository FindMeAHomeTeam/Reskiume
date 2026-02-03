package com.findmeahometeam.reskiume.usecases.fosterHome

import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteFosterHome.FireStoreRemoteFosterHomeRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.ModifyFosterHomeInRemoteRepository
import com.findmeahometeam.reskiume.fosterHome
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class ModifyFosterHomeInRemoteRepositoryTest {

    private val fireStoreRemoteFosterHomeRepository: FireStoreRemoteFosterHomeRepository = mock {
        everySuspend {
            modifyRemoteFosterHome(fosterHome.toData(), any())
        } returns Unit
    }

    private val modifyFosterHomeInRemoteRepository =
        ModifyFosterHomeInRemoteRepository(fireStoreRemoteFosterHomeRepository)

    @Test
    fun `given a remote foster home_when the app modifies it_then modifyRemoteFosterHome is called`() =
        runTest {
            modifyFosterHomeInRemoteRepository(fosterHome) {}
            verifySuspend {
                fireStoreRemoteFosterHomeRepository.modifyRemoteFosterHome(
                    fosterHome.toData(),
                    any()
                )
            }
        }
}
