package com.findmeahometeam.reskiume.usecases.fosterHome

import com.findmeahometeam.reskiume.domain.repository.local.LocalFosterHomeRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.DeleteAllMyFosterHomesFromLocalRepository
import com.findmeahometeam.reskiume.user
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
            deleteAllMyFosterHomes(user.uid, any())
        } returns Unit
    }

    private val deleteAllMyFosterHomesFromLocalRepository =
        DeleteAllMyFosterHomesFromLocalRepository(localFosterHomeRepository)

    @Test
    fun `given local foster home_when the app deletes them on account deletion_then deleteAllMyFosterHomes is called`() =
        runTest {
            deleteAllMyFosterHomesFromLocalRepository(user.uid, {})
            verifySuspend {
                localFosterHomeRepository.deleteAllMyFosterHomes(user.uid, any())
            }
        }
}
