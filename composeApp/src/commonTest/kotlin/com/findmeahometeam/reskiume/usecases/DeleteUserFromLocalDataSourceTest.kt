package com.findmeahometeam.reskiume.usecases

import com.findmeahometeam.reskiume.domain.repository.local.LocalUserRepository
import com.findmeahometeam.reskiume.domain.usecases.DeleteUserFromLocalDataSource
import com.findmeahometeam.reskiume.user
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class DeleteUserFromLocalDataSourceTest {

    val localUserRepository: LocalUserRepository = mock {
        everySuspend { deleteUser(user.uid, any()) } returns Unit
    }

    private val deleteUserFromLocalDataSource =
        DeleteUserFromLocalDataSource(localUserRepository)

    @Test
    fun `given a local user_when the app deletes it_then it calls to deleteUser`() =
        runTest {
            deleteUserFromLocalDataSource(user.uid, {})
            verifySuspend {
                localUserRepository.deleteUser(user.uid, any())
            }
        }
}
