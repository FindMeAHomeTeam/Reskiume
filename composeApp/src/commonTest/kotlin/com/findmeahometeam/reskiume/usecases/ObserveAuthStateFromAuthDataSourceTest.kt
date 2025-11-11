package com.findmeahometeam.reskiume.usecases

import app.cash.turbine.test
import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.authUser
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.domain.usecases.ObserveAuthStateFromAuthDataSource
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ObserveAuthStateFromAuthDataSourceTest: CoroutineTestDispatcher() {

    val authRepository: AuthRepository = mock {
        everySuspend { authState } returns flowOf(authUser)
    }

    private val observeAuthStateFromAuthDataSource =
        ObserveAuthStateFromAuthDataSource(authRepository)

    @Test
    fun `given an auth user_when the app request it in the auth data source_then it retrieves it`() =
        runTest {
            observeAuthStateFromAuthDataSource().test {
                val actualAuthUser = awaitItem()
                assertEquals(authUser, actualAuthUser)
                awaitComplete()
            }
        }
}
