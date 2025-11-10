package com.findmeahometeam.reskiume.usecases

import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.domain.usecases.ModifyUserEmailInAuthDataSource
import com.findmeahometeam.reskiume.userPwd
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class ModifyUserEmailInAuthDataSourceTest {

    val authRepository: AuthRepository = mock {
        everySuspend { updateUserEmail(userPwd, "newEmail@email.com", any()) } returns Unit
    }

    private val modifyUserEmailInAuthDataSource =
        ModifyUserEmailInAuthDataSource(authRepository)

    @Test
    fun `given a new user pwd_when the app updates it in the auth data source_then it calls to updateUserEmail`() =
        runTest {
            modifyUserEmailInAuthDataSource(userPwd, "newEmail@email.com", {})
            verifySuspend {
                authRepository.updateUserEmail(userPwd, "newEmail@email.com", any())
            }
        }
}
