package com.findmeahometeam.reskiume.ui.integration

import app.cash.turbine.test
import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.authUser
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.repository.local.LocalRepository
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.domain.usecases.GetUserFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.ObserveAuthStateFromAuthDataSource
import com.findmeahometeam.reskiume.ui.core.components.UiState
import com.findmeahometeam.reskiume.ui.home.HomeViewmodel
import com.findmeahometeam.reskiume.ui.integration.fakes.FakeAuthRepository
import com.findmeahometeam.reskiume.ui.integration.fakes.FakeLocalRepository
import com.findmeahometeam.reskiume.ui.integration.fakes.FakeLog
import com.findmeahometeam.reskiume.user
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

class HomeViewmodelIntegrationTest : CoroutineTestDispatcher() {

    private val log: Log = FakeLog()

    private fun getHomeViewmodel(
        authRepository: AuthRepository = FakeAuthRepository(),
        localRepository: LocalRepository = FakeLocalRepository()
    ): HomeViewmodel {
        val observeAuthStateFromAuthDataSource = ObserveAuthStateFromAuthDataSource(authRepository)
        val getUserFromLocalDataSource = GetUserFromLocalDataSource(localRepository)
        return HomeViewmodel(
            observeAuthStateFromAuthDataSource,
            log,
            getUserFromLocalDataSource
        )
    }

    @Test
    fun `given a registered user_when the user opens the app_then that user can see the chats section`() =
        runTest {
            val authRepository: AuthRepository = FakeAuthRepository(authUser = authUser)
            val localRepository: LocalRepository = FakeLocalRepository(mutableListOf(user))
            getHomeViewmodel(authRepository, localRepository).state.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Success }
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `given a registered user on auth but not on the local data source_when the user opens the app_then that user will see the default sections`() =
        runTest {
            val authRepository: AuthRepository =
                FakeAuthRepository(authUser = authUser.copy(uid = "wrongUid"))
            getHomeViewmodel(authRepository).state.test {
                assertTrue { awaitItem() is UiState.Idle }
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `given an unregistered user_when the user opens the app_then that user will see the default sections`() =
        runTest {
            getHomeViewmodel().state.test {
                assertTrue { awaitItem() is UiState.Idle }
                ensureAllEventsConsumed()
            }
        }
}
