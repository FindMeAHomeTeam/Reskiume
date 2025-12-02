package com.findmeahometeam.reskiume.usecases

import app.cash.turbine.test
import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.domain.model.User
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteUser.RealtimeDatabaseRemoteUserRepository
import com.findmeahometeam.reskiume.domain.usecases.GetUserFromRemoteDataSource
import com.findmeahometeam.reskiume.user
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class GetUserFromRemoteDataSourceTest: CoroutineTestDispatcher() {

    val realtimeDatabaseRemoteUserRepository: RealtimeDatabaseRemoteUserRepository = mock {
        everySuspend { getRemoteUser(user.uid) } returns flowOf(user.toData())
    }

    private val getUserFromRemoteDataSource =
        GetUserFromRemoteDataSource(realtimeDatabaseRemoteUserRepository)

    @Test
    fun `given a user uid_when the app request a user by its uid from the remote data source_then it retrieves it`() =
        runTest {
            val expectedUser = user.copy(savedBy = "", email = null)
            getUserFromRemoteDataSource(user.uid).test {
                val actualUser: User? = awaitItem()
                assertNotNull(actualUser)
                assertEquals(expectedUser, actualUser)
                awaitComplete()
            }
        }
}
