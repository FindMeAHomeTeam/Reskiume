package com.findmeahometeam.reskiume.usecases

import com.findmeahometeam.reskiume.authUser
import com.findmeahometeam.reskiume.data.remote.response.AuthResult
import com.findmeahometeam.reskiume.data.remote.response.AuthResult.Success
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.domain.usecases.SignInWithEmailAndPasswordFromAuthDataSource
import com.findmeahometeam.reskiume.user
import com.findmeahometeam.reskiume.userPwd
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SignInWithEmailAndPasswordFromAuthDataSourceTest {

    val authRepository: AuthRepository = mock {
        everySuspend { signInWithEmailAndPassword(user.email!!, userPwd) } returns Success(
            authUser
        )
        everySuspend {
            signInWithEmailAndPassword(
                user.email!!,
                "wrongPwd"
            )
        } returns AuthResult.Error(message = "The password is incorrect.")
    }

    private val signInWithEmailAndPasswordFromAuthDataSource =
        SignInWithEmailAndPasswordFromAuthDataSource(authRepository)

    @Test
    fun `given a correct email and pwd_when the user signs in_then signInWithEmailAndPassword is called`() =
        runTest {
            signInWithEmailAndPasswordFromAuthDataSource(user.email!!, userPwd)
            verifySuspend {
                authRepository.signInWithEmailAndPassword(user.email, userPwd)
            }
        }

    @Test
    fun `given a correct email and pwd_when the user signs in_then signInWithEmailAndPassword returns an AuthUser`() =
        runTest {
            val result = signInWithEmailAndPasswordFromAuthDataSource(user.email!!, userPwd)
            assertEquals(expected = Success(authUser), actual = result)
        }

    @Test
    fun `given an correct email and incorrect pwd_when the user signs in_then signInWithEmailAndPassword returns an error`() =
        runTest {
            val result = signInWithEmailAndPasswordFromAuthDataSource(user.email!!, "wrongPwd")
            assertEquals(
                expected = AuthResult.Error(message = "The password is incorrect."),
                actual = result
            )
        }
}
