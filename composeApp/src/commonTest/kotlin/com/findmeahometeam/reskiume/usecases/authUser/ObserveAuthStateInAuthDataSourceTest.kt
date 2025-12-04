package com.findmeahometeam.reskiume.usecases.authUser

import app.cash.turbine.test
import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.authUser
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.domain.usecases.authUser.ObserveAuthStateInAuthDataSource
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ObserveAuthStateInAuthDataSourceTest: CoroutineTestDispatcher() {

    val authRepository: AuthRepository = mock {
        everySuspend { authState } returns flowOf(authUser)
    }

    private val observeAuthStateInAuthDataSource =
        ObserveAuthStateInAuthDataSource(authRepository)

    @Test
    fun `given an auth user_when the app request it in the auth data source_then it retrieves it`() =
        runTest {
            observeAuthStateInAuthDataSource().test {
                val actualAuthUser = awaitItem()
                assertEquals(authUser, actualAuthUser)
                awaitComplete()
            }
        }
}
