package com.findmeahometeam.reskiume.usecases

import app.cash.turbine.test
import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.domain.model.User
import com.findmeahometeam.reskiume.domain.repository.remote.database.RealtimeDatabaseRepository
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

    val realtimeDatabaseRepository: RealtimeDatabaseRepository = mock {
        everySuspend { getRemoteUser(user.uid) } returns flowOf(user.toData())
    }

    private val getUserFromRemoteDataSource =
        GetUserFromRemoteDataSource(realtimeDatabaseRepository)

    @Test
    fun `given a user uid_when the app request a user by its uid from the remote data source_then it retrieves it`() =
        runTest {
            val expectedUser = user.copy(email = null, lastLogout = 0L)
            getUserFromRemoteDataSource(user.uid).test {
                val actualUser: User? = awaitItem()
                assertNotNull(actualUser)
                assertEquals(expectedUser, actualUser)
                awaitComplete()
            }
        }
}
