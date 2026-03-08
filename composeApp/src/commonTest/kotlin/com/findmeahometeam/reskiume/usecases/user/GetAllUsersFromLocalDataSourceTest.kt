package com.findmeahometeam.reskiume.usecases.user

import com.findmeahometeam.reskiume.domain.repository.local.LocalUserRepository
import com.findmeahometeam.reskiume.domain.usecases.user.GetAllUsersFromLocalDataSource
import com.findmeahometeam.reskiume.user
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetAllUsersFromLocalDataSourceTest {

    val localUserRepository: LocalUserRepository = mock {
        everySuspend { getAllUsers() } returns listOf(user.toEntity())
    }

    private val getAllUsersFromLocalDataSource =
        GetAllUsersFromLocalDataSource(localUserRepository)

    @Test
    fun `given users_when the app request all the local users from the local database_then it retrieves them`() =
        runTest {
            val actualUser = getAllUsersFromLocalDataSource()
            assertEquals(listOf(user.copy(email = null)), actualUser)
        }
}
