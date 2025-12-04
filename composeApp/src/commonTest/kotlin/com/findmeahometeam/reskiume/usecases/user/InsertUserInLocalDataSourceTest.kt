package com.findmeahometeam.reskiume.usecases.user

import com.findmeahometeam.reskiume.authUser
import com.findmeahometeam.reskiume.domain.repository.local.LocalUserRepository
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.domain.usecases.user.InsertUserInLocalDataSource
import com.findmeahometeam.reskiume.user
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class InsertUserInLocalDataSourceTest {

    val localUserRepository: LocalUserRepository = mock {
        everySuspend { insertUser(user, any()) } returns Unit
    }

    val authRepository: AuthRepository = mock {
        everySuspend { authState } returns flowOf(authUser)
    }

    private val insertUserInLocalDataSource =
        InsertUserInLocalDataSource(localUserRepository, authRepository)

    @Test
    fun `given a user_when the app saves it in the local data source_then it calls to insertUser`() =
        runTest {
            insertUserInLocalDataSource(user, {})
            verifySuspend {
                localUserRepository.insertUser(user, any())
            }
        }
}
