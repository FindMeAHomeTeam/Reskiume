package com.findmeahometeam.reskiume.usecases

import com.findmeahometeam.reskiume.domain.repository.remote.database.RealtimeDatabaseRepository
import com.findmeahometeam.reskiume.domain.usecases.InsertUserToRemoteDataSource
import com.findmeahometeam.reskiume.user
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class InsertUserToRemoteDataSourceTest {

    val realtimeDatabaseRepository: RealtimeDatabaseRepository = mock {
        everySuspend { insertRemoteUser(user.toData(), any()) } returns Unit
    }

    private val insertUserToRemoteDataSource =
        InsertUserToRemoteDataSource(realtimeDatabaseRepository)

    @Test
    fun `given a user_when the app saves it in the remote data source_then it calls to insertRemoteUser`() =
        runTest {
            insertUserToRemoteDataSource(user, {})
            verifySuspend {
                realtimeDatabaseRepository.insertRemoteUser(user.toData(), any())
            }
        }
}
