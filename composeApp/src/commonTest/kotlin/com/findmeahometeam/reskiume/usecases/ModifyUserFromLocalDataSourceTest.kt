package com.findmeahometeam.reskiume.usecases

import com.findmeahometeam.reskiume.domain.repository.local.LocalUserRepository
import com.findmeahometeam.reskiume.domain.usecases.ModifyUserFromLocalDataSource
import com.findmeahometeam.reskiume.user
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class ModifyUserFromLocalDataSourceTest {

    val localUserRepository: LocalUserRepository = mock {
        everySuspend { modifyUser(user, any()) } returns Unit
    }

    private val modifyUserFromLocalDataSource =
        ModifyUserFromLocalDataSource(localUserRepository)

    @Test
    fun `given a user_when the app updates it in the local data source_then it calls to modifyUser`() =
        runTest {
            modifyUserFromLocalDataSource(user, {})
            verifySuspend {
                localUserRepository.modifyUser(user, any())
            }
        }
}
