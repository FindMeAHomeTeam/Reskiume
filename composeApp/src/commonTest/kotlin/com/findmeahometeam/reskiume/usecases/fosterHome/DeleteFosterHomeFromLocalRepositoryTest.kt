package com.findmeahometeam.reskiume.usecases.fosterHome

import com.findmeahometeam.reskiume.domain.repository.local.LocalFosterHomeRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.DeleteFosterHomeFromLocalRepository
import com.findmeahometeam.reskiume.fosterHome
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class DeleteFosterHomeFromLocalRepositoryTest {

    private val localFosterHomeRepository: LocalFosterHomeRepository = mock {
        everySuspend {
            deleteFosterHome(fosterHome.id, any())
        } returns Unit
    }

    private val deleteFosterHomeFromLocalRepository =
        DeleteFosterHomeFromLocalRepository(localFosterHomeRepository)

    @Test
    fun `given a local foster home_when the app deletes it_then deleteFosterHome is called`() =
        runTest {
            deleteFosterHomeFromLocalRepository(fosterHome.id, {})
            verifySuspend {
                localFosterHomeRepository.deleteFosterHome(fosterHome.id, any())
            }
        }
}
