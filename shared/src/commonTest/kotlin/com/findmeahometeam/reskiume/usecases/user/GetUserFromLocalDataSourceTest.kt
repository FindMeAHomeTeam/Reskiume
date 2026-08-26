package com.findmeahometeam.reskiume.usecases.user

import app.cash.turbine.test
import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.domain.repository.local.LocalUserRepository
import com.findmeahometeam.reskiume.domain.usecases.user.GetUserFromLocalDataSource
import com.findmeahometeam.reskiume.user
import com.findmeahometeam.reskiume.userWithAllSubscriptionData
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetUserFromLocalDataSourceTest: CoroutineTestDispatcher() {

    val localUserRepository: LocalUserRepository = mock {
        everySuspend { getUser(user.uid) } returns flowOf(userWithAllSubscriptionData)
    }

    private val getUserFromLocalDataSource =
        GetUserFromLocalDataSource(localUserRepository)

    @Test
    fun `given a user uid_when the app request a user from its uid in the local database_then it retrieves it`() =
        runTest {
            getUserFromLocalDataSource(user.uid).test {

                assertEquals(user.copy(email = null), awaitItem())
                awaitComplete()
            }
        }
}
