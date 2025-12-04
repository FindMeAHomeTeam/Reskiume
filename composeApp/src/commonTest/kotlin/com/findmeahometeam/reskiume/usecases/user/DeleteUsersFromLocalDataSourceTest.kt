package com.findmeahometeam.reskiume.usecases.user

import com.findmeahometeam.reskiume.domain.repository.local.LocalUserRepository
import com.findmeahometeam.reskiume.domain.usecases.user.DeleteUsersFromLocalDataSource
import com.findmeahometeam.reskiume.user
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class DeleteUsersFromLocalDataSourceTest {

    val localUserRepository: LocalUserRepository = mock {
        everySuspend { deleteUsers(user.uid, any()) } returns Unit
    }

    private val deleteUsersFromLocalDataSource =
        DeleteUsersFromLocalDataSource(localUserRepository)

    @Test
    fun `given a local user_when the app deletes it_then it calls to deleteUser`() =
        runTest {
            deleteUsersFromLocalDataSource(user.uid, {})
            verifySuspend {
                localUserRepository.deleteUsers(user.uid, any())
            }
        }
}
