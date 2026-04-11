package com.findmeahometeam.reskiume.usecases.user

import app.cash.turbine.test
import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.domain.repository.local.LocalUserRepository
import com.findmeahometeam.reskiume.domain.usecases.user.GetAllUsersFromLocalDataSource
import com.findmeahometeam.reskiume.user
import com.findmeahometeam.reskiume.userWithAllSubscriptionData
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetAllUsersFromLocalDataSourceTest: CoroutineTestDispatcher() {

    val localUserRepository: LocalUserRepository = mock {
        everySuspend { getAllUsers() } returns flowOf(listOf(userWithAllSubscriptionData))
    }

    private val getAllUsersFromLocalDataSource =
        GetAllUsersFromLocalDataSource(localUserRepository)

    @Test
    fun `given users_when the app request all the local users from the local database_then it retrieves them`() =
        runTest {
            getAllUsersFromLocalDataSource().test {

                assertEquals(listOf(user.copy(email = null)), awaitItem())
                awaitComplete()
            }
        }
}
