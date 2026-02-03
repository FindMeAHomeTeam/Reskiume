package com.findmeahometeam.reskiume.usecases.fosterHome

import com.findmeahometeam.reskiume.domain.repository.local.LocalFosterHomeRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.DeleteAllMyFosterHomesFromLocalRepository
import com.findmeahometeam.reskiume.fosterHome
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class DeleteAllMyFosterHomesFromLocalRepositoryTest {

    private val localFosterHomeRepository: LocalFosterHomeRepository = mock {
        everySuspend {
            deleteAllMyFosterHomes(fosterHome.ownerId, any())
        } returns Unit
    }

    private val deleteAllMyFosterHomesFromLocalRepository =
        DeleteAllMyFosterHomesFromLocalRepository(localFosterHomeRepository)

    @Test
    fun `given my own local foster homes_when the app deletes them on account deletion_then deleteAllMyFosterHomes is called`() =
        runTest {
            deleteAllMyFosterHomesFromLocalRepository(fosterHome.ownerId) {}
            verifySuspend {
                localFosterHomeRepository.deleteAllMyFosterHomes(fosterHome.ownerId, any())
            }
        }
}
