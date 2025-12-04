package com.findmeahometeam.reskiume.usecases.user

import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteUser.RealtimeDatabaseRemoteUserRepository
import com.findmeahometeam.reskiume.domain.usecases.user.DeleteUserFromRemoteDataSource
import com.findmeahometeam.reskiume.user
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class DeleteUserFromRemoteDataSourceTest {

    val realtimeDatabaseRemoteUserRepository: RealtimeDatabaseRemoteUserRepository = mock {
        everySuspend { deleteRemoteUser(user.uid, any()) } returns Unit
    }

    private val deleteUserFromRemoteDataSource =
        DeleteUserFromRemoteDataSource(realtimeDatabaseRemoteUserRepository)

    @Test
    fun `given a remote user_when the app deletes it_then it calls to deleteRemoteUser`() =
        runTest {
            deleteUserFromRemoteDataSource(user.uid, {})
            verifySuspend {
                realtimeDatabaseRemoteUserRepository.deleteRemoteUser(user.uid, any())
            }
        }
}
