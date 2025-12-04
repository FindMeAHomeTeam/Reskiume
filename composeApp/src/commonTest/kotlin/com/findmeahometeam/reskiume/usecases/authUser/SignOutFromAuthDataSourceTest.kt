package com.findmeahometeam.reskiume.usecases.authUser

import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.domain.usecases.authUser.SignOutFromAuthDataSource
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class SignOutFromAuthDataSourceTest {

    val authRepository: AuthRepository = mock {
        everySuspend { signOut() } returns true
    }

    private val signOutFromAuthDataSource =
        SignOutFromAuthDataSource(authRepository)

    @Test
    fun `given a user_when that user signs out in the auth data source_then it calls to signOut`() =
        runTest {
            signOutFromAuthDataSource()
            verifySuspend {
                authRepository.signOut()
            }
        }
}
