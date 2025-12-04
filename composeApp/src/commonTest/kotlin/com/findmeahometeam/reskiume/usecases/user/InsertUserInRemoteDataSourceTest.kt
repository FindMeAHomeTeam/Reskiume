package com.findmeahometeam.reskiume.usecases.user

import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteUser.RealtimeDatabaseRemoteUserRepository
import com.findmeahometeam.reskiume.domain.usecases.user.InsertUserInRemoteDataSource
import com.findmeahometeam.reskiume.user
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class InsertUserInRemoteDataSourceTest {

    val realtimeDatabaseRemoteUserRepository: RealtimeDatabaseRemoteUserRepository = mock {
        everySuspend { insertRemoteUser(user.toData(), any()) } returns Unit
    }

    private val insertUserInRemoteDataSource =
        InsertUserInRemoteDataSource(realtimeDatabaseRemoteUserRepository)

    @Test
    fun `given a user_when the app saves it in the remote data source_then it calls to insertRemoteUser`() =
        runTest {
            insertUserInRemoteDataSource(user, {})
            verifySuspend {
                realtimeDatabaseRemoteUserRepository.insertRemoteUser(user.toData(), any())
            }
        }
}
