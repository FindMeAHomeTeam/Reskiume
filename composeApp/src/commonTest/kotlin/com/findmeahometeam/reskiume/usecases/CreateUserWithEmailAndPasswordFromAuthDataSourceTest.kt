package com.findmeahometeam.reskiume.usecases

import com.findmeahometeam.reskiume.authUser
import com.findmeahometeam.reskiume.data.remote.response.AuthResult
import com.findmeahometeam.reskiume.data.remote.response.AuthResult.Success
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.domain.usecases.CreateUserWithEmailAndPasswordFromAuthDataSource
import com.findmeahometeam.reskiume.user
import com.findmeahometeam.reskiume.userPwd
import com.findmeahometeam.reskiume.wrongEmail
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class CreateUserWithEmailAndPasswordFromAuthDataSourceTest {

    val authRepository: AuthRepository = mock {
        everySuspend { createUserWithEmailAndPassword(user.email!!, userPwd) } returns Success(
            authUser
        )
        everySuspend {
            createUserWithEmailAndPassword(
                wrongEmail,
                userPwd
            )
        } returns AuthResult.Error(message = "The email address is badly formatted.")
    }

    private val createUserWithEmailAndPasswordFromAuthDataSource =
        CreateUserWithEmailAndPasswordFromAuthDataSource(authRepository)

    @Test
    fun `given a correct email_when the user creates a new account_then createUserWithEmailAndPassword is called`() =
        runTest {
            createUserWithEmailAndPasswordFromAuthDataSource(user.email!!, userPwd)
            verifySuspend {
                authRepository.createUserWithEmailAndPassword(user.email, userPwd)
            }
        }

    @Test
    fun `given a correct email_when the user creates a new account_then createUserWithEmailAndPassword returns an AuthUser`() =
        runTest {
            val result = createUserWithEmailAndPasswordFromAuthDataSource(user.email!!, userPwd)
            assertEquals(expected = Success(authUser), actual = result)
        }

    @Test
    fun `given an incorrect email_when the user creates a new account_then createUserWithEmailAndPassword returns an error`() =
        runTest {
            val result = createUserWithEmailAndPasswordFromAuthDataSource(wrongEmail, userPwd)
            assertEquals(
                expected = AuthResult.Error(message = "The email address is badly formatted."),
                actual = result
            )
        }
}
