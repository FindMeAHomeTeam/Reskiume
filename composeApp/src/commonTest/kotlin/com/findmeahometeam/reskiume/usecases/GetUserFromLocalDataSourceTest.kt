package com.findmeahometeam.reskiume.usecases

import com.findmeahometeam.reskiume.domain.repository.local.LocalUserRepository
import com.findmeahometeam.reskiume.domain.usecases.GetUserFromLocalDataSource
import com.findmeahometeam.reskiume.user
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetUserFromLocalDataSourceTest {

    val localUserRepository: LocalUserRepository = mock {
        everySuspend { getUser(user.uid) } returns user
    }

    private val getUserFromLocalDataSource =
        GetUserFromLocalDataSource(localUserRepository)

    @Test
    fun `given a user uid_when the app request a user from its uid in the local database_then it retrieves it`() =
        runTest {
            val actualUser = getUserFromLocalDataSource(user.uid)
            assertEquals(user, actualUser)
        }
}
