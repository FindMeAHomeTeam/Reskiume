package com.findmeahometeam.reskiume.usecases.user

import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteUser.RealtimeDatabaseRemoteUserRepository
import com.findmeahometeam.reskiume.domain.usecases.user.ModifyUserInRemoteDataSource
import com.findmeahometeam.reskiume.user
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class ModifyUserInRemoteDataSourceTest {

    val realtimeDatabaseRemoteUserRepository: RealtimeDatabaseRemoteUserRepository = mock {
        everySuspend { updateRemoteUser(user.toData(), any()) } returns Unit
    }

    private val modifyUserInRemoteDataSource =
        ModifyUserInRemoteDataSource(realtimeDatabaseRemoteUserRepository)

    @Test
    fun `given a user_when the app updates it in the remote data source_then it calls to updateRemoteUser`() =
        runTest {
            modifyUserInRemoteDataSource(user, {})
            verifySuspend {
                realtimeDatabaseRemoteUserRepository.updateRemoteUser(user.toData(), any())
            }
        }
}
