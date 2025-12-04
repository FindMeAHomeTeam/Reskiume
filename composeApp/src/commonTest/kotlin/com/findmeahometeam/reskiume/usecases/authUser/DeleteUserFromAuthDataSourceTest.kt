package com.findmeahometeam.reskiume.usecases.authUser

import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.domain.usecases.authUser.DeleteUserFromAuthDataSource
import com.findmeahometeam.reskiume.userPwd
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class DeleteUserFromAuthDataSourceTest {

    val authRepository: AuthRepository = mock {
        everySuspend { deleteUser(userPwd, any()) } returns Unit
    }

    private val deleteUserFromAuthDataSource =
        DeleteUserFromAuthDataSource(authRepository)

    @Test
    fun `given an auth user_when the app deletes it_then it calls to deleteUser`() =
        runTest {
            deleteUserFromAuthDataSource(userPwd, {})
            verifySuspend {
                authRepository.deleteUser(userPwd, any())
            }
        }
}
