package com.findmeahometeam.reskiume.usecases.authUser

import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.domain.usecases.authUser.ModifyUserPasswordInAuthDataSource
import com.findmeahometeam.reskiume.userPwd
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class ModifyUserPasswordInAuthDataSourceTest {

    val authRepository: AuthRepository = mock {
        everySuspend { updateUserPassword(userPwd, "newPwd", any()) } returns Unit
    }

    private val modifyUserPasswordInAuthDataSource =
        ModifyUserPasswordInAuthDataSource(authRepository)

    @Test
    fun `given a user pwd_when the app updates it in the auth data source_then it calls to updateUserPassword`() =
        runTest {
            modifyUserPasswordInAuthDataSource(userPwd, "newPwd", {})
            verifySuspend {
                authRepository.updateUserPassword(userPwd, "newPwd", any())
            }
        }
}
