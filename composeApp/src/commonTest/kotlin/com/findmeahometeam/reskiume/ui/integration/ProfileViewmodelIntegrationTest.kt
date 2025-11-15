package com.findmeahometeam.reskiume.ui.integration

import app.cash.turbine.test
import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.authUser
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.repository.local.LocalRepository
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.domain.usecases.GetUserFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.ObserveAuthStateFromAuthDataSource
import com.findmeahometeam.reskiume.ui.integration.fakes.FakeAuthRepository
import com.findmeahometeam.reskiume.ui.integration.fakes.FakeLocalRepository
import com.findmeahometeam.reskiume.ui.integration.fakes.FakeLog
import com.findmeahometeam.reskiume.ui.profile.ProfileViewmodel
import com.findmeahometeam.reskiume.ui.profile.ProfileViewmodel.ProfileUiState
import com.findmeahometeam.reskiume.user
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ProfileViewmodelIntegrationTest : CoroutineTestDispatcher() {

    private val log: Log = FakeLog()

    private fun getProfileViewmodel(
        authRepository: AuthRepository = FakeAuthRepository(),
        localRepository: LocalRepository = FakeLocalRepository()
    ): ProfileViewmodel {
        val observeAuthStateFromAuthDataSource = ObserveAuthStateFromAuthDataSource(authRepository)
        val getUserFromLocalDataSource = GetUserFromLocalDataSource(localRepository)
        return ProfileViewmodel(
            observeAuthStateFromAuthDataSource,
            getUserFromLocalDataSource,
            log
        )
    }

    @Test
    fun `given a registered user_when the user opens the profile section_then that user will see it populated`() =
        runTest {
            val authRepository: AuthRepository = FakeAuthRepository(authUser = authUser)
            val localRepository: LocalRepository = FakeLocalRepository(mutableListOf(user))
            getProfileViewmodel(authRepository, localRepository).state.test {
                assertEquals(ProfileUiState.Success(user), awaitItem())
                awaitComplete()
            }
        }

    @Test
    fun `given a registered user on auth but not on the local data source_when the user opens the profile section_then that user will see the default profile`() =
        runTest {
            val authRepository: AuthRepository = FakeAuthRepository(authUser = authUser.copy(uid = "wrongUid"))
            val localRepository: LocalRepository = FakeLocalRepository(mutableListOf(user))
            getProfileViewmodel(authRepository, localRepository).state.test {
                assertEquals(
                    ProfileUiState.Error("ProfileViewmodel - User data not found"),
                    awaitItem()
                )
                awaitComplete()
            }
        }

    @Test
    fun `given an unregistered user_when the user opens the profile section_then that user will see the default profile`() =
        runTest {
            getProfileViewmodel().state.test {
                assertTrue { awaitItem() is ProfileUiState.Idle }
                awaitComplete()
            }
        }
}
