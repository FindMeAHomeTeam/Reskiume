package com.findmeahometeam.reskiume.ui.unitTests.home

import app.cash.turbine.test
import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.authUser
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.repository.local.LocalUserRepository
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.domain.usecases.user.GetUserFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.authUser.ObserveAuthStateInAuthDataSource
import com.findmeahometeam.reskiume.ui.core.components.UiState
import com.findmeahometeam.reskiume.ui.home.HomeViewmodel
import com.findmeahometeam.reskiume.user
import com.findmeahometeam.reskiume.userWithAllSubscriptionData
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

class HomeViewmodelTest : CoroutineTestDispatcher() {

    private val localUserRepository: LocalUserRepository = mock {
        everySuspend { getUser(user.uid) } returns flowOf(userWithAllSubscriptionData)
        everySuspend { getUser("wrongUid") } returns flowOf(null)
    }
    private val log: Log = mock {
        every { d(any(), any()) } returns Unit
        every { e(any(), any()) } returns Unit
    }

    private val getUserFromLocalDataSource = GetUserFromLocalDataSource(localUserRepository)

    private fun getHomeViewmodel(authRepository: AuthRepository): HomeViewmodel {
        val observeAuthStateInAuthDataSource = ObserveAuthStateInAuthDataSource(authRepository)
        return HomeViewmodel(
            observeAuthStateInAuthDataSource,
            log,
            getUserFromLocalDataSource
        )
    }

    @Test
    fun `given a registered user_when the user opens the app_then that user can see the chats section`() =
        runTest {
            val authRepository: AuthRepository = mock {
                everySuspend { authState } returns (flowOf(authUser))
            }
            getHomeViewmodel(authRepository).userState.test {
                assertTrue { awaitItem() is UiState.Success }
                awaitComplete()
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `given a registered user on auth but not on the local data source_when the user opens the app_then that user will see the default sections`() =
        runTest {
            val authRepository: AuthRepository = mock {
                everySuspend { authState } returns (flowOf(authUser.copy(uid = "wrongUid")))
            }
            getHomeViewmodel(authRepository).userState.test {
                assertTrue { awaitItem() is UiState.Idle }
                awaitComplete()
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `given an unregistered user_when the user opens the app_then that user will see the default sections`() =
        runTest {
            val authRepository: AuthRepository = mock {
                every { authState } returns flowOf(null)
            }
            getHomeViewmodel(authRepository).userState.test {
                assertTrue { awaitItem() is UiState.Idle }
                awaitComplete()
                ensureAllEventsConsumed()
            }
        }
}
