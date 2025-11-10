package com.findmeahometeam.reskiume.usecases

import com.findmeahometeam.reskiume.domain.repository.remote.database.RealtimeDatabaseRepository
import com.findmeahometeam.reskiume.domain.usecases.ModifyUserFromRemoteDataSource
import com.findmeahometeam.reskiume.user
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class ModifyUserFromRemoteDataSourceTest {

    val realtimeDatabaseRepository: RealtimeDatabaseRepository = mock {
        everySuspend { updateRemoteUser(user.toData(), any()) } returns Unit
    }

    private val modifyUserFromRemoteDataSource =
        ModifyUserFromRemoteDataSource(realtimeDatabaseRepository)

    @Test
    fun `given a user_when the app updates it in the remote data source_then it calls to updateRemoteUser`() =
        runTest {
            modifyUserFromRemoteDataSource(user, {})
            verifySuspend {
                realtimeDatabaseRepository.updateRemoteUser(user.toData(), any())
            }
        }
}
